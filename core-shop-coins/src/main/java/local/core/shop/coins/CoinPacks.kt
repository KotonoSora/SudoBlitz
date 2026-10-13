package local.core.shop.coins

import local.core.billing.DomainProduct
import java.util.Locale

data class CoinPackDefinition(
    val productId: String,
    val coinAmount: Int,
    val debugPrice: String,
    val description: String
)

/**
 * Pure coin-pack catalog. This file has no `R` imports; the drawable mapping
 * for coin amounts lives in CoinPackItems.kt in this same module.
 */
object CoinPacks {
    /**
     * Default description template (Flappy wording). Games must NOT edit this
     * constant — pass an explicit template to [definitionFor]/[formatDescription]
     * instead so core stays game-agnostic. No global mutable `var` (init-ordering
     * race: previews/early VMs would see Flappy text).
     */
    const val DEFAULT_DESC_TEMPLATE =
        "A pack of %d coins used to unlock powerful boosts like Extra Time, Hint, and Undo."

    fun formatDescription(coinAmount: Int, template: String = DEFAULT_DESC_TEMPLATE): String =
        String.format(Locale.US, template, coinAmount)

    private val definitions = listOf(
        CoinPackDefinition(
            productId = CoinPackIds.COINS_100,
            coinAmount = 100,
            debugPrice = "$0.29",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 100)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_500,
            coinAmount = 500,
            debugPrice = "$0.49",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_1000,
            coinAmount = 1000,
            debugPrice = "$0.69",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 1000)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_1500,
            coinAmount = 1500,
            debugPrice = "$0.99",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 1500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_2000,
            coinAmount = 2000,
            debugPrice = "$1.99",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 2000)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_2500,
            coinAmount = 2500,
            debugPrice = "$3.99",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 2500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_3000,
            coinAmount = 3000,
            debugPrice = "$4.99",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 3000)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_3500,
            coinAmount = 3500,
            debugPrice = "$7.99",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 3500)
        ),
        CoinPackDefinition(
            productId = CoinPackIds.COINS_4000,
            coinAmount = 4000,
            debugPrice = "$9.99",
            description = String.format(Locale.US, DEFAULT_DESC_TEMPLATE, 4000)
        )
    )

    fun getCoinAmount(productId: String): Int? {
        return definitions.firstOrNull { it.productId == productId }?.coinAmount
            ?: productId.removePrefix("coins_").toIntOrNull()
    }

    /**
     * Returns the definition with [template] applied to the description, so
     * per-game wording never requires forking core. Callers in game modules
     * pass their own template explicitly.
     */
    fun definitionFor(
        productId: String,
        template: String = DEFAULT_DESC_TEMPLATE
    ): CoinPackDefinition? {
        val def = definitions.firstOrNull { it.productId == productId } ?: return null
        return if (template == DEFAULT_DESC_TEMPLATE) def
        else def.copy(description = formatDescription(def.coinAmount, template))
    }

    fun formatCoinPackName(coinAmount: Int): String =
        String.format(Locale.US, "%,d Coins", coinAmount)

    val mockProducts: List<DomainProduct> = definitions.map { def ->
        DomainProduct(
            productId = def.productId,
            name = formatCoinPackName(def.coinAmount),
            formattedPrice = def.debugPrice
        )
    }
}
