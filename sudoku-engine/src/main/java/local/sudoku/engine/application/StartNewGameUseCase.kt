package local.sudoku.engine.application

import local.sudoku.engine.domain.model.BoardGenerator
import local.sudoku.engine.domain.model.Difficulty
import local.sudoku.engine.domain.model.GameSession
import local.sudoku.engine.domain.model.SudokuEngine
import local.sudoku.engine.domain.model.TimePolicy

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
