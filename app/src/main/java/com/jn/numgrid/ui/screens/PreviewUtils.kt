package com.jn.numgrid.ui.screens

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import com.jn.numgrid.audio.SoundManager
import com.jn.numgrid.billing.BillingManager
import com.jn.numgrid.billing.StoreProduct
import com.jn.numgrid.data.GameRecord
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.viewmodel.GameViewModel
import com.jn.numgrid.viewmodel.ProgressViewModel
import com.jn.numgrid.viewmodel.SettingsViewModel
import com.jn.numgrid.viewmodel.ShopViewModel
import com.jn.numgrid.model.Difficulty
import com.jn.numgrid.viewmodel.GameState
import com.jn.numgrid.engine.SudokuEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object PreviewData {
    val previewDataStore = object : DataStore<Preferences> {
        override val data: Flow<Preferences> = flowOf(preferencesOf(UserPreferencesRepository.COINS_KEY to 500))
        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            return emptyPreferences()
        }
    }

    val previewRepository = UserPreferencesRepository(previewDataStore)

    val fakeGameRecords = listOf(
        GameRecord(1, 1500, System.currentTimeMillis() - 3600000, "EASY", 4, true),
        GameRecord(2, 3500, System.currentTimeMillis() - 7200000, "MEDIUM", 6, true),
        GameRecord(3, 4200, System.currentTimeMillis() - 10800000, "HARD", 6, false)
    )

    val previewGameRecordDao = object : GameRecordDao {
        override fun getRecentRecords(): Flow<List<GameRecord>> = flowOf(fakeGameRecords)
        override suspend fun insertRecord(record: GameRecord) {}
        override fun getHighScore(): Flow<Int?> = flowOf(4200)
    }

    val mockProducts = BillingManager.productIds.map { id ->
        StoreProduct(id, "${BillingManager.getCoinAmount(id)} Coins", "$0.99", BillingManager.getCoinAmount(id))
    }
}

@Composable
fun rememberPreviewSoundManager(): SoundManager {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    return remember(context) { SoundManager(context, PreviewData.previewRepository, isPreview = isPreview) }
}

@Composable
fun rememberPreviewProgressViewModel(): ProgressViewModel {
    return remember { ProgressViewModel(PreviewData.previewRepository, PreviewData.previewGameRecordDao) }
}

@Composable
fun rememberPreviewSettingsViewModel(): SettingsViewModel {
    return remember { SettingsViewModel(PreviewData.previewRepository) }
}

@Composable
fun rememberPreviewGameViewModel(): GameViewModel {
    return remember {
        GameViewModel(PreviewData.previewRepository, PreviewData.previewGameRecordDao).apply {
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
fun rememberPreviewShopViewModel(): ShopViewModel {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val app = context.applicationContext as? Application ?: Application()
    return remember(app, isPreview) {
        ShopViewModel(app, PreviewData.previewRepository, isPreview = isPreview).apply {
            if (isPreview) {
                setMockProducts(PreviewData.mockProducts)
            }
        }
    }
}
