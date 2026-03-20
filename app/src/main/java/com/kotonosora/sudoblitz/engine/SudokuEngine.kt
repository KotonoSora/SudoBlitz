package com.kotonosora.sudoblitz.engine

import com.kotonosora.sudoblitz.model.Board
import com.kotonosora.sudoblitz.model.Cell
import com.kotonosora.sudoblitz.model.Difficulty

object SudokuEngine {

    /**
     * Generates a solvable Sudoku board of given size (4, 6, or 9) and difficulty.
     */
    fun generateBoard(size: Int, difficulty: Difficulty): Board {
        val regionRows = when (size) {
            4 -> 2
            6 -> 2
            else -> 3
        }
        val regionCols = when (size) {
            4 -> 2
            6 -> 3
            else -> 3
        }

        // Generate a fully solved board
        val solvedGrid = Array(size) { IntArray(size) }
        solve(solvedGrid, size, regionRows, regionCols)

        // Remove numbers based on difficulty
        val puzzleGrid = Array(size) { i -> solvedGrid[i].clone() }
        val numToRemove = getCellsToRemove(size, difficulty)
        removeCells(puzzleGrid, size, numToRemove, regionRows, regionCols)

        val cells = List(size) { row ->
            List(size) { col ->
                val value = puzzleGrid[row][col]
                Cell(
                    row = row,
                    col = col,
                    value = value,
                    correctValue = solvedGrid[row][col],
                    isGiven = value != 0,
                    isError = false
                )
            }
        }

        return Board(size = size, cells = cells)
    }

    private fun solve(grid: Array<IntArray>, size: Int, regionRows: Int, regionCols: Int): Boolean {
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (grid[row][col] == 0) {
                    val numbers = (1..size).shuffled()
                    for (num in numbers) {
                        if (isValid(grid, row, col, num, size, regionRows, regionCols)) {
                            grid[row][col] = num
                            if (solve(grid, size, regionRows, regionCols)) {
                                return true
                            }
                            grid[row][col] = 0 // backtrack
                        }
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(
        grid: Array<IntArray>,
        row: Int,
        col: Int,
        num: Int,
        size: Int,
        regionRows: Int,
        regionCols: Int
    ): Boolean {
        // Check row and col
        for (i in 0 until size) {
            if (grid[row][i] == num) return false
            if (grid[i][col] == num) return false
        }

        // Check region
        val startRow = row - (row % regionRows)
        val startCol = col - (col % regionCols)
        for (r in 0 until regionRows) {
            for (c in 0 until regionCols) {
                if (grid[r + startRow][c + startCol] == num) {
                    return false
                }
            }
        }
        return true
    }

    private fun removeCells(
        grid: Array<IntArray>,
        size: Int,
        count: Int,
        regionRows: Int,
        regionCols: Int
    ) {
        var toRemove = count
        val cellIds = (0 until (size * size)).shuffled().toMutableList()

        for (cellId in cellIds) {
            if (toRemove <= 0) break
            val row = cellId / size
            val col = cellId % size

            val temp = grid[row][col]
            grid[row][col] = 0

            val solutions = countSolutions(grid, size, regionRows, regionCols, 0)
            if (solutions != 1) {
                grid[row][col] = temp // put back if not unique
            } else {
                toRemove--
            }
        }
    }

    private fun countSolutions(
        grid: Array<IntArray>,
        size: Int,
        regionRows: Int,
        regionCols: Int,
        count: Int
    ): Int {
        var solutionCount = count
        for (row in 0 until size) {
            for (col in 0 until size) {
                if (grid[row][col] == 0) {
                    for (num in 1..size) {
                        if (isValid(grid, row, col, num, size, regionRows, regionCols)) {
                            grid[row][col] = num
                            solutionCount =
                                countSolutions(grid, size, regionRows, regionCols, solutionCount)
                            grid[row][col] = 0 // always backtrack for counting
                            if (solutionCount > 1) {
                                return solutionCount
                            }
                        }
                    }
                    return solutionCount
                }
            }
        }
        return solutionCount + 1
    }

    private fun getCellsToRemove(size: Int, difficulty: Difficulty): Int {
        return when (size) {
            4 -> when (difficulty) {
                Difficulty.EASY -> 6
                Difficulty.MEDIUM -> 8
                Difficulty.HARD -> 10
                Difficulty.VERY_HARD -> 12
            }

            6 -> when (difficulty) {
                Difficulty.EASY -> 14
                Difficulty.MEDIUM -> 18
                Difficulty.HARD -> 22
                Difficulty.VERY_HARD -> 26
            }

            9 -> when (difficulty) {
                Difficulty.EASY -> 35
                Difficulty.MEDIUM -> 45
                Difficulty.HARD -> 55
                Difficulty.VERY_HARD -> 60 // 9x9 usually has 81 cells. 21 givens is very hard.
            }

            else -> size * size / 2
        }
    }
}
