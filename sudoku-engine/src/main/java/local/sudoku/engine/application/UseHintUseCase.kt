package local.sudoku.engine.application

import local.sudoku.engine.domain.model.GameSession

class UseHintUseCase {
    fun execute(session: GameSession): GameSession {
        return session.useHint()
    }
}
