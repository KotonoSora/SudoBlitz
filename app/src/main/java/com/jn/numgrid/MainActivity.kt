package com.jn.numgrid

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jn.numgrid.audio.HapticManager
import com.jn.numgrid.audio.SoundManager
import com.jn.numgrid.data.AppDatabase
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.data.dataStore
import com.jn.numgrid.ui.navigation.Screen
import com.jn.numgrid.ui.screens.BoostSelectionScreen
import com.jn.numgrid.ui.screens.DailyChallengeScreen
import com.jn.numgrid.ui.screens.GameScreen
import com.jn.numgrid.ui.screens.HomeScreen
import com.jn.numgrid.ui.screens.ProgressScreen
import com.jn.numgrid.ui.screens.ResultScreen
import com.jn.numgrid.ui.screens.SettingsScreen
import com.jn.numgrid.ui.screens.ShopScreen
import com.jn.numgrid.ui.theme.SudoBlitzTheme
import com.jn.numgrid.viewmodel.GameViewModel
import com.jn.numgrid.viewmodel.ProgressViewModel
import com.jn.numgrid.viewmodel.SettingsViewModel
import com.jn.numgrid.viewmodel.ShopViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = UserPreferencesRepository(applicationContext.dataStore)
        val database = AppDatabase.getDatabase(applicationContext)
        val gameRecordDao = database.gameRecordDao()

        setContent {
            val soundManager = remember {
                SoundManager(
                    applicationContext, repository
                )
            }
            val hapticManager = remember {
                HapticManager(
                    applicationContext, repository
                )
            }

            DisposableEffect(Unit) {
                onDispose { soundManager.release() }
            }

            SudoBlitzTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    NumGridApp(
                        repository = repository,
                        gameRecordDao = gameRecordDao,
                        application = application as Application,
                        soundManager = soundManager,
                        hapticManager = hapticManager
                    )
                }
            }
        }
    }

    @Composable
    private fun NumGridApp(
        repository: UserPreferencesRepository,
        gameRecordDao: GameRecordDao,
        application: Application,
        soundManager: SoundManager,
        hapticManager: HapticManager
    ) {
        val navController = rememberNavController()
        val gameViewModel: GameViewModel = viewModel(
            factory = GameViewModel.provideFactory(
                repository, gameRecordDao
            )
        )
        val shopViewModel: ShopViewModel = viewModel(
            factory = ShopViewModel.provideFactory(
                application, repository
            )
        )
        val settingsViewModel: SettingsViewModel = viewModel(
            factory = SettingsViewModel.provideFactory(
                repository
            )
        )
        val progressViewModel: ProgressViewModel = viewModel(
            factory = ProgressViewModel.provideFactory(
                repository, gameRecordDao
            )
        )

        val coins by gameViewModel.coins.collectAsState()
        val gameState by gameViewModel.gameState.collectAsState()

        NavHost(navController = navController, startDestination = Screen.Home.route) {
            composable(Screen.Home.route) {
                HomeScreen(coins = coins, onPlayClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.BoostSelection.route)
                }, onDailyChallengeClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.DailyChallenge.route)
                }, onLeaderboardClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Progress.route)
                }, onSettingsClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Settings.route)
                }, onShopClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Shop.route)
                })
            }

            composable(Screen.BoostSelection.route) {
                BoostSelectionScreen(soundManager = soundManager, onBack = {
                    soundManager.playTap()
                    navController.popBackStack()
                }, onStartGame = { size, difficulty ->
                    soundManager.playTap()
                    gameViewModel.startNewGame(size, difficulty)
                    navController.navigate(Screen.Game.route)
                })
            }

            composable(Screen.Progress.route) {
                ProgressScreen(
                    viewModel = progressViewModel, onBack = {
                        soundManager.playTap()
                        navController.popBackStack()
                    })
            }

            composable(Screen.DailyChallenge.route) {
                DailyChallengeScreen(soundManager = soundManager, onBack = {
                    soundManager.playTap()
                    navController.popBackStack()
                }, onStartChallenge = { size, difficulty ->
                    gameViewModel.startNewGame(size, difficulty)
                    navController.navigate(Screen.Game.route)
                })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel, onBack = {
                        soundManager.playTap()
                        navController.popBackStack()
                    })
            }

            composable(Screen.Game.route) {
                GameScreen(
                    viewModel = gameViewModel,
                    soundManager = soundManager,
                    hapticManager = hapticManager,
                    onNavigateToResult = {
                        navController.navigate(Screen.Result.route) {
                            popUpTo(Screen.Game.route) { inclusive = true }
                        }
                    })
            }

            composable(Screen.Result.route) {
                ResultScreen(gameState = gameState, onPlayAgain = {
                    soundManager.playTap()
                    if (gameState.isVictory) {
                        gameViewModel.nextLevel()
                    } else {
                        gameViewModel.startNewGame(
                            gameState.currentSize, gameState.currentDifficulty
                        )
                    }
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                }, onHome = {
                    soundManager.playTap()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Shop.route) {
                ShopScreen(
                    viewModel = shopViewModel, onBack = {
                        soundManager.playTap()
                        navController.popBackStack()
                    })
            }
        }
    }
}
