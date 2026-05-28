package com.jn.numgrid.billing

import android.app.Activity
import android.content.Context
import com.jn.numgrid.application.shop.ShopStoreGateway
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.domain.shop.ShopProduct
import com.jn.numgrid.domain.shop.ShopStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BillingShopStoreGateway(
    context: Context,
    repository: UserPreferencesRepository,
    isPreview: Boolean = false
) : ShopStoreGateway {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val billingManager = BillingManager(
        context = context,
        preferencesRepository = repository,
        isPreview = isPreview
    )

    override val products: StateFlow<List<ShopProduct>> = billingManager.products
        .map { list -> list.map { it.toDomain() } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val status: StateFlow<ShopStatus> = billingManager.status
        .map { it.toDomain() }
        .stateIn(scope, SharingStarted.Eagerly, ShopStatus.IDLE)

    override fun setMockProducts(products: List<ShopProduct>) {
        billingManager.setMockProducts(products.map { it.toData() })
    }

    override fun purchase(activity: Activity, product: ShopProduct) {
        // We need the original StoreProduct for originalDetails if available.
        // We can find it in the billingManager's products.
        val original = billingManager.products.value.find { it.productId == product.productId }
            ?: product.toData()
        billingManager.launchBillingFlow(activity, original)
    }

    override fun release() {
        billingManager.release()
        scope.cancel()
    }

    private fun StoreProduct.toDomain() = ShopProduct(
        productId = productId,
        title = title,
        price = price,
        coinAmount = coinAmount
    )

    private fun ShopProduct.toData() = StoreProduct(
        productId = productId,
        title = title,
        price = price,
        coinAmount = coinAmount
    )

    private fun BillingStatus.toDomain() = when (this) {
        BillingStatus.IDLE -> ShopStatus.IDLE
        BillingStatus.CONNECTING -> ShopStatus.CONNECTING
        BillingStatus.CONNECTED -> ShopStatus.CONNECTED
        BillingStatus.ERROR -> ShopStatus.ERROR
        BillingStatus.EMPTY -> ShopStatus.EMPTY
    }
}
