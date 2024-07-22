package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val grid: List<List<Cell>>,
    val endStatus: EndStatus? = null,
    val turn: Turn,
)
