package local.sudoku.shop.domain.service

import local.sudoku.shop.domain.model.PowerUpIds
import local.sudoku.shop.domain.model.PowerUpShopItem
import local.sudoku.shop.domain.model.ShopItem

object PowerUps {
    const val EXTRA_TIME_COST = 20
    const val HINT_COST = 30
    const val UNDO_COST = 15

    const val EXTRA_TIME_SECONDS = 30

    fun getPowerUpItems(): List<ShopItem> {
        return listOf(
            PowerUpShopItem(
                id = PowerUpIds.POWERUP_EXTRA_TIME_ID,
                // Must match the engine's PowerUpType id ("extra_time").
                inventoryId = "extra_time",
                name = "Extra Time",
                description = "Adds 30 seconds to the clock",
                price = EXTRA_TIME_COST,
                isUnlocked = false
            ),
            PowerUpShopItem(
                id = PowerUpIds.POWERUP_HINT_ID,
                // Must match the engine's PowerUpType id ("hint").
                inventoryId = "hint",
                name = "Hint",
                description = "Reveals the correct number",
                price = HINT_COST,
                isUnlocked = false
            ),
            PowerUpShopItem(
                id = PowerUpIds.POWERUP_UNDO_ID,
                // Must match the engine's PowerUpType id ("undo").
                inventoryId = "undo",
                name = "Undo",
                description = "Clears your last mistake",
                price = UNDO_COST,
                isUnlocked = false
            )
        )
    }
}
