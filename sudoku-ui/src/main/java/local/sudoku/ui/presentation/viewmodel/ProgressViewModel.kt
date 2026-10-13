package local.sudoku.ui.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import local.sudoku.engine.application.GetHighScoreUseCase
import local.sudoku.engine.application.GetScoreHistoryUseCase
import local.sudoku.engine.domain.model.RunRecord
import local.sudoku.ui.infrastructure.persistence.SudokuStatsRepository

class ProgressViewModel(
    getHighScore: GetHighScoreUseCase,
    getScoreHistory: GetScoreHistoryUseCase,
    statsRepository: SudokuStatsRepository
) : ViewModel() {

    /** High score derives from runs (core `game_history.db`). */
    val highScore: StateFlow<Int> = getHighScore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestStreak: StateFlow<Int> = statsRepository.bestStreakFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gamesPlayed: StateFlow<Int> = statsRepository.gamesPlayedFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val gamesWon: StateFlow<Int> = statsRepository.gamesWonFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentRuns: StateFlow<List<RunRecord>> = getScoreHistory(50)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
