package local.core.billing

data class BillingConfig(
    val productIds: List<String>,
    val coinAmountFor: (String) -> Int?
)
