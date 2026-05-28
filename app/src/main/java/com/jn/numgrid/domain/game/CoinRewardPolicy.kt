package com.jn.numgrid.domain.game

data class CoinRewardBreakdown(
    val totalCoins: Int,
    val base: Int,
    val timeBonus: Int,
    val sizeBonus: Int,
    val details: String
)

object CoinRewardPolicy {
    fun forVictory(timeRemaining: Int, boardSize: Int): CoinRewardBreakdown {
        val base = 10
        val timeBonus = timeRemaining / 10
        val sizeBonus = boardSize
        val total = base + timeBonus + sizeBonus
        val details = "Base: $base, Time: +$timeBonus, Size: +$sizeBonus"
        return CoinRewardBreakdown(
            totalCoins = total,
            base = base,
            timeBonus = timeBonus,
            sizeBonus = sizeBonus,
            details = details
        )
    }
}

