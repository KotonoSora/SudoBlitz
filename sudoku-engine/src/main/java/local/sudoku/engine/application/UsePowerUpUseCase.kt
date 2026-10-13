package local.sudoku.engine.application

import local.sudoku.engine.domain.model.PowerUpType
import local.sudoku.engine.domain.repository.PowerUpStore

class UsePowerUpUseCase(private val store: PowerUpStore) {
    suspend operator fun invoke(typeId: String): Boolean {
        PowerUpType.fromId(typeId) ?: return false
        return store.usePowerUp(typeId)
    }
}
