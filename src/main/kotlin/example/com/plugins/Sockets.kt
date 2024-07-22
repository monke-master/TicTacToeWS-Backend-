package example.com.plugins

import example.com.data.*
import example.com.domain.Cell
import example.com.domain.GameSession
import example.com.domain.Player
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
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.charset.Charset
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

@OptIn(ExperimentalSerializationApi::class, InternalAPI::class)
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false

        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }
    routing {
        val connectionRepository = ConnectionRepository()
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
                session.sendSerializedBase<GameSession>(
                    charset = Charset.defaultCharset(),
                    data = newConnection.gameSession,
                    converter = KotlinxWebsocketSerializationConverter(Json)
                )
            }
            handleIncomeData(code)
        }
    }
}


suspend fun DefaultWebSocketServerSession.handleIncomeData(gameCode: String) {
    for (frame in incoming) {
        val field = receiveDeserialized<List<List<Cell>>>()

    }
}

suspend fun checkForWin(field: List<List<Cell>>) {

}

