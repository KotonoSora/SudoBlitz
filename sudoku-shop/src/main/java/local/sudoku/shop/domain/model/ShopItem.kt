package local.sudoku.shop.domain.model

sealed interface ShopItem {
    val id: String
    val name: String
    val description: String
    val price: Int
}

data class PowerUpShopItem(
    override val id: String,
    /** Must match the engine's PowerUpType id ("extra_time"/"hint"/"undo"). */
    val inventoryId: String,
    override val name: String,
    override val description: String,
    override val price: Int,
    val isUnlocked: Boolean = false
) : ShopItem
