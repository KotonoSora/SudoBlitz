package local.sudoku.engine.domain.repository

/**
 * Narrow power-up wallet. The UI's inventory store adapts to this so
 * `:sudoku-engine` never depends on Android DataStore or app classes.
 */
interface PowerUpStore {
    suspend fun usePowerUp(typeId: String): Boolean
}
