package local.sudoku.shop.application

import local.core.billing.DomainProduct
import local.core.shop.coins.CoinPackItem
import local.core.shop.coins.CoinPacks
import local.core.shop.coins.mapToCoinPack

/**
 * Per-game coin-shop copy. Passed explicitly to core [CoinPacks] helpers —
 * core keeps only its fallback template, never a per-game global.
 */
object CoinShopConfig {
    const val DESCRIPTION_TEMPLATE =
        "A pack of %d coins used to buy Extra Time, Hint, and Undo power-ups."

    fun coinPackItem(product: DomainProduct): CoinPackItem =
        mapToCoinPack(product, DESCRIPTION_TEMPLATE)
}
