package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.BoardGenerator
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.domain.game.GameSession
import com.jn.numgrid.domain.game.SudokuEngine
import com.jn.numgrid.domain.game.TimePolicy

data class NewGameResult(
    val session: GameSession,
    val initialTimeSeconds: Int
)

class StartNewGameUseCase(
    private val boardGenerator: BoardGenerator = SudokuEngine
) {
    fun execute(size: Int, difficulty: Difficulty): NewGameResult {
        val board = boardGenerator.generateBoard(size, difficulty)
        val session = GameSession(
            board = board,
            difficulty = difficulty
        )
        return NewGameResult(
            session = session,
            initialTimeSeconds = TimePolicy.initialTimeSeconds(size)
        )
    }
}

