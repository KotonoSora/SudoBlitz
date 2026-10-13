package local.core.ui.usecase

import kotlinx.coroutines.flow.Flow
import local.core.ui.data.CoinAccount

class GetCoinsUseCase(private val coins: CoinAccount) {
    operator fun invoke(): Flow<Int> = coins.coinsFlow
}
