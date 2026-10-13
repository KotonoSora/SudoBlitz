package local.core.ui.data

import kotlinx.coroutines.flow.Flow

/** Coin-balance port. Game-agnostic; amounts are plain Ints. */
interface CoinAccount {
    val coinsFlow: Flow<Int>
    suspend fun addCoins(amount: Int)
    suspend fun spendCoins(amount: Int): Boolean
}
