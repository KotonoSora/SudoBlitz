package local.sudoku.engine.domain.service

import org.junit.Assert.assertEquals
import org.junit.Test

class RewardPolicyTest {

    @Test
    fun `forVictory returns expected breakdown`() {
        val reward = RewardPolicy.forVictory(timeRemaining = 125, boardSize = 6)

        assertEquals(28, reward.totalCoins)
        assertEquals(10, reward.base)
        assertEquals(12, reward.timeBonus)
        assertEquals(6, reward.sizeBonus)
        assertEquals("Base: 10, Time: +12, Size: +6", reward.details)
    }
}

