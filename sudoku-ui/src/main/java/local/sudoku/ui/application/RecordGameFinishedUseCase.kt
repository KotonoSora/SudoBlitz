package local.sudoku.ui.application

import kotlinx.coroutines.flow.first
import local.core.ui.data.CoinAccount
import local.sudoku.engine.domain.model.RunEndCause
import local.sudoku.engine.domain.model.RunRecord
import local.sudoku.engine.domain.repository.RunHistoryRepository
import local.sudoku.ui.infrastructure.persistence.SudokuStatsRepository

/**
 * Records a finished game: stats counters + coin reward + run-history row.
 * High score is derived from runs (core), so this returns whether [record]
 * set a new high (for the win sound / highlight).
 */
class RecordGameFinishedUseCase(
    private val coinAccount: CoinAccount,
    private val runs: RunHistoryRepository,
    private val stats: SudokuStatsRepository
) {
    suspend operator fun invoke(record: RunRecord, streak: Int): Boolean {
        val previousHigh = runs.highScoreFlow.first()

        stats.incrementGamesPlayed()
        if (record.endCause == RunEndCause.VICTORY) {
            stats.incrementGamesWon()
        }
        stats.updateBestStreak(streak)
        if (record.reward > 0) {
            coinAccount.addCoins(record.reward)
        }
        runs.recordRun(record)

        return record.score > 0 && record.score > previousHigh
    }
}
