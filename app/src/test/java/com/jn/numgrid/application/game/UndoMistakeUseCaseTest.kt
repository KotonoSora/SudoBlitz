package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.Board
import com.jn.numgrid.domain.game.Cell
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.domain.game.GameSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UndoMistakeUseCaseTest {

    private val useCase = UndoMistakeUseCase()

    @Test
    fun `execute removes last error and decrements mistakes`() {
        val board = Board(
            size = 4,
            cells = List(4) { row ->
                List(4) { col ->
                    Cell(row = row, col = col, value = 0, correctValue = 1, isGiven = false)
                }
            }
        )
        val session = GameSession(board = board, difficulty = Difficulty.HARD).inputNumber(0, 0, 3)
        assertTrue(session.board.getCell(0, 0).isError)

        val next = useCase.execute(session)

        assertFalse(next.board.getCell(0, 0).isError)
        assertEquals(0, next.board.getCell(0, 0).value)
        assertEquals(0, next.mistakes)
    }
}

