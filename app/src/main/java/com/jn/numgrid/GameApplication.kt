package com.jn.numgrid

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jn.numgrid.audio.SoundManager
import com.jn.numgrid.data.GameRecordDao
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.ui.navigation.Screen
import com.jn.numgrid.ui.screens.BoostSelectionScreen
import com.jn.numgrid.ui.screens.DailyChallengeScreen
import com.jn.numgrid.ui.screens.GameScreen
import com.jn.numgrid.ui.screens.HomeScreen
import com.jn.numgrid.ui.screens.ProgressScreen
import com.jn.numgrid.ui.screens.ResultScreen
import com.jn.numgrid.ui.screens.SettingsScreen
import com.jn.numgrid.ui.screens.ShopScreen
import com.jn.numgrid.viewmodel.GameViewModel
import com.jn.numgrid.viewmodel.ProgressViewModel
import com.jn.numgrid.viewmodel.SettingsViewModel
import com.jn.numgrid.viewmodel.ShopViewModel

@Composable
fun GameApplication(
    repository: UserPreferencesRepository,
    gameRecordDao: GameRecordDao,
    application: Application,
    soundManager: SoundManager
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

    val navigateHomeAsRoot: () -> Unit = {
        navController.navigate(Screen.Home.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    val navigateBackSafely: () -> Unit = {
        if (!navController.popBackStack(Screen.Home.route, inclusive = false)) {
            navigateHomeAsRoot()
        }
    }

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
                navigateBackSafely()
            }, onStartGame = { size, difficulty ->
                soundManager.playTap()
                gameViewModel.startNewGame(size, difficulty)
                navController.navigate(Screen.Game.route)
            })
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                viewModel = progressViewModel, onBack = {
                    navigateBackSafely()
                })
        }

        composable(Screen.DailyChallenge.route) {
            DailyChallengeScreen(soundManager = soundManager, onBack = {
                navigateBackSafely()
            }, onStartChallenge = { size, difficulty ->
                gameViewModel.startNewGame(size, difficulty)
                navController.navigate(Screen.Game.route)
            })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel, onBack = {
                    navigateBackSafely()
                })
        }

        composable(Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                soundManager = soundManager,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                })
        }

        composable(Screen.Result.route) {
            ResultScreen(
                gameState = gameState,
                onNextLevel = {
                    soundManager.playTap()
                    gameViewModel.nextLevel()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onPlayAgain = {
                    soundManager.playTap()
                    gameViewModel.startNewGame(
                        gameState.currentSize, gameState.currentDifficulty
                    )
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHome = {
                    soundManager.playTap()
                    navigateHomeAsRoot()
                }
            )
        }

        composable(Screen.Shop.route) {
            ShopScreen(
                viewModel = shopViewModel, onBack = {
                    navigateBackSafely()
                })
        }
    }
}