package local.sudoku.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import local.core.billing.BillingRepository
import local.core.billing.BillingStatus
import local.core.shop.coins.CoinPackItem
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.sudoku.engine.domain.model.PowerUpType
import local.sudoku.shop.application.CoinShopConfig
import local.sudoku.shop.domain.model.PowerUpShopItem
import local.sudoku.shop.domain.model.ShopItem
import local.sudoku.shop.domain.service.PowerUps

class ShopViewModel(
    private val billingRepository: BillingRepository,
    private val coinAccount: CoinAccount,
    private val inventoryStore: InventoryStore
) : ViewModel() {

    val products: StateFlow<List<CoinPackItem>> = billingRepository.products
        .map { list -> list.map { CoinShopConfig.coinPackItem(it) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val status: StateFlow<BillingStatus> = billingRepository.status
        .stateIn(viewModelScope, SharingStarted.Eagerly, BillingStatus.IDLE)

    val coins: StateFlow<Int> = coinAccount.coinsFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, 0
    )

    val powerUpItems: List<ShopItem> = PowerUps.getPowerUpItems()

    /** Owned counts keyed by engine power-up type id. */
    val powerUpCounts: StateFlow<Map<String, Int>> = combine(
        inventoryStore.inventoryCountFlow(PowerUpType.EXTRA_TIME.id),
        inventoryStore.inventoryCountFlow(PowerUpType.HINT.id),
        inventoryStore.inventoryCountFlow(PowerUpType.UNDO.id)
    ) { extraTime, hint, undo ->
        mapOf(
            PowerUpType.EXTRA_TIME.id to extraTime,
            PowerUpType.HINT.id to hint,
            PowerUpType.UNDO.id to undo
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    init {
        billingRepository.startConnection()
    }

    fun buyCoinPack(activity: android.app.Activity, product: CoinPackItem) {
        billingRepository.launchPurchaseFlow(activity, product.id)
    }

    /**
     * Buys one power-up for coins into the core inventory.
     * Calls [onResult] with true when the purchase succeeded.
     */
    fun buyPowerUp(item: PowerUpShopItem, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val paid = coinAccount.spendCoins(item.price)
            if (paid) {
                inventoryStore.addToInventory(item.inventoryId)
            }
            onResult(paid)
        }
    }
}
