package local.core.ui.data

import kotlinx.coroutines.flow.Flow

/**
 * Consumable-inventory port. Item IDs are plain strings owned by the game
 * (e.g. power-up type IDs) so this module stays game-agnostic.
 */
interface InventoryStore {
    fun inventoryCountFlow(itemId: String): Flow<Int>
    suspend fun addToInventory(itemId: String)
    suspend fun consumeFromInventory(itemId: String): Boolean
}
