package local.core.ui.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import local.core.ui.data.db.RunDao
import local.core.ui.data.db.RunEntity

class RoomRunStore(private val dao: RunDao) : RunStore {
    override val highScoreFlow: Flow<Int> = dao.observeHighScore()

    override fun recentRunsFlow(limit: Int): Flow<List<RunRecord>> =
        dao.observeRecent(limit).map { list -> list.map { it.toRecord() } }

    override suspend fun recordRun(entry: RunRecord): Long {
        return dao.insert(entry.toEntity())
    }

    override suspend fun updateRun(id: Long, entry: RunRecord) {
        dao.update(entry.toEntity().copy(id = id))
    }
}

private fun RunEntity.toRecord(): RunRecord = RunRecord(
    id = id,
    mode = mode,
    level = level,
    score = score,
    reward = reward,
    durationMs = durationMs,
    pipesPassed = pipesPassed,
    endCause = endCause,
    shieldUses = shieldUses,
    multiplierUses = multiplierUses,
    autoPlayUses = autoPlayUses,
    deathX = deathX,
    deathY = deathY,
    startedAt = startedAt,
    endedAt = endedAt
)

private fun RunRecord.toEntity(): RunEntity = RunEntity(
    mode = mode,
    level = level,
    score = score,
    reward = reward,
    durationMs = durationMs,
    pipesPassed = pipesPassed,
    endCause = endCause,
    shieldUses = shieldUses,
    multiplierUses = multiplierUses,
    autoPlayUses = autoPlayUses,
    deathX = deathX,
    deathY = deathY,
    startedAt = startedAt,
    endedAt = endedAt
)
