package example.com.plugins

import example.com.domain.GameSession
import io.ktor.websocket.*

data class Connection(
    val sessions: List<DeviceSession>,
    val gameSession: GameSession
)