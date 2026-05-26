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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StoreProduct(
    val productId: String,
    val title: String,
    val price: String,
    val coinAmount: Int,
    val originalDetails: ProductDetails? = null
)

class BillingManager(
    private val context: Context,
    private val preferencesRepository: UserPreferencesRepository,
    private val isPreview: Boolean = false
) : PurchasesUpdatedListener {

    companion object {
        private val productMap = mapOf(
            "coins_100" to 100,
            "coins_500" to 500,
            "coins_1000" to 1000,
            "coins_1500" to 1500,
            "coins_2000" to 2000,
            "coins_2500" to 2500,
            "coins_3000" to 3000,
            "coins_3500" to 3500,
            "coins_4000" to 4000
        )

        val productIds = productMap.keys.toList()

        fun getCoinAmount(productId: String): Int = productMap[productId] ?: 0
    }

    private var isDebug = isPreview

    private val pendingPurchasesParams by lazy {
        PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
    }

    private val billingClient: BillingClient? by lazy {
        if (isPreview) {
            null
        } else {
            try {
                BillingClient.newBuilder(context).setListener(this)
                    .enablePendingPurchases(pendingPurchasesParams).build()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private val _products = MutableStateFlow<List<StoreProduct>>(
        if (isPreview) {
            productIds.map { id ->
                StoreProduct(id, "${getCoinAmount(id)} Coins", "$0.99", getCoinAmount(id))
            }
        } else emptyList()
    )
    val products: StateFlow<List<StoreProduct>> = _products.asStateFlow()

    fun setMockProducts(mockProducts: List<StoreProduct>) {
        if (isPreview) {
            _products.value = mockProducts
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        if (isPreview) {
            isDebug = true
            // products already initialized in _products declaration
        } else {
            try {
                isDebug = (context.applicationInfo?.let { (it.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0 } ?: true)
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

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to Google Play
            }
        })
    }

    private fun queryProducts() {
        if (isDebug) {
            val mockProducts = productIds.map { id ->
                StoreProduct(id, "${getCoinAmount(id)} Coins", "$0.99", getCoinAmount(id))
            }
            _products.value = mockProducts
            return
        }

        val productList = productIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder().setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP).build()
        }

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient?.queryProductDetailsAsync(params) { billingResult, result ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Sort the products based on their coin value
                val sortedProducts = result.productDetailsList.sortedBy { details ->
                        getCoinAmount(details.productId)
                    }.map { details ->
                        StoreProduct(
                            productId = details.productId,
                            title = details.title,
                            price = details.oneTimePurchaseOfferDetails?.formattedPrice
                                ?: "Unknown",
                            coinAmount = getCoinAmount(details.productId),
                            originalDetails = details
                        )
                    }
                _products.value = sortedProducts
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

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
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
        scope.launch {
            var coinsToAdd = 0
            for (productId in productIds) {
                coinsToAdd += getCoinAmount(productId)
            }
            if (coinsToAdd > 0) {
                preferencesRepository.updateCoins(coinsToAdd)
            }
        }
    }

    fun endConnection() {
        billingClient?.endConnection()
    }
}
