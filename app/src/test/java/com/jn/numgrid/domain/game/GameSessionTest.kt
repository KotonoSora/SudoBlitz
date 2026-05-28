package com.jn.numgrid.domain.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameSessionTest {

    private fun createTestBoard(): Board {
        val size = 4
        val cells = List(size) { r ->
            List(size) { c ->
                Cell(row = r, col = c, value = 0, correctValue = 1, isGiven = false)
            }
        }
        return Board(size, cells)
    }

    @Test
    fun `input correct number updates score and multiplier`() {
        val board = createTestBoard()
        val session = GameSession(board, Difficulty.EASY)

        val nextSession = session.inputNumber(0, 0, 1)

        assertEquals(10, nextSession.score)
        assertEquals(1, nextSession.comboMultiplier)
        assertFalse(nextSession.board.getCell(0, 0).isError)
    }

    @Test
    fun `input wrong number updates mistakes and resets multiplier`() {
        val board = createTestBoard()
        val session = GameSession(board, Difficulty.EASY, score = 100, comboMultiplier = 3)

        val nextSession = session.inputNumber(0, 0, 2)

        assertEquals(100, nextSession.score)
        assertEquals(0, nextSession.comboMultiplier)
        assertEquals(1, nextSession.mistakes)
        assertTrue(nextSession.board.getCell(0, 0).isError)
    }

    @Test
    fun `reaching max mistakes ends game`() {
        val board = createTestBoard()
        val session = GameSession(board, Difficulty.EASY, mistakes = 2, maxMistakes = 3)

        val nextSession = session.inputNumber(0, 0, 2)

        assertTrue(nextSession.isGameOver)
        assertFalse(nextSession.isVictory)
    }

    @Test
    fun `solving board ends game with victory`() {
        // Create a board with only one cell to solve
        val cells = listOf(
            listOf(Cell(0, 0, 1, 1, isGiven = true), Cell(0, 1, 0, 2, isGiven = false)),
            listOf(Cell(1, 0, 2, 2, isGiven = true), Cell(1, 1, 1, 1, isGiven = true))
        )
        val board = Board(2, cells)
        val session = GameSession(board, Difficulty.EASY)

        val nextSession = session.inputNumber(0, 1, 2)

        assertTrue(nextSession.isGameOver)
        assertTrue(nextSession.isVictory)
    }

    @Test
    fun `useHint fills one empty cell correctly`() {
        val board = createTestBoard()
        val session = GameSession(board, Difficulty.EASY)

        val nextSession = session.useHint()

        val filledCell = nextSession.board.cells.flatten().first { it.row == 0 && it.col == 0 }
        assertEquals(filledCell.correctValue, filledCell.value)
        assertFalse(filledCell.isError)
    }

    @Test
    fun `undoMistake clears last error`() {
        val board = createTestBoard()
        val session = GameSession(board, Difficulty.EASY)
            .inputNumber(0, 0, 2) // Wrong value (correct is 1)

        assertTrue(session.board.getCell(0, 0).isError)
        assertEquals(1, session.mistakes)

        val nextSession = session.undoMistake()

        assertFalse(nextSession.board.getCell(0, 0).isError)
        assertEquals(0, nextSession.board.getCell(0, 0).value)
        assertEquals(0, nextSession.mistakes)
    }

    @Test
    fun `useHint fixing error cell decrements mistakes`() {
        val board = createTestBoard()
        val mistaken = GameSession(board, Difficulty.EASY).inputNumber(0, 0, 2)

        val nextSession = mistaken.useHint()

        assertEquals(0, nextSession.mistakes)
        assertFalse(nextSession.board.getCell(0, 0).isError)
        assertEquals(
            nextSession.board.getCell(0, 0).correctValue,
            nextSession.board.getCell(0, 0).value
        )
    }
}
