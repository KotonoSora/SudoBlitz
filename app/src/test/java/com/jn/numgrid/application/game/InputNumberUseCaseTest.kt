package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.Board
import com.jn.numgrid.domain.game.Cell
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.domain.game.GameSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class InputNumberUseCaseTest {

    private val useCase = InputNumberUseCase()

    @Test
    fun `execute delegates to session inputNumber`() {
        val board = Board(
            size = 4,
            cells = List(4) { row ->
                List(4) { col ->
                    Cell(row = row, col = col, value = 0, correctValue = 1, isGiven = false)
                }
            }
        )
        val session = GameSession(board = board, difficulty = Difficulty.EASY)

        val next = useCase.execute(session, row = 0, col = 0, number = 1)

        assertEquals(10, next.score)
        assertEquals(1, next.comboMultiplier)
        assertFalse(next.board.getCell(0, 0).isError)
    }
}

