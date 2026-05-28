package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.GameSession

class InputNumberUseCase {
    fun execute(session: GameSession, row: Int, col: Int, number: Int): GameSession {
        return session.inputNumber(row, col, number)
    }
}

