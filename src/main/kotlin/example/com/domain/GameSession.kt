package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class GameSession(
    val players: List<Player>,
    val game: Game,
    val code: String
)
