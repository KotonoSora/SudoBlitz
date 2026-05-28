package com.jn.numgrid.application.shop

import android.app.Activity
import com.jn.numgrid.domain.shop.ShopProduct
import com.jn.numgrid.domain.shop.ShopStatus
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShopUseCasesTest {

    @Test
    fun `observe use cases expose gateway state`() {
        val gateway = FakeShopStoreGateway()

        val products = ObserveShopProductsUseCase(gateway).invoke().value
        val status = ObserveShopStatusUseCase(gateway).invoke().value

        assertEquals(1, products.size)
        assertEquals(ShopStatus.CONNECTED, status)
    }

    @Test
    fun `set mock products and release use cases delegate to gateway`() {
        val gateway = FakeShopStoreGateway()
        val custom = listOf(
            ShopProduct(
                productId = "coins_500",
                title = "500 Coins",
                price = "$0.59~",
                coinAmount = 500
            )
        )

        SetMockProductsUseCase(gateway).invoke(custom)
        ReleaseShopUseCase(gateway).invoke()

        assertEquals(custom, gateway.products.value)
        assertTrue(gateway.released)
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
