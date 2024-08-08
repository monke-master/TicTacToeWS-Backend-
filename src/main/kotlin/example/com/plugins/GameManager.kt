package example.com.plugins

import example.com.data.getCode
import example.com.domain.CellType
import example.com.domain.EndStatus
import example.com.domain.Field

class GameManager {

    private lateinit var field: Field

    fun checkForWin(field: Field): EndStatus? {
        this.field = field
        checkColumns()?.let { return EndStatus.Win(it) }
        checkRows()?.let { return EndStatus.Win(it) }

        checkDig()?.let { return EndStatus.Win(it) }

        if (checkForDraw()) return EndStatus.Draw

        return null
    }

    private fun checkColumns(): CellType? {
        for (col in field.indices) {
            val sum = field.map { it[col] }.sumOf { it.type.getCode() }
            if (sum == 0) return CellType.Nought
            if (sum == field.size) return CellType.Cross
        }
        return null
    }

    private fun checkRows(): CellType? {
        for (row in field) {
            val sum = row.sumOf { it.type.getCode() }
            if (sum == 0) return CellType.Nought
            if (sum == row.size) return CellType.Cross
        }
        return null
    }

    private fun checkDig(): CellType? {
        var sum = 0
        for (index in field.indices) {
            sum += field[index][index].type.getCode()
        }

        if (sum == 0) return CellType.Nought
        if (sum == field.size) return CellType.Cross

        sum = 0

        for (index in field.indices) {
            sum += field[index][field.size - index - 1].type.getCode()
        }

        if (sum == 0) return CellType.Nought
        if (sum == field.size) return CellType.Cross

        return null
    }

    private fun checkForDraw(): Boolean {
        return field.flatten().count { it.type != null } == field.size*field.size
    }

}