package local.sudoku.engine.application

import local.sudoku.engine.domain.model.Board
import local.sudoku.engine.domain.model.Cell
import local.sudoku.engine.domain.model.Difficulty
import local.sudoku.engine.domain.model.GameSession
import org.junit.Assert.assertEquals
import org.junit.Test

class UseHintUseCaseTest {

    private val useCase = UseHintUseCase()

    @Test
    fun `execute fills one unresolved cell`() {
        val board = Board(
            size = 4,
            cells = List(4) { row ->
                List(4) { col ->
                    Cell(row = row, col = col, value = 0, correctValue = 2, isGiven = false)
                }
            }
        )
        val session = GameSession(board = board, difficulty = Difficulty.MEDIUM)

        val next = useCase.execute(session)
        val first = next.board.getCell(0, 0)

        assertEquals(2, first.value)
        assertEquals(2, first.correctValue)
    }
}

