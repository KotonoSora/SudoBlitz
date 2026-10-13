package local.core.ui.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import local.core.ui.AppConstants

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepositoryImpl(
    private val context: Context,
    // Must match SkinIds.SKIN_DEFAULT_ID. Plain string so :core-ui stays game-agnostic;
    // callers that can see both modules should pass the constant explicitly.
    private val defaultSkinId: String = "skin_default",
    // Game-specific inventory slots (power-up type ID -> DataStore int key).
    // Defaults to the Flappy map so existing installs keep their inventory;
    // other games pass their own map built with [inventoryKeysFor] from a game
    // module — never add game-specific helpers to this companion.
    // Key *names* below MUST stay identical so existing users keep their inventory.
    inventoryKeys: Map<String, Preferences.Key<Int>>? = null
) : CoinAccount, InventoryStore, SkinLibrary, AudioSettings {

    companion object {
        private const val INITIAL_COINS = AppConstants.DEFAULT_INITIAL_COINS
        private val SELECTED_SKIN_KEY = stringPreferencesKey("selected_skin")
        private val COINS_KEY = intPreferencesKey("coins")
        private val PURCHASED_ITEMS_KEY = stringSetPreferencesKey("purchased_items")
        private val SHIELD_COUNT_KEY = intPreferencesKey("shield_count")
        private val MULTIPLIER_COUNT_KEY = intPreferencesKey("multiplier_count")
        private val AUTOPLAY_COUNT_KEY = intPreferencesKey("autoplay_count")
        private val MUSIC_ENABLED_KEY = booleanPreferencesKey("music_enabled")
        private val SFX_ENABLED_KEY = booleanPreferencesKey("sfx_enabled")

        /** Flappy inventory map (default). Key names are frozen for existing users. */
        private fun flappyInventoryKeys(): Map<String, Preferences.Key<Int>> = mapOf(
            "shield" to SHIELD_COUNT_KEY,
            "multiplier" to MULTIPLIER_COUNT_KEY,
            "autoplay" to AUTOPLAY_COUNT_KEY,
            "boost" to AUTOPLAY_COUNT_KEY
        )

        /**
         * Generic helper for games to build their own inventory map.
         * Each pair is (power-up type ID -> DataStore int-key name).
         * Key names are frozen once a game ships — existing users keep inventory.
         */
        fun inventoryKeysFor(vararg pairs: Pair<String, String>): Map<String, Preferences.Key<Int>> =
            pairs.associate { (typeId, keyName) -> typeId to intPreferencesKey(keyName) }
    }

    /**
     * String-keyed inventory slots. Keys mirror the game's power-up type IDs;
     * DataStore key names above MUST stay identical so existing users keep
     * their inventory.
     */
    private val INVENTORY_KEYS: Map<String, Preferences.Key<Int>> =
        inventoryKeys ?: flappyInventoryKeys()

    override val selectedSkinFlow: Flow<String> = context.settingsDataStore.data
        .map { preferences ->
            preferences[SELECTED_SKIN_KEY] ?: defaultSkinId
        }

    override val coinsFlow: Flow<Int> = context.settingsDataStore.data
        .map { preferences ->
            preferences[COINS_KEY] ?: INITIAL_COINS
        }

    override val purchasedItemsFlow: Flow<Set<String>> = context.settingsDataStore.data
        .map { preferences ->
            preferences[PURCHASED_ITEMS_KEY] ?: setOf(defaultSkinId)
        }

    override fun inventoryCountFlow(itemId: String): Flow<Int> {
        val key = INVENTORY_KEYS[itemId] ?: return flowOf(0)
        return context.settingsDataStore.data
            .map { preferences -> preferences[key] ?: 0 }
    }

    override val musicEnabledFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[MUSIC_ENABLED_KEY] ?: true
        }

    override val sfxEnabledFlow: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[SFX_ENABLED_KEY] ?: true
        }

    override suspend fun updateSelectedSkin(skinId: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[SELECTED_SKIN_KEY] = skinId
        }
    }

    override suspend fun addCoins(amount: Int) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: INITIAL_COINS
            preferences[COINS_KEY] = current + amount
        }
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        context.settingsDataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: INITIAL_COINS
            if (current >= amount) {
                preferences[COINS_KEY] = current - amount
                success = true
            }
        }
        return success
    }

    override suspend fun purchaseItem(itemId: String) {
        context.settingsDataStore.edit { preferences ->
            val current = preferences[PURCHASED_ITEMS_KEY] ?: setOf(defaultSkinId)
            preferences[PURCHASED_ITEMS_KEY] = current + itemId
        }
    }

    override suspend fun addToInventory(itemId: String) {
        val key = INVENTORY_KEYS[itemId] ?: return
        context.settingsDataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            preferences[key] = current + 1
        }
    }

    override suspend fun consumeFromInventory(itemId: String): Boolean {
        val key = INVENTORY_KEYS[itemId] ?: return false
        var success = false
        context.settingsDataStore.edit { preferences ->
            val current = preferences[key] ?: 0
            if (current > 0) {
                preferences[key] = current - 1
                success = true
            }
        }
        return success
    }

    override suspend fun toggleMusic(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[MUSIC_ENABLED_KEY] = enabled
        }
    }

    override suspend fun toggleSfx(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[SFX_ENABLED_KEY] = enabled
        }
    }
}
