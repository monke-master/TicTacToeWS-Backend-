package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val type: CellType
)