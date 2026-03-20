package com.kotonosora.sudoblitz.model

data class Cell(
    val row: Int,
    val col: Int,
    val value: Int = 0,
    val correctValue: Int = 0,
    val isGiven: Boolean = false,
    val isError: Boolean = false
) {
    val isEmpty: Boolean get() = value == 0
}

enum class Difficulty {
    EASY, MEDIUM, HARD, VERY_HARD
}

data class Board(
    val size: Int, // 4, 6, or 9
    val cells: List<List<Cell>>
) {
    val regionRows: Int
        get() = when (size) {
            4 -> 2
            6 -> 2
            else -> 3
        }

    val regionCols: Int
        get() = when (size) {
            4 -> 2
            6 -> 3
            else -> 3
        }

    fun getCell(row: Int, col: Int): Cell = cells[row][col]

    fun updateCell(row: Int, col: Int, update: (Cell) -> Cell): Board {
        val newCells = cells.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, cell ->
                    if (c == col) update(cell) else cell
                }
            } else {
                rowList
            }
        }
        return copy(cells = newCells)
    }

    fun isSolved(): Boolean {
        return cells.flatten().all { !it.isEmpty && !it.isError && it.value == it.correctValue }
    }
}
