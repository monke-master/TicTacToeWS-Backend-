package example.com.plugins

import example.com.data.*
import example.com.domain.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import qrcode.QRCode
import java.io.File
import java.io.FileOutputStream
import java.time.Duration
import javax.imageio.ImageIO

private val gameManager = GameManager()
private val connectionRepository = ConnectionRepository()

@OptIn(ExperimentalSerializationApi::class, InternalAPI::class)
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(150)
        timeout = Duration.ofSeconds(150)
        maxFrameSize = Long.MAX_VALUE
        masking = false

        contentConverter = KotlinxWebsocketSerializationConverter(getConverter())
    }
    routing {

        webSocket("/sessions/new") {
            val deviceId = call.parameters["did"] ?: return@webSocket
            println("new session")

            val player = Player(
                id = "1",
                type = randomType()
            )
            val game = createGame()
            val gameSession = GameSession(
                players = listOf(player),
                game = game,
                code = generateCode().uppercase()
            )

            val deviceSession = DeviceSession(deviceId, this)
            val connection = Connection(
                sessions = listOf(deviceSession),
                gameSession = gameSession
            )
            connectionRepository.addSession(connection)

            sendSerializedBase<ServerResponse>(ServerResponse.Success(gameSession))

            handleIncomeData(gameSession.code)
        }

        webSocket("/sessions/{code}") {
            val code = call.parameters["code"] ?: return@webSocket
            val deviceId = call.parameters["did"] ?: return@webSocket

            val connection = connectionRepository.getConnectionBySessionCode(code) ?: return@webSocket
            val session = connection.sessions.find { it.deviceId == deviceId }
            if (session != null) {
                reconnectSession(connection, session)
            } else {
                val deviceSession = DeviceSession(deviceId, this)
                val newConnection = connection.copy(
                    sessions = connection.sessions + deviceSession,
                    gameSession = connection.gameSession.addNewPlayer()
                )
                connectionRepository.updateConnection(newConnection, code)

                for (session in newConnection.sessions) {
                    session.wsSession.sendSerializedBase<ServerResponse>(ServerResponse.Success(newConnection.gameSession))
                }
            }
            handleIncomeData(code)
        }

        post("/sessions/{code}/restart") {
            val code = call.parameters["code"] ?: return@post

            val connection = connectionRepository.getConnectionBySessionCode(code) ?: return@post

            val newConnection = if (connection.gameSession.game.gameStatus == GameStatus.Waiting) {
                connection.copy(gameSession = connection.gameSession.startGame())
            } else {
                connection.copy(gameSession = connection.gameSession.restart())
            }

            connectionRepository.updateConnection(newConnection, code)
            newConnection.sessions.forEach {
                it.wsSession.sendSerializedBase<ServerResponse>(ServerResponse.Success(newConnection.gameSession))
            }
        }

        get("/qr/generate") {
            try {
                val code = call.parameters["code"] ?: return@get

                val helloWorld = QRCode.ofSquares()
                    .build(code)

                val pngBytes = helloWorld.render()
                call.respond(pngBytes.getBytes())
            } catch (e: Exception) {
                call.respond(400)
            }
        }
    }
}


suspend fun DefaultWebSocketServerSession.handleIncomeData(code: String) {
    try {
        for (frame in incoming) {
            println(frame)
            val text = (frame as? Frame.Text)?.readText() ?: continue
            println(text)
            val game = Json.decodeFromString<Game>(text)
            println(game)

            val connection = connectionRepository.getConnectionBySessionCode(code) ?: continue

            val newConnection: Connection = if (connection.gameSession.game.gameStatus == GameStatus.Started) {
                val endStatus = gameManager.checkForWin(game.field)

                if (endStatus != null) {
                    connection.copy(gameSession = connection.gameSession.setEndStatus(game, endStatus))
                } else {
                    connection.copy(gameSession = connection.gameSession.nextTurn(game))
                }
            } else {
                println("STARTING GAME")
                connection.copy(gameSession = connection.gameSession.copy(game = game))
            }

            connectionRepository.updateConnection(newConnection, code)
            newConnection.sessions.forEach {
                it.wsSession.sendSerializedBase<ServerResponse>(ServerResponse.Success(newConnection.gameSession))
            }
        }
    } catch (e: ClosedReceiveChannelException) {
        onOpponentLeft(code)
        println("onClose ${closeReason.await()}")
    } catch (e: Throwable) {
        val connection = connectionRepository.getConnectionBySessionCode(code) ?: return
        connection.sessions.forEach {
            if (it.wsSession.isActive) {
                it.wsSession.sendSerializedBase<ServerResponse>(ServerResponse.Error("Да сами хз бля"))
            }
        }
        println("onClose ${closeReason.await()}")
        println("onError ${closeReason.await()}")
        e.printStackTrace()
    }

    closeReason.await()?.let {
        println("CLOSED MUTHERFUCKER")
        onOpponentLeft(code)
    }
}

private suspend fun DefaultWebSocketServerSession.reconnectSession(connection: Connection, session: DeviceSession) {
    // session.wsSession.close()
    val newSession = session.copy(wsSession = this)
    val sessionsList = connection.sessions.toMutableList()

    sessionsList.remove(session)
    sessionsList += newSession

    connectionRepository.updateConnection(connection.copy(sessions = sessionsList), connection.gameSession.code)
    println("Reconnected")
}

private suspend fun onOpponentLeft(code: String) {
    val connection = connectionRepository.getConnectionBySessionCode(code) ?: return
    connection.sessions.forEach {
        println("MALAHOVV")
        try {
            it.wsSession.sendSerializedBase<ServerResponse>(ServerResponse.Error(OpponentLeftException().localizedMessage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}