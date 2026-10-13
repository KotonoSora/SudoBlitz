package local.sudoku.engine.application

import kotlinx.coroutines.flow.Flow
import local.sudoku.engine.domain.repository.RunHistoryRepository

class GetHighScoreUseCase(private val runs: RunHistoryRepository) {
    operator fun invoke(): Flow<Int> = runs.highScoreFlow
}
