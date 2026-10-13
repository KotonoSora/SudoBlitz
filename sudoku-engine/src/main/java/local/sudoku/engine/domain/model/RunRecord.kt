package local.sudoku.engine.domain.model

/**
 * One played game, engine-owned. Field-for-field compatible with core's
 * RunRecord so the ui adapters map without a Room migration (Option A):
 * - mode = difficulty name (EASY/MEDIUM/HARD/VERY_HARD)
 * - level = board size (4/6/9)
 * - pipesPassed = mistake count (repurposed Flappy field, no schema change)
 * - shieldUses = hint uses, multiplierUses = undo uses, autoPlayUses = extra-time uses
 * - deathX/deathY always null (no death position in Sudoku)
 */
data class RunRecord(
    val mode: String,
    val level: Int,
    val score: Int,
    val reward: Int,
    val durationMs: Long,
    val pipesPassed: Int,
    val endCause: RunEndCause,
    val shieldUses: Int = 0,
    val multiplierUses: Int = 0,
    val autoPlayUses: Int = 0,
    val deathX: Float? = null,
    val deathY: Float? = null,
    val startedAt: Long,
    val endedAt: Long
)
