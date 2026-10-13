package local.core.ui.data

import kotlinx.coroutines.flow.Flow

/** Owned/selectable-item port. Skin IDs are plain strings owned by the game. */
interface SkinLibrary {
    val selectedSkinFlow: Flow<String>
    val purchasedItemsFlow: Flow<Set<String>>
    suspend fun updateSelectedSkin(skinId: String)
    suspend fun purchaseItem(itemId: String)
}
