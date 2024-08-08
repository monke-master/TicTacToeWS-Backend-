package example.com.plugins

import io.ktor.websocket.*

data class DeviceSession(
    val deviceId: String,
    val wsSession: DefaultWebSocketSession
)