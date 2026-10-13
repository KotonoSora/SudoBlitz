package local.core.billing

import android.app.Activity
import kotlinx.coroutines.flow.Flow

enum class BillingStatus {
    IDLE, CONNECTING, CONNECTED, ERROR, EMPTY
}

data class DomainProduct(
    val productId: String,
    val name: String,
    val formattedPrice: String
)

interface BillingRepository {
    val status: Flow<BillingStatus>
    val products: Flow<List<DomainProduct>>
    fun startConnection()
    fun launchPurchaseFlow(activity: Activity, productId: String)
}
