package local.core.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
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
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BillingManagerImpl(
    context: Context,
    private val wallet: CoinSink,
    private val config: BillingConfig
) : BillingRepository {

    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _status = MutableStateFlow(BillingStatus.IDLE)
    override val status = _status.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _products = MutableStateFlow<List<ProductDetails>>(emptyList())
    override val products = _products.map { list ->
        list.map {
            DomainProduct(
                productId = it.productId,
                name = it.name,
                formattedPrice = it.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
            )
        }
    }

    init {
        startConnection()
    }

    override fun startConnection() {
        if (_status.value == BillingStatus.CONNECTING || _status.value == BillingStatus.CONNECTED) return

        _status.value = BillingStatus.CONNECTING
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _status.value = BillingStatus.CONNECTED
                    queryProducts(config.productIds)
                    queryPurchases()
                } else {
                    _status.value = BillingStatus.ERROR
                }
            }

            override fun onBillingServiceDisconnected() {
                _status.value = BillingStatus.IDLE
            }
        })
    }

    private fun queryProducts(productIds: List<String>) {
        if (productIds.isEmpty()) return

        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                productIds.map {
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(it)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                }
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val list = productDetailsResult.productDetailsList
                _products.value = list
                if (list.isEmpty()) {
                    _status.value = BillingStatus.EMPTY
                }
            } else {
                _status.value = BillingStatus.ERROR
            }
        }
    }

    private fun queryPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        handlePurchase(purchase)
                    }
                }
            }
        }
    }

    override fun launchPurchaseFlow(activity: Activity, productId: String) {
        val productDetails = _products.value.find { it.productId == productId } ?: return

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

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val isCoinPack = purchase.products.any { config.coinAmountFor(it) != null }

            if (isCoinPack) {
                consumeCoinPack(purchase)
            } else if (!purchase.isAcknowledged) {
                acknowledgePurchaseIfNeeded(purchase)
            }
        }
    }

    private fun acknowledgePurchaseIfNeeded(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                queryPurchases()
            }
        }
    }

    private fun consumeCoinPack(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                managerScope.launch {
                    purchase.products.forEach { productId ->
                        config.coinAmountFor(productId)?.let { coinAmount ->
                            wallet.addCoins(coinAmount)
                        }
                    }
                }
                queryPurchases()
            }
        }
    }

    fun release() {
        billingClient.endConnection()
        managerScope.cancel()
    }
}
