package com.jn.numgrid.billing

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.jn.numgrid.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoreProduct(
    val productId: String,
    val title: String,
    val price: String?,
    val coinAmount: Int,
    val originalDetails: ProductDetails? = null
)

enum class BillingStatus {
    IDLE, CONNECTING, CONNECTED, ERROR, EMPTY
}

class BillingManager(
    private val context: Context,
    private val preferencesRepository: UserPreferencesRepository,
    private val isPreview: Boolean = false
) {

    companion object {
        fun getDebugProducts(): List<StoreProduct> = ShopData.productIds.map { id ->
            val coinAmount = ShopData.getCoinAmount(id)
            StoreProduct(
                productId = id,
                title = "$coinAmount Coins",
                price = ShopData.getDebugPrice(id),
                coinAmount = coinAmount
            )
        }
    }

    private var isDebug = isPreview

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow(BillingStatus.IDLE)
    val status = _status.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private val pendingPurchasesParams by lazy {
        PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
    }

    private val billingClient: BillingClient? by lazy {
        if (isPreview) {
            null
        } else {
            try {
                BillingClient.newBuilder(context).setListener(purchasesUpdatedListener)
                    .enablePendingPurchases(pendingPurchasesParams).build()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private val _products = MutableStateFlow<List<StoreProduct>>(
        if (isPreview) getDebugProducts() else emptyList()
    )
    val products: StateFlow<List<StoreProduct>> = _products.asStateFlow()

    fun setMockProducts(mockProducts: List<StoreProduct>) {
        if (isPreview) {
            _products.value = mockProducts
            if (mockProducts.isEmpty()) {
                _status.value = BillingStatus.EMPTY
            } else {
                _status.value = BillingStatus.CONNECTED
            }
        }
    }


    init {
        if (isPreview) {
            isDebug = true
            // products already initialized in _products declaration
            if (_products.value.isEmpty()) {
                _status.value = BillingStatus.EMPTY
            } else {
                _status.value = BillingStatus.CONNECTED
            }
        } else {
            try {
                isDebug =
                    (context.applicationInfo?.let { (it.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0 }
                        ?: true)
            } catch (e: Exception) {
                e.printStackTrace()
                isDebug = true
            }

            if (isDebug) {
                queryProducts()
            } else {
                startConnection()
            }
        }
    }

    fun startConnection() {
        if (_status.value == BillingStatus.CONNECTING || _status.value == BillingStatus.CONNECTED) return

        _status.value = BillingStatus.CONNECTING
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _status.value = BillingStatus.CONNECTED
                    queryProducts()
                } else {
                    _status.value = BillingStatus.ERROR
                }
            }

            override fun onBillingServiceDisconnected() {
                _status.value = BillingStatus.IDLE
            }
        })
    }

    private fun queryProducts() {
        if (isDebug) {
            val debugProducts = getDebugProducts()
            _products.value = debugProducts
            if (debugProducts.isEmpty()) {
                _status.value = BillingStatus.EMPTY
            } else {
                _status.value = BillingStatus.CONNECTED
            }
            return
        }

        val productList = ShopData.productIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder().setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP).build()
        }

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val detailsList = result.productDetailsList
                if (detailsList.isEmpty()) {
                    _status.value = BillingStatus.EMPTY
                }
                // Sort the products based on their coin value
                val sortedProducts = detailsList.sortedBy { details ->
                    ShopData.getCoinAmount(details.productId)
                }.map { details ->
                    StoreProduct(
                        productId = details.productId,
                        title = details.title,
                        price = details.oneTimePurchaseOfferDetails?.formattedPrice,
                        coinAmount = ShopData.getCoinAmount(details.productId),
                        originalDetails = details
                    )
                }
                _products.value = sortedProducts
            } else {
                _status.value = BillingStatus.ERROR
            }
        }
    }

    fun launchBillingFlow(activity: Activity, product: StoreProduct) {
        if (isDebug && product.originalDetails == null) {
            // Mock purchase flow
            grantCoins(listOf(product.productId))
            return
        }

        val originalDetails = product.originalDetails ?: return

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(originalDetails)
                .build()
        )

        val billingFlowParams =
            BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                .build()

        billingClient?.launchBillingFlow(activity, billingFlowParams)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val consumeParams =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()

                billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Grant coins to user
                        grantCoins(purchase.products)
                    }
                }
            } else {
                val consumeParams =
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()

                billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Already acknowledged but grant coins and consume if applicable
                        grantCoins(purchase.products)
                    }
                }
            }
        }
    }

    private fun grantCoins(productIds: List<String>) {
        managerScope.launch {
            var coinsToAdd = 0
            for (productId in productIds) {
                coinsToAdd += ShopData.getCoinAmount(productId)
            }
            if (coinsToAdd > 0) {
                preferencesRepository.updateCoins(coinsToAdd)
            }
        }
    }

    fun release() {
        billingClient?.endConnection()
        managerScope.cancel()
    }
}
