package local.core.billing

/**
 * Narrow coin wallet. Implemented by the host app's settings/persistence layer so
 * `:core-billing` never depends on app or game modules.
 */
fun interface CoinSink {
    suspend fun addCoins(amount: Int)
}
