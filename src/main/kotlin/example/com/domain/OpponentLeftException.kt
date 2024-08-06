package example.com.domain

class OpponentLeftException: Throwable() {
    override fun getLocalizedMessage(): String {
        return "Opponent has left the game"
    }
}