package local.sudoku.engine.domain.service

data class RewardBreakdown(
    val totalCoins: Int,
    val base: Int,
    val timeBonus: Int,
    val sizeBonus: Int,
    val details: String
)

/**
 * Domain service: coin-reward policy for victories. Pure rules, no ports.
 */
object RewardPolicy {
    fun forVictory(timeRemaining: Int, boardSize: Int): RewardBreakdown {
        val base = 10
        val timeBonus = timeRemaining / 10
        val sizeBonus = boardSize
        val total = base + timeBonus + sizeBonus
        val details = "Base: $base, Time: +$timeBonus, Size: +$sizeBonus"
        return RewardBreakdown(
            totalCoins = total,
            base = base,
            timeBonus = timeBonus,
            sizeBonus = sizeBonus,
            details = details
        )
    }
}
