package local.sudoku.engine.application

import kotlinx.coroutines.flow.Flow
import local.sudoku.engine.domain.model.RunRecord
import local.sudoku.engine.domain.repository.RunHistoryRepository

class GetScoreHistoryUseCase(private val runs: RunHistoryRepository) {
    operator fun invoke(limit: Int = 50): Flow<List<RunRecord>> = runs.recentRunsFlow(limit)
}
