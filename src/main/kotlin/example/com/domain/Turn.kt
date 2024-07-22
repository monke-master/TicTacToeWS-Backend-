package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class Turn(
    val playerId: String,
    val number: Int,
    val timer: Int
)
