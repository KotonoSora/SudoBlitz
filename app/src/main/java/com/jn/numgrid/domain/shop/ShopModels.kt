package com.jn.numgrid.domain.shop

data class ShopProduct(
    val productId: String,
    val title: String,
    val price: String?,
    val coinAmount: Int
)

enum class ShopStatus {
    IDLE, CONNECTING, CONNECTED, ERROR, EMPTY
}
