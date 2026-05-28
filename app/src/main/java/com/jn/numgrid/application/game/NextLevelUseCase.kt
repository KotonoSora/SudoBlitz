package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.BoardGenerator
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.domain.game.GameSession
import com.jn.numgrid.domain.game.SudokuEngine
import com.jn.numgrid.domain.game.TimePolicy

data class NextLevelResult(
    val session: GameSession,
    val addedTimeSeconds: Int
)

class NextLevelUseCase(
    private val boardGenerator: BoardGenerator = SudokuEngine
) {
    fun execute(size: Int, difficulty: Difficulty): NextLevelResult {
        val board = boardGenerator.generateBoard(size, difficulty)
        val session = GameSession(
            board = board,
            difficulty = difficulty
        )
        return NextLevelResult(
            session = session,
            addedTimeSeconds = TimePolicy.bonusTimeForNextLevel(size)
        )
    }
}

