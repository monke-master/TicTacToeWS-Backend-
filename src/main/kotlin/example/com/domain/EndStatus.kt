package example.com.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class EndStatus {
    @Serializable
    @SerialName("win")
    data class Win(val cellType: CellType): EndStatus()

    @Serializable
    @SerialName("draw")
    data object Draw: EndStatus()
}