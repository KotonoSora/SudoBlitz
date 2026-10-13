package local.sudoku.engine.domain.repository

import kotlinx.coroutines.flow.Flow
import local.sudoku.engine.domain.model.RunRecord

interface RunHistoryRepository {
    val highScoreFlow: Flow<Int>
    fun recentRunsFlow(limit: Int): Flow<List<RunRecord>>

    /** Inserts a run, returning its row id for later [updateRun] calls. */
    suspend fun recordRun(record: RunRecord): Long

    /** Overwrites the row stored under [id]. */
    suspend fun updateRun(id: Long, record: RunRecord)
}
