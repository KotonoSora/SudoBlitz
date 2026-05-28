package com.jn.numgrid.domain.game

data class GameSession(
    val board: Board,
    val difficulty: Difficulty,
    val score: Int = 0,
    val mistakes: Int = 0,
    val maxMistakes: Int = 3,
    val comboMultiplier: Int = 0,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false
) {
    fun inputNumber(row: Int, col: Int, number: Int): GameSession {
        if (isGameOver) return this
        val cell = board.getCell(row, col)
        if (cell.isGiven) return this

        val isCorrect = (number == cell.correctValue)
        val newBoard = board.updateCell(row, col) {
            it.copy(value = number, isError = !isCorrect)
        }

        return if (isCorrect) {
            val nextMultiplier = minOf(comboMultiplier + 1, 5)
            val points = 10 * nextMultiplier
            val solved = newBoard.isSolved()
            copy(
                board = newBoard,
                score = score + points,
                comboMultiplier = nextMultiplier,
                isVictory = solved,
                isGameOver = solved
            )
        } else {
            val nextMistakes = mistakes + 1
            val over = nextMistakes >= maxMistakes
            copy(
                board = newBoard,
                mistakes = nextMistakes,
                comboMultiplier = 0,
                isGameOver = over
            )
        }
    }

    fun useHint(): GameSession {
        if (isGameOver) return this
        val targetCell = board.cells.flatten().find { it.isEmpty || it.isError } ?: return this

        val newBoard = board.updateCell(targetCell.row, targetCell.col) {
            it.copy(value = it.correctValue, isError = false)
        }
        val solved = newBoard.isSolved()
        val nextMistakes = if (targetCell.isError) (mistakes - 1).coerceAtLeast(0) else mistakes
        return copy(
            board = newBoard,
            mistakes = nextMistakes,
            isVictory = solved,
            isGameOver = solved
        )
    }

    fun undoMistake(): GameSession {
        if (isGameOver || mistakes <= 0) return this
        val targetCell = board.cells.flatten().lastOrNull { it.isError } ?: return this

        val newBoard = board.updateCell(targetCell.row, targetCell.col) {
            it.copy(value = 0, isError = false)
        }
        return copy(
            board = newBoard,
            mistakes = mistakes - 1
        )
    }
}
