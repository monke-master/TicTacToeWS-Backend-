package example.com.domain

sealed class EndStatus {
    data class Win(val player: Player): EndStatus()

    data object Draw: EndStatus()
}