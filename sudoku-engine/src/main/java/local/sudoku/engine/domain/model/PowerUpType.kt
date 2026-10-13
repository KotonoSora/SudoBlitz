package local.sudoku.engine.domain.model

/**
 * Consumable power-ups. IDs are the string contract shared with `:sudoku-shop`:
 * shop catalog `inventoryId`s MUST equal these IDs (enforced by a `check()` in
 * the UI container and by the shop's contract test).
 */
enum class PowerUpType(val id: String) {
    EXTRA_TIME("extra_time"),
    HINT("hint"),
    UNDO("undo");

    companion object {
        fun fromId(id: String) = entries.find { it.id == id }
    }
}
