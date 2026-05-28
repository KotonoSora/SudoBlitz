package com.jn.numgrid.data

import com.jn.numgrid.domain.shop.CoinWalletGateway
import kotlinx.coroutines.flow.Flow

class CoinWalletGatewayAdapter(
    private val repository: UserPreferencesRepository
) : CoinWalletGateway {
    override val coinsFlow: Flow<Int>
        get() = repository.coinsFlow
}

