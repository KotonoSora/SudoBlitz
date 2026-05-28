package com.jn.numgrid.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jn.numgrid.application.shop.ObserveShopProductsUseCase
import com.jn.numgrid.application.shop.ObserveShopStatusUseCase
import com.jn.numgrid.application.shop.PurchaseProductUseCase
import com.jn.numgrid.application.shop.ReleaseShopUseCase
import com.jn.numgrid.application.shop.SetMockProductsUseCase
import com.jn.numgrid.application.shop.ShopStoreGateway
import com.jn.numgrid.billing.BillingShopStoreGateway
import com.jn.numgrid.data.CoinWalletGatewayAdapter
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.domain.shop.CoinWalletGateway
import com.jn.numgrid.domain.shop.ShopProduct
import com.jn.numgrid.domain.shop.ShopStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ShopViewModel(
    private val observeShopProducts: ObserveShopProductsUseCase,
    private val observeShopStatus: ObserveShopStatusUseCase,
    private val purchaseProduct: PurchaseProductUseCase,
    private val setMockProductsUseCase: SetMockProductsUseCase,
    private val releaseShopUseCase: ReleaseShopUseCase,
    walletGateway: CoinWalletGateway
) : ViewModel() {

    fun setMockProducts(products: List<ShopProduct>) {
        setMockProductsUseCase(products)
    }

    val products: StateFlow<List<ShopProduct>> = observeShopProducts()

    val status: StateFlow<ShopStatus> = observeShopStatus()

    val coins: StateFlow<Int> = walletGateway.coinsFlow.stateIn(
        viewModelScope, SharingStarted.Eagerly, 0
    )

    fun buyProduct(activity: Activity, product: ShopProduct) {
        purchaseProduct(activity, product)
    }

    override fun onCleared() {
        super.onCleared()
        releaseShopUseCase()
    }

    companion object {
        fun provideFactory(
            application: Application,
            repository: UserPreferencesRepository,
            isPreview: Boolean = false
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
                    val storeGateway: ShopStoreGateway =
                        BillingShopStoreGateway(application, repository, isPreview)
                    val walletGateway: CoinWalletGateway = CoinWalletGatewayAdapter(repository)
                    return ShopViewModel(
                        observeShopProducts = ObserveShopProductsUseCase(storeGateway),
                        observeShopStatus = ObserveShopStatusUseCase(storeGateway),
                        purchaseProduct = PurchaseProductUseCase(storeGateway),
                        setMockProductsUseCase = SetMockProductsUseCase(storeGateway),
                        releaseShopUseCase = ReleaseShopUseCase(storeGateway),
                        walletGateway = walletGateway
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
