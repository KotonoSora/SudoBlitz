package local.sudoku.ui.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import local.sudoku.ui.presentation.viewmodel.GameViewModel
import local.sudoku.ui.presentation.viewmodel.ProgressViewModel
import local.sudoku.ui.presentation.viewmodel.SettingsViewModel
import local.sudoku.ui.presentation.viewmodel.ShopViewModel

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(GameViewModel::class.java) -> {
                GameViewModel(
                    container.coinAccount,
                    container.inventoryStore,
                    container.recordGameFinishedUseCase,
                    container.usePowerUpUseCase,
                    container.startNewGameUseCase,
                    container.nextLevelUseCase,
                    container.inputNumberUseCase,
                    container.useHintUseCase,
                    container.undoMistakeUseCase
                ) as T
            }

            modelClass.isAssignableFrom(ShopViewModel::class.java) -> {
                ShopViewModel(
                    container.billingRepository,
                    container.coinAccount,
                    container.inventoryStore
                ) as T
            }

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    container.getAudioSettingsUseCase,
                    container.toggleAudioUseCase
                ) as T
            }

            modelClass.isAssignableFrom(ProgressViewModel::class.java) -> {
                ProgressViewModel(
                    container.getHighScoreUseCase,
                    container.getScoreHistoryUseCase,
                    container.statsRepository
                ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
