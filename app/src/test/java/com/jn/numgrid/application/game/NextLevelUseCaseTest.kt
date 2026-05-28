package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.Board
import com.jn.numgrid.domain.game.BoardGenerator
import com.jn.numgrid.domain.game.Cell
import com.jn.numgrid.domain.game.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NextLevelUseCaseTest {

    private val useCase = NextLevelUseCase()

    @Test
    fun `execute returns session and time bonus for current size and difficulty`() {
        val result = useCase.execute(size = 9, difficulty = Difficulty.HARD)
        val session = result.session

        assertEquals(9, session.board.size)
        assertEquals(Difficulty.HARD, session.difficulty)
        assertEquals(150, result.addedTimeSeconds)
        assertTrue(session.board.cells.flatten().isNotEmpty())
    }

    @Test
    fun `execute uses provided board generator`() {
        val fakeBoard = Board(
            size = 6,
            cells = List(6) { row ->
                List(6) { col ->
                    Cell(row = row, col = col, value = 1, correctValue = 1, isGiven = true)
                }
            }
        )
        val fakeGenerator = object : BoardGenerator {
            override fun generateBoard(size: Int, difficulty: Difficulty): Board = fakeBoard
        }

        val useCase = NextLevelUseCase(fakeGenerator)
        val result = useCase.execute(size = 6, difficulty = Difficulty.MEDIUM)

        assertEquals(fakeBoard, result.session.board)
        assertEquals(90, result.addedTimeSeconds)
    }
}
