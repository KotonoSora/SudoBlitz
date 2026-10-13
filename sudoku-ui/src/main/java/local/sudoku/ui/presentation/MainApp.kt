package local.sudoku.ui.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import local.sudoku.ui.di.AppViewModelFactory
import local.sudoku.ui.di.LocalAppContainer
import local.sudoku.ui.infrastructure.audio.LocalSoundManager
import local.sudoku.ui.presentation.navigation.Screen
import local.sudoku.ui.presentation.screens.BoostSelectionScreen
import local.sudoku.ui.presentation.screens.DailyChallengeScreen
import local.sudoku.ui.presentation.screens.GameScreen
import local.sudoku.ui.presentation.screens.HomeScreen
import local.sudoku.ui.presentation.screens.ProgressScreen
import local.sudoku.ui.presentation.screens.ResultScreen
import local.sudoku.ui.presentation.screens.SettingsScreen
import local.sudoku.ui.presentation.screens.ShopScreen
import local.sudoku.ui.presentation.viewmodel.GameViewModel
import local.sudoku.ui.presentation.viewmodel.ProgressViewModel
import local.sudoku.ui.presentation.viewmodel.SettingsViewModel
import local.sudoku.ui.presentation.viewmodel.ShopViewModel

@Composable
fun MainApp(
    factory: AppViewModelFactory,
    navController: NavHostController = rememberNavController()
) {
    val soundManager = LocalSoundManager.current
    // Sanctioned SFX-only wiring: the SFX toggle flows from core AudioSettings;
    // Settings routes toggles through core (see SettingsViewModel).
    val sfxEnabled by LocalAppContainer.current.audioSettings.sfxEnabledFlow
        .collectAsState(initial = true)
    soundManager.soundEnabled = sfxEnabled

    val gameViewModel: GameViewModel = viewModel(factory = factory)
    val shopViewModel: ShopViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
    val progressViewModel: ProgressViewModel = viewModel(factory = factory)

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
                },
                onShopClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Shop.route)
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
