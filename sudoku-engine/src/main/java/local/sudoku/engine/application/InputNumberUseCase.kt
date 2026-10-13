package local.sudoku.engine.application

import local.sudoku.engine.domain.model.GameSession

class InputNumberUseCase {
    fun execute(session: GameSession, row: Int, col: Int, number: Int): GameSession {
        return session.inputNumber(row, col, number)
    }
}
