package example.com.plugins

import io.ktor.serialization.kotlinx.*
import io.ktor.util.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.serialization.json.Json
import java.nio.charset.Charset

@OptIn(InternalAPI::class)
suspend inline fun <reified T> DefaultWebSocketSession.sendSerializedBase(data: Any?) {
    this.sendSerializedBase<T>(
        data = data,
        charset = Charset.defaultCharset(),
        converter = KotlinxWebsocketSerializationConverter(getConverter())
    )
}