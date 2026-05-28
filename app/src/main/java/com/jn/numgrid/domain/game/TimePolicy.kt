package com.jn.numgrid.domain.game

object TimePolicy {
    fun initialTimeSeconds(size: Int): Int = when (size) {
        4 -> 60
        6 -> 180
        9 -> 300
        else -> 180
    }

    fun bonusTimeForNextLevel(size: Int): Int = initialTimeSeconds(size) / 2
}

