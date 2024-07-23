package example.com.data

import example.com.domain.*

fun randomType(): CellType {
    val randomInt = (0..1).random()
    return CellType.entries[randomInt]
}

fun createGame(): Game {
    val row = listOf(Cell(CellType.Nought), Cell(null), Cell(null),)
    val grid = listOf(row, row, row)
    val turn = Turn(
        playerId = (0..1).random().toString(),
        number = 0,
        timer = 30
    )
    val game = Game(
        field = grid,
        turn = turn,
        gameStatus = GameStatus.Waiting
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

fun GameSession.setEndStatus(endStatus: EndStatus): GameSession {
    return copy(game = game.copy(gameStatus = GameStatus.End(endStatus)))
}

fun GameSession.nextTurn(game: Game): GameSession {
    val newTurn  = Turn(
        playerId = if (game.turn.playerId == "1") "2" else "1",
        number = game.turn.number + 1,
        timer = 30
    )
    return copy(
        game = game.copy(turn = newTurn)
    )
}