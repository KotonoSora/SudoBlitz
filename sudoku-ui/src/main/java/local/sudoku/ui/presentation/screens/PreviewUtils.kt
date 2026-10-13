package local.sudoku.ui.presentation.screens

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import local.core.billing.BillingRepository
import local.core.billing.BillingStatus
import local.core.billing.DomainProduct
import local.core.shop.coins.CoinPackItem
import local.core.shop.coins.CoinPacks
import local.core.ui.data.AudioSettings
import local.core.ui.data.CoinAccount
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.ToggleAudioUseCase
import local.sudoku.engine.application.GetHighScoreUseCase
import local.sudoku.engine.application.GetScoreHistoryUseCase
import local.sudoku.engine.domain.model.Difficulty
import local.sudoku.engine.domain.model.RunEndCause
import local.sudoku.engine.domain.model.RunRecord
import local.sudoku.engine.domain.model.SudokuEngine
import local.sudoku.engine.domain.repository.RunHistoryRepository
import local.sudoku.shop.application.CoinShopConfig
import local.sudoku.ui.application.RecordGameFinishedUseCase
import local.sudoku.ui.infrastructure.audio.SoundManager
import local.sudoku.ui.infrastructure.persistence.SudokuStatsRepository
import local.sudoku.ui.presentation.viewmodel.GameState
import local.sudoku.ui.presentation.viewmodel.GameViewModel
import local.sudoku.ui.presentation.viewmodel.ProgressViewModel
import local.sudoku.ui.presentation.viewmodel.SettingsViewModel
import local.sudoku.ui.presentation.viewmodel.ShopViewModel

object PreviewData {
    val mockCoinPacks: List<CoinPackItem> = CoinPacks.mockProducts.map {
        CoinShopConfig.coinPackItem(it)
    }

    private val now = System.currentTimeMillis()
    val fakeRuns = listOf(
        RunRecord(
            mode = "EASY", level = 4, score = 1500, reward = 20, durationMs = 60_000,
            pipesPassed = 1, endCause = RunEndCause.VICTORY, shieldUses = 1,
            startedAt = now - 3_600_000, endedAt = now - 3_600_000
        ),
        RunRecord(
            mode = "MEDIUM", level = 6, score = 3500, reward = 35, durationMs = 180_000,
            pipesPassed = 0, endCause = RunEndCause.VICTORY, multiplierUses = 1,
            startedAt = now - 7_200_000, endedAt = now - 7_200_000
        ),
        RunRecord(
            mode = "HARD", level = 6, score = 4200, reward = 0, durationMs = 200_000,
            pipesPassed = 3, endCause = RunEndCause.GAME_OVER,
            startedAt = now - 10_800_000, endedAt = now - 10_800_000
        )
    )
}

private class PreviewCoinAccount(initial: Int = 500) : CoinAccount {
    private val flow = MutableStateFlow(initial)
    override val coinsFlow: Flow<Int> = flow
    override suspend fun addCoins(amount: Int) {
        flow.value += amount
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        if (flow.value < amount) return false
        flow.value -= amount
        return true
    }
}

private class PreviewRunHistory(private val runs: List<RunRecord>) : RunHistoryRepository {
    private val flow = MutableStateFlow(runs)
    override val highScoreFlow: Flow<Int> = flow.map { list -> list.maxOfOrNull { it.score } ?: 0 }
    override fun recentRunsFlow(limit: Int): Flow<List<RunRecord>> =
        flow.map { it.take(limit) }

    override suspend fun recordRun(record: RunRecord): Long {
        flow.value = listOf(record) + flow.value
        return 0
    }

    override suspend fun updateRun(id: Long, record: RunRecord) = Unit
}

private class PreviewAudioSettings : AudioSettings {
    private val music = MutableStateFlow(true)
    private val sfx = MutableStateFlow(true)
    override val musicEnabledFlow: Flow<Boolean> = music
    override val sfxEnabledFlow: Flow<Boolean> = sfx
    override suspend fun toggleMusic(enabled: Boolean) {
        music.value = enabled
    }

    override suspend fun toggleSfx(enabled: Boolean) {
        sfx.value = enabled
    }
}

private class PreviewBillingRepository : BillingRepository {
    private val statusFlow = MutableStateFlow(BillingStatus.CONNECTED)
    private val productsFlow = MutableStateFlow(CoinPacks.mockProducts)
    override val status: Flow<BillingStatus> = statusFlow
    override val products: Flow<List<DomainProduct>> = productsFlow
    override fun startConnection() = Unit
    override fun launchPurchaseFlow(activity: Activity, productId: String) = Unit
}

@Composable
fun rememberPreviewSoundManager(): SoundManager {
    return remember { SoundManager(null) }
}

@Composable
fun rememberPreviewProgressViewModel(): ProgressViewModel {
    val context = LocalContext.current
    return remember {
        val runs = PreviewRunHistory(PreviewData.fakeRuns)
        ProgressViewModel(
            GetHighScoreUseCase(runs),
            GetScoreHistoryUseCase(runs),
            SudokuStatsRepository(context)
        )
    }
}

@Composable
fun rememberPreviewSettingsViewModel(): SettingsViewModel {
    return remember {
        val audio = PreviewAudioSettings()
        SettingsViewModel(
            GetAudioSettingsUseCase(audio),
            ToggleAudioUseCase(audio)
        )
    }
}

@Composable
fun rememberPreviewGameViewModel(): GameViewModel {
    val context = LocalContext.current
    return remember {
        val coins = PreviewCoinAccount()
        val runs = PreviewRunHistory(PreviewData.fakeRuns)
        GameViewModel(
            coinAccount = coins,
            recordGameFinishedUseCase = RecordGameFinishedUseCase(
                coins, runs, SudokuStatsRepository(context)
            )
        ).apply {
            setPreviewState(
                GameState(
                    board = SudokuEngine.generateBoard(4, Difficulty.EASY),
                    score = 1250,
                    comboMultiplier = 3,
                    mistakes = 1,
                    timeRemaining = 45,
                    streak = 2
                )
            )
        }
    }
}

@Composable
@Suppress("unused")
fun rememberPreviewShopViewModel(): ShopViewModel {
    return remember {
        ShopViewModel(
            PreviewBillingRepository(),
            PreviewCoinAccount()
        )
    }
}
