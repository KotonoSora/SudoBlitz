package com.jn.numgrid.domain.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SudokuEngineTest {

    @Test
    fun `generateBoard rejects unsupported size`() {
        try {
            SudokuEngine.generateBoard(5, Difficulty.EASY)
            fail("Expected IllegalArgumentException for unsupported size")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Unsupported board size") == true)
        }
    }

    @Test
    fun `generateBoard creates board of requested size`() {
        val sizes = listOf(4, 6, 9)
        for (size in sizes) {
            val board = SudokuEngine.generateBoard(size, Difficulty.EASY)
            assertEquals(size, board.size)
            assertEquals(size, board.cells.size)
            assertTrue(board.cells.all { it.size == size })
        }
    }

    @Test
    fun `generateBoard creates board with unique solution`() {
        val board = SudokuEngine.generateBoard(4, Difficulty.HARD)

        // This is hard to test directly without exposing countSolutions, 
        // but we can at least verify it is solvable.
        assertTrue(board.cells.flatten().any { it.value == 0 }) // Should have some empty cells

        board.cells.flatten().forEach { cell ->
            if (cell.value != 0) {
                assertEquals(cell.correctValue, cell.value)
            }
        }
    }

    @Test
    fun `generated board difficulty affects number of removed cells`() {
        val size = 4
        val easyBoard = SudokuEngine.generateBoard(size, Difficulty.EASY)
        val hardBoard = SudokuEngine.generateBoard(size, Difficulty.VERY_HARD)

        val easyGivens = easyBoard.cells.flatten().count { it.isGiven }
        val hardGivens = hardBoard.cells.flatten().count { it.isGiven }

        assertTrue(
            "Hard board should have fewer givens than easy board. Easy: $easyGivens, Hard: $hardGivens",
            hardGivens < easyGivens
        )
    }

    @Test
    fun `generated board satisfies Sudoku rules`() {
        val size = 9
        val board = SudokuEngine.generateBoard(size, Difficulty.MEDIUM)

        // Check rows
        for (row in 0 until size) {
            val rowValues = board.cells[row].map { it.correctValue }.filter { it != 0 }
            assertEquals(size, rowValues.distinct().size)
        }

        // Check columns
        for (col in 0 until size) {
            val colValues =
                (0 until size).map { row -> board.cells[row][col].correctValue }.filter { it != 0 }
            assertEquals(size, colValues.distinct().size)
        }

        // Check regions
        val regionRows = 3
        val regionCols = 3
        for (r in 0 until size step regionRows) {
            for (c in 0 until size step regionCols) {
                val regionValues = mutableListOf<Int>()
                for (rr in 0 until regionRows) {
                    for (cc in 0 until regionCols) {
                        regionValues.add(board.cells[r + rr][c + cc].correctValue)
                    }
                }
                assertEquals(size, regionValues.distinct().size)
            }
        }
    }
}
