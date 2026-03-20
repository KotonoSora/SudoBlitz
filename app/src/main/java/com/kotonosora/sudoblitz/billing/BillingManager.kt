package com.kotonosora.sudoblitz.billing

import android.app.Activity
import android.content.Context
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
import com.kotonosora.sudoblitz.data.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val context: Context,
    private val preferencesRepository: UserPreferencesRepository
) : PurchasesUpdatedListener {

    private val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .build()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(pendingPurchasesParams)
        .build()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    val products: StateFlow<List<ProductDetails>> = _products.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
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
        val productIds = listOf(
            "coins_100", "coins_500", "coins_1000", "coins_1500",
            "coins_2000", "coins_2500", "coins_3000", "coins_3500", "coins_4000"
        )

        val productList = productIds.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Sort the products based on their coin value
                val sortedProducts = productDetailsList.sortedBy { product ->
                    when (product.productId) {
                        "coins_100" -> 100
                        "coins_500" -> 500
                        "coins_1000" -> 1000
                        "coins_1500" -> 1500
                        "coins_2000" -> 2000
                        "coins_2500" -> 2500
                        "coins_3000" -> 3000
                        "coins_3500" -> 3500
                        "coins_4000" -> 4000
                        else -> Int.MAX_VALUE
                    }
                }
                _products.value = sortedProducts
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
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
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.consumeAsync(consumeParams) { billingResult, _ ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Grant coins to user
                        grantCoins(purchase.products)
                    }
                }
            } else {
                val consumeParams = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.consumeAsync(consumeParams) { billingResult, _ ->
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
                coinsToAdd += when (productId) {
                    "coins_100" -> 100
                    "coins_500" -> 500
                    "coins_1000" -> 1000
                    "coins_1500" -> 1500
                    "coins_2000" -> 2000
                    "coins_2500" -> 2500
                    "coins_3000" -> 3000
                    "coins_3500" -> 3500
                    "coins_4000" -> 4000
                    else -> 0
                }
            }
            if (coinsToAdd > 0) {
                preferencesRepository.updateCoins(coinsToAdd)
            }
        }
    }

    fun endConnection() {
        billingClient.endConnection()
    }
}
