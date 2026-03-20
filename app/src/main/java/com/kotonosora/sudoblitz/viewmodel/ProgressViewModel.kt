package com.kotonosora.sudoblitz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kotonosora.sudoblitz.data.GameRecord
import com.kotonosora.sudoblitz.data.GameRecordDao
import com.kotonosora.sudoblitz.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(
    private val repository: UserPreferencesRepository,
    private val gameRecordDao: GameRecordDao
) : ViewModel() {

    val highScore: StateFlow<Int> = repository.highScoreFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestStreak: StateFlow<Int> = repository.bestStreakFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val recentRecords: StateFlow<List<GameRecord>> = gameRecordDao.getRecentRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        fun provideFactory(
            repository: UserPreferencesRepository,
            gameRecordDao: GameRecordDao
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProgressViewModel(repository, gameRecordDao) as T
                }
            }
    }
}
