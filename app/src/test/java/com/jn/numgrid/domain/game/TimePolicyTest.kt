package com.jn.numgrid.domain.game

import org.junit.Assert.assertEquals
import org.junit.Test

class TimePolicyTest {

    @Test
    fun `initialTimeSeconds returns expected values for supported board sizes`() {
        assertEquals(60, TimePolicy.initialTimeSeconds(4))
        assertEquals(180, TimePolicy.initialTimeSeconds(6))
        assertEquals(300, TimePolicy.initialTimeSeconds(9))
    }

    @Test
    fun `bonusTimeForNextLevel is always half of initial time`() {
        assertEquals(30, TimePolicy.bonusTimeForNextLevel(4))
        assertEquals(90, TimePolicy.bonusTimeForNextLevel(6))
        assertEquals(150, TimePolicy.bonusTimeForNextLevel(9))
    }
}

