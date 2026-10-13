package local.sudoku.shop.domain.service

import local.sudoku.engine.domain.model.PowerUpType
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The string contract: shop catalog power-up IDs MUST equal engine
 * [PowerUpType] IDs. Otherwise [local.sudoku.engine.application.UsePowerUpUseCase]
 * rejects purchases and inventory never drains.
 */
class PowerUpCatalogContractTest {

    @Test
    fun `shop inventory ids match engine power-up ids`() {
        val engineIds = PowerUpType.entries.map { it.id }.toSet()
        val shopIds = PowerUps.getPowerUpItems()
            .map { (it as local.sudoku.shop.domain.model.PowerUpShopItem).inventoryId }
            .toSet()
        assertEquals(engineIds, shopIds)
    }
}
