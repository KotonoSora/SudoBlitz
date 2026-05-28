package com.jn.numgrid.domain.game

import org.junit.Assert.assertEquals
import org.junit.Test

class CoinRewardPolicyTest {

    @Test
    fun `forVictory returns expected breakdown`() {
        val reward = CoinRewardPolicy.forVictory(timeRemaining = 125, boardSize = 6)

        assertEquals(28, reward.totalCoins)
        assertEquals(10, reward.base)
        assertEquals(12, reward.timeBonus)
        assertEquals(6, reward.sizeBonus)
        assertEquals("Base: 10, Time: +12, Size: +6", reward.details)
    }
}

