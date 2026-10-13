package local.sudoku.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import local.core.billing.BillingRepository
import local.core.billing.BillingStatus
import local.core.shop.coins.CoinPackItem
import local.core.ui.data.CoinAccount
import local.sudoku.shop.application.CoinShopConfig

class ShopViewModel(
    private val billingRepository: BillingRepository,
    private val coinAccount: CoinAccount
) : ViewModel() {

    val products: StateFlow<List<CoinPackItem>> = billingRepository.products
        .map { list -> list.map { CoinShopConfig.coinPackItem(it) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val status: StateFlow<BillingStatus> = billingRepository.status
        .stateIn(viewModelScope, SharingStarted.Eagerly, BillingStatus.IDLE)

    val coins: StateFlow<Int> = coinAccount.coinsFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, 0
    )

    init {
        billingRepository.startConnection()
    }

    fun buyCoinPack(activity: android.app.Activity, product: CoinPackItem) {
        billingRepository.launchPurchaseFlow(activity, product.id)
    }

    fun retryConnection() {
        billingRepository.startConnection()
    }
}
