package local.core.ui.data

/**
 * One played run. Game-agnostic: mode/end-cause are plain strings owned by
 * the game; the engine maps them to its own taxonomy in the UI adapters.
 */
data class RunRecord(
    val id: Long = 0,
    val mode: String,
    val level: Int,
    val score: Int,
    val reward: Int,
    val durationMs: Long,
    val pipesPassed: Int,
    val endCause: String,
    val shieldUses: Int = 0,
    val multiplierUses: Int = 0,
    val autoPlayUses: Int = 0,
    val deathX: Float? = null,
    val deathY: Float? = null,
    val startedAt: Long,
    val endedAt: Long
)
