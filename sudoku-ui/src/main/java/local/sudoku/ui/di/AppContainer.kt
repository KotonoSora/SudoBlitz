package local.sudoku.ui.di

import android.content.Context
import local.core.billing.BillingConfig
import local.core.billing.BillingManagerImpl
import local.core.billing.BillingRepository
import local.core.billing.MockBillingManager
import local.core.shop.coins.CoinPackIds
import local.core.shop.coins.CoinPacks
import local.core.ui.data.AudioSettings
import local.core.ui.data.CoinAccount
import local.core.ui.data.InventoryStore
import local.core.ui.data.RoomRunStore
import local.core.ui.data.RunStore
import local.core.ui.data.SettingsRepositoryImpl
import local.core.ui.data.db.GameDatabase
import local.core.ui.data.db.createGameDatabase
import local.core.ui.usecase.GetAudioSettingsUseCase
import local.core.ui.usecase.ToggleAudioUseCase
import local.sudoku.engine.application.GetHighScoreUseCase
import local.sudoku.engine.application.GetScoreHistoryUseCase
import local.sudoku.engine.application.InputNumberUseCase
import local.sudoku.engine.application.NextLevelUseCase
import local.sudoku.engine.application.StartNewGameUseCase
import local.sudoku.engine.application.UndoMistakeUseCase
import local.sudoku.engine.application.UseHintUseCase
import local.sudoku.engine.application.UsePowerUpUseCase
import local.sudoku.engine.domain.model.PowerUpType
import local.sudoku.engine.domain.repository.RunHistoryRepository
import local.sudoku.shop.domain.model.PowerUpShopItem
import local.sudoku.shop.domain.model.SkinIds
import local.sudoku.shop.domain.service.PowerUps
import local.sudoku.shop.infrastructure.persistence.SudokuInventoryKeys
import local.sudoku.ui.adapters.asCoinSink
import local.sudoku.ui.adapters.asPowerUpStore
import local.sudoku.ui.adapters.asRunHistoryRepository
import local.sudoku.ui.application.RecordGameFinishedUseCase
import local.sudoku.ui.infrastructure.persistence.SudokuStatsRepository

/**
 * Dependency Injection container for the application. Manual DI, no Hilt/Koin.
 */
interface AppContainer {
    val coinAccount: CoinAccount
    val inventoryStore: InventoryStore
    val audioSettings: AudioSettings
    val statsRepository: SudokuStatsRepository
    val runHistoryRepository: RunHistoryRepository
    val billingRepository: BillingRepository

    val startNewGameUseCase: StartNewGameUseCase
    val nextLevelUseCase: NextLevelUseCase
    val inputNumberUseCase: InputNumberUseCase
    val useHintUseCase: UseHintUseCase
    val undoMistakeUseCase: UndoMistakeUseCase
    val usePowerUpUseCase: UsePowerUpUseCase
    val getHighScoreUseCase: GetHighScoreUseCase
    val getScoreHistoryUseCase: GetScoreHistoryUseCase
    val getAudioSettingsUseCase: GetAudioSettingsUseCase
    val toggleAudioUseCase: ToggleAudioUseCase
    val recordGameFinishedUseCase: RecordGameFinishedUseCase
}

/**
 * Default implementation of the [AppContainer] interface.
 */
class DefaultAppContainer(
    private val context: Context,
    private val isDebug: Boolean
) : AppContainer {

    init {
        // String contract: shop catalog power-up IDs MUST equal engine PowerUpType IDs.
        check(
            PowerUpType.entries.map { it.id }.toSet() ==
                PowerUps.getPowerUpItems()
                    .map { (it as PowerUpShopItem).inventoryId }.toSet()
        ) { "Shop catalog power-up IDs diverged from engine PowerUpType IDs" }
    }

    /**
     * Single DataStore-backed store, exposed through narrow ports so each
     * consumer depends only on what it uses. The Sudoku inventory map lives in
     * `:sudoku-shop` and is injected via constructor (core stays game-agnostic).
     */
    private val settingsStore: SettingsRepositoryImpl by lazy {
        SettingsRepositoryImpl(
            context,
            SkinIds.SKIN_DEFAULT_ID,
            SudokuInventoryKeys.map()
        )
    }

    override val coinAccount: CoinAccount by lazy { settingsStore }
    override val inventoryStore: InventoryStore by lazy { settingsStore }
    override val audioSettings: AudioSettings by lazy { settingsStore }

    override val statsRepository: SudokuStatsRepository by lazy {
        SudokuStatsRepository(context)
    }

    private val gameDatabase: GameDatabase by lazy {
        createGameDatabase(context)
    }

    private val runStore: RunStore by lazy {
        RoomRunStore(gameDatabase.runDao())
    }

    override val runHistoryRepository: RunHistoryRepository by lazy {
        runStore.asRunHistoryRepository()
    }

    override val billingRepository: BillingRepository by lazy {
        val wallet = coinAccount.asCoinSink()
        if (isDebug) {
            MockBillingManager(wallet, CoinPacks.mockProducts, CoinPacks::getCoinAmount)
        } else {
            BillingManagerImpl(
                context,
                wallet,
                BillingConfig(CoinPackIds.ALL, CoinPacks::getCoinAmount)
            )
        }
    }

    override val startNewGameUseCase: StartNewGameUseCase by lazy { StartNewGameUseCase() }
    override val nextLevelUseCase: NextLevelUseCase by lazy { NextLevelUseCase() }
    override val inputNumberUseCase: InputNumberUseCase by lazy { InputNumberUseCase() }
    override val useHintUseCase: UseHintUseCase by lazy { UseHintUseCase() }
    override val undoMistakeUseCase: UndoMistakeUseCase by lazy { UndoMistakeUseCase() }

    override val getHighScoreUseCase: GetHighScoreUseCase by lazy {
        GetHighScoreUseCase(runHistoryRepository)
    }
    override val getScoreHistoryUseCase: GetScoreHistoryUseCase by lazy {
        GetScoreHistoryUseCase(runHistoryRepository)
    }
    override val getAudioSettingsUseCase: GetAudioSettingsUseCase by lazy {
        GetAudioSettingsUseCase(audioSettings)
    }
    override val toggleAudioUseCase: ToggleAudioUseCase by lazy {
        ToggleAudioUseCase(audioSettings)
    }
    override val recordGameFinishedUseCase: RecordGameFinishedUseCase by lazy {
        RecordGameFinishedUseCase(coinAccount, runHistoryRepository, statsRepository)
    }
    override val usePowerUpUseCase: UsePowerUpUseCase by lazy {
        UsePowerUpUseCase(inventoryStore.asPowerUpStore())
    }
}
