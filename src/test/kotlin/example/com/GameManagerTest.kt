package example.com

import example.com.domain.Cell
import example.com.domain.CellType
import example.com.domain.EndStatus
import example.com.plugins.GameManager
import org.junit.Test
import kotlin.test.assertEquals

class GameManagerTest {

    @Test
    fun `test nought win on first row`() {
        val field = listOf(
            listOf(Cell(CellType.Nought), Cell(CellType.Nought), Cell(CellType.Nought)),
            listOf(Cell(CellType.Cross), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Nought), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(EndStatus.Win(CellType.Nought), gameManager.checkForWin(field))
    }

    @Test
    fun `test cross win on first row`() {
        val field = listOf(
            listOf(Cell(CellType.Cross), Cell(CellType.Cross), Cell(CellType.Cross)),
            listOf(Cell(CellType.Cross), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Nought), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(EndStatus.Win(CellType.Cross), gameManager.checkForWin(field))
    }

    @Test
    fun `test cross win on first col`() {
        val field = listOf(
            listOf(Cell(CellType.Cross), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Cross), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Cross), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(EndStatus.Win(CellType.Cross), gameManager.checkForWin(field))
    }

    @Test
    fun `test cross win on diagonal`() {
        val field = listOf(
            listOf(Cell(CellType.Cross), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Nought), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Cross), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(EndStatus.Win(CellType.Cross), gameManager.checkForWin(field))
    }

    @Test
    fun `test cross win on anti diagonal`() {
        val field = listOf(
            listOf(Cell(CellType.Nought), Cell(CellType.Cross), Cell(CellType.Cross)),
            listOf(Cell(CellType.Nought), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(CellType.Cross), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(EndStatus.Win(CellType.Cross), gameManager.checkForWin(field))
    }

    @Test
    fun `test not game end`() {
        val field = listOf(
            listOf(Cell(CellType.Nought), Cell(null), Cell(CellType.Cross)),
            listOf(Cell(CellType.Nought), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(null), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(null, gameManager.checkForWin(field))
    }

    @Test
    fun `test draw`() {
        val field = listOf(
            listOf(Cell(CellType.Nought), Cell(CellType.Cross), Cell(CellType.Cross)),
            listOf(Cell(CellType.Nought), Cell(CellType.Cross), Cell(CellType.Nought)),
            listOf(Cell(null), Cell(CellType.Nought), Cell(CellType.Cross)),
        )

        val gameManager = GameManager()
        assertEquals(EndStatus.Draw, gameManager.checkForWin(field))
    }
}