package com.jn.numgrid.domain.shop

import kotlinx.coroutines.flow.Flow

interface CoinWalletGateway {
    val coinsFlow: Flow<Int>
}

