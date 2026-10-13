package local.core.shop.coins

import local.core.billing.DomainProduct

data class CoinPackItem(
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val coinAmount: Int,
    val imageRes: Int
)

fun mapToCoinPack(product: DomainProduct, template: String = CoinPacks.DEFAULT_DESC_TEMPLATE): CoinPackItem {
    val def = CoinPacks.definitionFor(product.productId, template)
    val coinAmount = def?.coinAmount ?: CoinPacks.getCoinAmount(product.productId) ?: 0

    return CoinPackItem(
        id = product.productId,
        name = def?.let { CoinPacks.formatCoinPackName(it.coinAmount) } ?: product.name,
        description = def?.description ?: "",
        price = product.formattedPrice,
        coinAmount = coinAmount,
        imageRes = getCoinPackImageRes(coinAmount)
    )
}

private fun getCoinPackImageRes(coinAmount: Int): Int = when {
    coinAmount <= 100 -> R.drawable.img_coins_100
    coinAmount <= 500 -> R.drawable.img_coins_500
    coinAmount <= 1000 -> R.drawable.img_coins_1000
    coinAmount <= 1500 -> R.drawable.img_coins_1000
    coinAmount <= 2000 -> R.drawable.img_coins_2000
    else -> R.drawable.img_coins_2000
}
