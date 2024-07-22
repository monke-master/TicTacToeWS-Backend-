package example.com.domain

data class Session(
    val players: List<Player>,
    val game: Game,
    val code: String
)
