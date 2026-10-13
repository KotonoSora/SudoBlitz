package local.sudoku.engine.application

import local.sudoku.engine.domain.model.BoardGenerator
import local.sudoku.engine.domain.model.Difficulty
import local.sudoku.engine.domain.model.GameSession
import local.sudoku.engine.domain.model.SudokuEngine
import local.sudoku.engine.domain.model.TimePolicy

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
