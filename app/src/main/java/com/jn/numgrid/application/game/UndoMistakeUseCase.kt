package com.jn.numgrid.application.game

import com.jn.numgrid.domain.game.GameSession

class UndoMistakeUseCase {
    fun execute(session: GameSession): GameSession {
        return session.undoMistake()
    }
}

