package example.com.data

import example.com.plugins.Connection
import java.util.*
import kotlin.collections.LinkedHashSet

class ConnectionRepository {


    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    fun addSession(connection: Connection) {
        connections.add(connection)
    }

    fun getConnectionBySessionCode(code: String): Connection? {
        return connections.find { it.gameSession.code == code }
    }

    fun updateConnection(newConnection: Connection, code: String) {
        connections.remove(getConnectionBySessionCode(code))
        connections.add(newConnection)
    }
}