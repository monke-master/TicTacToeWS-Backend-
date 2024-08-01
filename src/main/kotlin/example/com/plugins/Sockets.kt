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
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false

        contentConverter = KotlinxWebsocketSerializationConverter(getConverter())
    }
    routing {

        webSocket("/sessions/new") {
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

            val connection = Connection(
                sessions = listOf(this),
                gameSession = gameSession
            )
            connectionRepository.addSession(connection)

            sendSerialized(gameSession)

            handleIncomeData(gameSession.code)
        }

        webSocket("/sessions/{code}") {
            val code = call.parameters["code"] ?: return@webSocket

            val connection = connectionRepository.getConnectionBySessionCode(code) ?: return@webSocket
            val newConnection = connection.copy(
                sessions = connection.sessions + this,
                gameSession = connection.gameSession.addNewPlayer()
            )
            connectionRepository.updateConnection(newConnection, code)

            for (session in newConnection.sessions) {
                session.sendSerializedBase<GameSession>(newConnection.gameSession)
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
                it.sendSerializedBase<GameSession>(newConnection.gameSession)
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
    for (frame in incoming) {
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
            connection.copy(gameSession = connection.gameSession.copy(game = game))
        }

        connectionRepository.updateConnection(newConnection, code)
        newConnection.sessions.forEach {
            it.sendSerializedBase<GameSession>(newConnection.gameSession)
        }
    }
}