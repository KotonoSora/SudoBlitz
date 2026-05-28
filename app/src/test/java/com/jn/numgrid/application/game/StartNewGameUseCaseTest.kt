package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.Board
import com.jn.numgrid.domain.game.BoardGenerator
import com.jn.numgrid.domain.game.Cell
import com.jn.numgrid.domain.game.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StartNewGameUseCaseTest {

    private val useCase = StartNewGameUseCase()

    @Test
    fun `execute returns session and metadata for requested setup`() {
        val result = useCase.execute(size = 4, difficulty = Difficulty.EASY)

        assertEquals(4, result.session.board.size)
        assertEquals(Difficulty.EASY, result.session.difficulty)
        assertEquals(60, result.initialTimeSeconds)
        assertTrue(result.session.board.cells.flatten().isNotEmpty())
    }

    @Test
    fun `execute creates board with consistent puzzle and solution values`() {
        val result = useCase.execute(size = 6, difficulty = Difficulty.MEDIUM)

        assertBoardHasValidSolution(result.session.board)

        result.session.board.cells.flatten().forEach { cell ->
            if (cell.isGiven) {
                assertEquals(cell.correctValue, cell.value)
            } else {
                assertEquals(0, cell.value)
            }
        }
    }

    private fun assertBoardHasValidSolution(board: Board) {
        val expected = (1..board.size).toSet()

        for (row in 0 until board.size) {
            val values = board.cells[row].map { it.correctValue }.toSet()
            assertEquals(expected, values)
        }

        for (col in 0 until board.size) {
            val values =
                (0 until board.size).map { row -> board.cells[row][col].correctValue }.toSet()
            assertEquals(expected, values)
        }

        val rowStep = board.regionRows
        val colStep = board.regionCols
        for (startRow in 0 until board.size step rowStep) {
            for (startCol in 0 until board.size step colStep) {
                val regionValues = buildList {
                    for (row in startRow until startRow + rowStep) {
                        for (col in startCol until startCol + colStep) {
                            add(board.cells[row][col].correctValue)
                        }
                    }
                }.toSet()
                assertEquals(expected, regionValues)
            }
        }
    }

    @Test
    fun `execute uses provided board generator`() {
        val fakeBoard = Board(
            size = 4,
            cells = List(4) { row ->
                List(4) { col ->
                    Cell(row = row, col = col, value = 1, correctValue = 1, isGiven = true)
                }
            }
        )
        val fakeGenerator = object : BoardGenerator {
            override fun generateBoard(size: Int, difficulty: Difficulty): Board = fakeBoard
        }

        val useCase = StartNewGameUseCase(fakeGenerator)
        val result = useCase.execute(size = 4, difficulty = Difficulty.EASY)

        assertEquals(fakeBoard, result.session.board)
    }
}
