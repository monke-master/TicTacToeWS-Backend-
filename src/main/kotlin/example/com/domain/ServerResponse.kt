package example.com.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ServerResponse {

    @Serializable
    @SerialName("success")
    data class Success(
        val gameSession: GameSession
    ): ServerResponse()

    @Serializable
    @SerialName("error")
    data class Error(
        val errorMessage: String
    ): ServerResponse()


}