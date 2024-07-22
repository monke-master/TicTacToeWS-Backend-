package example.com.data

import example.com.domain.*
import kotlin.random.Random

fun randomType(): CellType {
    val randomInt = (0..1).random()
    return CellType.entries[randomInt]
}

fun createGame(): Game {
    val row = listOf(Cell(null), Cell(null), Cell(null),)
    val grid = listOf(row, row, row)
    val turn = Turn(
        playerId = (0..1).random().toString(),
        number = 0,
        timer = 30
    )
    val game = Game(
        grid = grid,
        turn = turn
    )
    return game
}

fun generateCode(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..6)
        .map { chars.random() }
        .joinToString("")
}

fun GameSession.addNewPlayer(): GameSession {
    if (players[0].type == CellType.Nought) {
        return this.copy(players = players + Player("2", CellType.Cross))
    }
    return this.copy(players = players + Player("2", CellType.Nought))
}


fun CellType?.getCode(): Int {
    if (this == null) return Int.MAX_VALUE
    return CellType.entries.indexOf(this)
}