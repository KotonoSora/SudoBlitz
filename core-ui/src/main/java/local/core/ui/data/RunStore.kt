package local.core.ui.data

import kotlinx.coroutines.flow.Flow

/** Run-history port, backed by Room. History is unbounded in v1 (no pruning). */
interface RunStore {
    val highScoreFlow: Flow<Int>
    fun recentRunsFlow(limit: Int): Flow<List<RunRecord>>

    /** Inserts a run, returning its row id for later [updateRun] calls. */
    suspend fun recordRun(entry: RunRecord): Long

    /** Overwrites the row stored under [id] (e.g. after a revive continues the run). */
    suspend fun updateRun(id: Long, entry: RunRecord)
}
