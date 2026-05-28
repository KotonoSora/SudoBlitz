package com.jn.numgrid.application.shop

import android.app.Activity
import com.jn.numgrid.domain.shop.ShopProduct
import com.jn.numgrid.domain.shop.ShopStatus
import kotlinx.coroutines.flow.StateFlow

interface ShopStoreGateway {
    val products: StateFlow<List<ShopProduct>>
    val status: StateFlow<ShopStatus>

    fun setMockProducts(products: List<ShopProduct>)
    fun purchase(activity: Activity, product: ShopProduct)
    fun release()
}

class ObserveShopProductsUseCase(private val gateway: ShopStoreGateway) {
    operator fun invoke(): StateFlow<List<ShopProduct>> = gateway.products
}

class ObserveShopStatusUseCase(private val gateway: ShopStoreGateway) {
    operator fun invoke(): StateFlow<ShopStatus> = gateway.status
}

class PurchaseProductUseCase(private val gateway: ShopStoreGateway) {
    operator fun invoke(activity: Activity, product: ShopProduct) {
        gateway.purchase(activity, product)
    }
}

class SetMockProductsUseCase(private val gateway: ShopStoreGateway) {
    operator fun invoke(products: List<ShopProduct>) {
        gateway.setMockProducts(products)
    }
}

class ReleaseShopUseCase(private val gateway: ShopStoreGateway) {
    operator fun invoke() {
        gateway.release()
    }
}
