package example.com.domain

data class Game(
    val grid: List<List<Cell>>,
    val endStatus: EndStatus? = null,
    val turn: Turn,
)
