package com.jn.numgrid.ui.screens

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesOf
import com.jn.numgrid.application.settings.ObserveSettingsUseCase
import com.jn.numgrid.application.settings.ToggleMusicUseCase
import com.jn.numgrid.application.settings.ToggleSoundUseCase
import com.jn.numgrid.application.shop.ObserveShopProductsUseCase
import com.jn.numgrid.application.shop.ObserveShopStatusUseCase
import com.jn.numgrid.application.shop.PurchaseProductUseCase
import com.jn.numgrid.application.shop.ReleaseShopUseCase
import com.jn.numgrid.application.shop.SetMockProductsUseCase
import com.jn.numgrid.application.shop.ShopStoreGateway
import com.jn.numgrid.audio.SoundManager
import com.jn.numgrid.billing.BillingManager
import com.jn.numgrid.data.CoinWalletGatewayAdapter
import com.jn.numgrid.data.GameRecord
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.SettingsPreferencesGatewayAdapter
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.domain.game.Difficulty
import com.jn.numgrid.domain.game.SudokuEngine
import com.jn.numgrid.domain.shop.CoinWalletGateway
import com.jn.numgrid.domain.shop.ShopProduct
import com.jn.numgrid.domain.shop.ShopStatus
import com.jn.numgrid.viewmodel.GameState
import com.jn.numgrid.viewmodel.GameViewModel
import com.jn.numgrid.viewmodel.ProgressViewModel
import com.jn.numgrid.viewmodel.SettingsViewModel
import com.jn.numgrid.viewmodel.ShopViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

object PreviewData {
    val previewDataStore = object : DataStore<Preferences> {
        override val data: Flow<Preferences> =
            flowOf(preferencesOf(UserPreferencesRepository.COINS_KEY to 500))

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

    val mockProducts = BillingManager.getDebugProducts().map {
        ShopProduct(it.productId, it.title, it.price, it.coinAmount)
    }
}

@Composable
fun rememberPreviewSoundManager(): SoundManager {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    return remember(context) {
        SoundManager(
            context,
            PreviewData.previewRepository,
            isPreview = isPreview
        )
    }
}

@Composable
fun rememberPreviewProgressViewModel(): ProgressViewModel {
    return remember {
        ProgressViewModel(
            PreviewData.previewRepository,
            PreviewData.previewGameRecordDao
        )
    }
}

@Composable
fun rememberPreviewSettingsViewModel(): SettingsViewModel {
    return remember {
        val gateway = SettingsPreferencesGatewayAdapter(PreviewData.previewRepository)
        SettingsViewModel(
            observeSettings = ObserveSettingsUseCase(gateway),
            toggleSoundUseCase = ToggleSoundUseCase(gateway),
            toggleMusicUseCase = ToggleMusicUseCase(gateway)
        )
    }
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
@Suppress("unused")
fun rememberPreviewShopViewModel(): ShopViewModel {
    val isPreview = LocalInspectionMode.current
    return remember(isPreview) {
        val shopGateway = PreviewShopStoreGateway()
        val walletGateway: CoinWalletGateway =
            CoinWalletGatewayAdapter(PreviewData.previewRepository)
        ShopViewModel(
            observeShopProducts = ObserveShopProductsUseCase(shopGateway),
            observeShopStatus = ObserveShopStatusUseCase(shopGateway),
            purchaseProduct = PurchaseProductUseCase(shopGateway),
            setMockProductsUseCase = SetMockProductsUseCase(shopGateway),
            releaseShopUseCase = ReleaseShopUseCase(shopGateway),
            walletGateway = walletGateway
        ).apply {
            if (isPreview) setMockProducts(PreviewData.mockProducts)
        }
    }
}

private class PreviewShopStoreGateway : ShopStoreGateway {
    private val productsFlow = MutableStateFlow<List<ShopProduct>>(emptyList())
    private val statusFlow = MutableStateFlow(ShopStatus.CONNECTED)

    override val products = productsFlow
    override val status = statusFlow

    override fun setMockProducts(products: List<ShopProduct>) {
        productsFlow.value = products
        statusFlow.value = if (products.isEmpty()) ShopStatus.EMPTY else ShopStatus.CONNECTED
    }

    override fun purchase(activity: Activity, product: ShopProduct) {
        // No-op for preview.
    }

    override fun release() {
        // No-op for preview.
    }
}
