package local.sudoku.engine.application

import local.sudoku.engine.domain.model.GameSession

class UndoMistakeUseCase {
    fun execute(session: GameSession): GameSession {
        return session.undoMistake()
    }
}
