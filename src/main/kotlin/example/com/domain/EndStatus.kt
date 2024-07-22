package example.com.domain

import kotlinx.serialization.Serializable

@Serializable
sealed class EndStatus {
    @Serializable
    data class Win(val cellType: CellType): EndStatus()

    @Serializable
    data object Draw: EndStatus()
}