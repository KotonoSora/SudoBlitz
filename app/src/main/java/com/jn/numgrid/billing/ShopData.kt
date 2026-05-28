package com.jn.numgrid.billing

object ShopData {
    private val productMap = mapOf(
        "coins_100" to 100,
        "coins_500" to 500,
        "coins_1000" to 1000,
        "coins_1500" to 1500,
        "coins_2000" to 2000,
        "coins_2500" to 2500,
        "coins_3000" to 3000,
        "coins_3500" to 3500,
        "coins_4000" to 4000
    )

    private val debugPriceMap = mapOf(
        "coins_100" to "$0.39~",
        "coins_500" to "$0.59~",
        "coins_1000" to "$0.79~",
        "coins_1500" to "$0.89~",
        "coins_2000" to "$0.99~",
        "coins_2500" to "$1.99~",
        "coins_3000" to "$3.99~",
        "coins_3500" to "$5.99~",
        "coins_4000" to "$7.99~"
    )

    val productIds = productMap.keys.toList()

    fun getCoinAmount(productId: String): Int = productMap[productId] ?: 0

    fun getDebugPrice(productId: String): String = debugPriceMap[productId] ?: "$0.99"
}
