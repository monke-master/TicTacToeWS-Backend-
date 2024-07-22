package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val field: Field,
    val endStatus: EndStatus? = null,
    val turn: Turn,
)
