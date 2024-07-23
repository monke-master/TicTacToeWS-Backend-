package example.com.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class GameStatus {

    @Serializable
    @SerialName("waiting")
    data object Waiting: GameStatus()

    @Serializable
    @SerialName("started")
    data object Started: GameStatus()

    @Serializable
    @SerialName("end")
    data class End(val endStatus: EndStatus): GameStatus()
}