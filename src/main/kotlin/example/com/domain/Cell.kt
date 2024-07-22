package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    val type: CellType? = null
)