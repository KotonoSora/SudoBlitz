package com.jn.numgrid.viewmodel

import android.app.Activity
import com.jn.numgrid.application.shop.ObserveShopProductsUseCase
import com.jn.numgrid.application.shop.ObserveShopStatusUseCase
import com.jn.numgrid.application.shop.PurchaseProductUseCase
import com.jn.numgrid.application.shop.ReleaseShopUseCase
import com.jn.numgrid.application.shop.SetMockProductsUseCase
import com.jn.numgrid.application.shop.ShopStoreGateway
import com.jn.numgrid.domain.shop.CoinWalletGateway
import com.jn.numgrid.domain.shop.ShopProduct
import com.jn.numgrid.domain.shop.ShopStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShopViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `view model exposes shop and wallet state and delegates set mock products`() = runTest {
        val gateway = FakeShopStoreGateway()
        val wallet = FakeCoinWalletGateway(350)
        val viewModel = ShopViewModel(
            observeShopProducts = ObserveShopProductsUseCase(gateway),
            observeShopStatus = ObserveShopStatusUseCase(gateway),
            purchaseProduct = PurchaseProductUseCase(gateway),
            setMockProductsUseCase = SetMockProductsUseCase(gateway),
            releaseShopUseCase = ReleaseShopUseCase(gateway),
            walletGateway = wallet
        )

        val custom = listOf(
            ShopProduct(
                productId = "coins_1500",
                title = "1500 Coins",
                price = "$0.89~",
                coinAmount = 1500
            )
        )
        viewModel.setMockProducts(custom)

        advanceUntilIdle()

        assertEquals(350, viewModel.coins.value)
        assertEquals(custom, viewModel.products.value)
        assertEquals(ShopStatus.CONNECTED, viewModel.status.value)
    }

    @Test
    fun `view model handles store error status`() = runTest {
        val gateway = FakeShopStoreGateway()
        gateway.status.value = ShopStatus.ERROR
        val wallet = FakeCoinWalletGateway(0)
        val viewModel = ShopViewModel(
            observeShopProducts = ObserveShopProductsUseCase(gateway),
            observeShopStatus = ObserveShopStatusUseCase(gateway),
            purchaseProduct = PurchaseProductUseCase(gateway),
            setMockProductsUseCase = SetMockProductsUseCase(gateway),
            releaseShopUseCase = ReleaseShopUseCase(gateway),
            walletGateway = wallet
        )

        advanceUntilIdle()
        assertEquals(ShopStatus.ERROR, viewModel.status.value)
    }

    private class FakeCoinWalletGateway(coins: Int) : CoinWalletGateway {
        private val coinsState = MutableStateFlow(coins)
        override val coinsFlow: Flow<Int> = coinsState
    }

    private class FakeShopStoreGateway : ShopStoreGateway {
        override val products = MutableStateFlow(
            listOf(ShopProduct("coins_100", "100 Coins", "$0.39~", 100))
        )
        override val status = MutableStateFlow(ShopStatus.CONNECTED)

        var released: Boolean = false

        override fun setMockProducts(products: List<ShopProduct>) {
            this.products.value = products
        }

        override fun purchase(activity: Activity, product: ShopProduct) {
            // Not needed for this unit test.
        }

        override fun release() {
            released = true
        }
    }
}
