package com.kotonosora.sudoblitz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotonosora.sudoblitz.audio.SoundManager
import com.kotonosora.sudoblitz.data.UserPreferencesRepository
import com.kotonosora.sudoblitz.data.dataStore
import com.kotonosora.sudoblitz.ui.navigation.Screen
import com.kotonosora.sudoblitz.ui.screens.BoostSelectionScreen
import com.kotonosora.sudoblitz.ui.screens.DailyChallengeScreen
import com.kotonosora.sudoblitz.ui.screens.GameScreen
import com.kotonosora.sudoblitz.ui.screens.HomeScreen
import com.kotonosora.sudoblitz.ui.screens.ProgressScreen
import com.kotonosora.sudoblitz.ui.screens.ResultScreen
import com.kotonosora.sudoblitz.ui.screens.SettingsScreen
import com.kotonosora.sudoblitz.ui.screens.ShopScreen
import com.kotonosora.sudoblitz.ui.theme.SudoBlitzTheme
import com.kotonosora.sudoblitz.viewmodel.GameViewModel
import com.kotonosora.sudoblitz.viewmodel.ShopViewModel

val LocalSoundManager = staticCompositionLocalOf<SoundManager> {
    error("No SoundManager provided")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = UserPreferencesRepository(applicationContext.dataStore)

        setContent {
            val soundManager = remember { SoundManager(applicationContext) }
            DisposableEffect(Unit) {
                onDispose { soundManager.release() }
            }

            CompositionLocalProvider(LocalSoundManager provides soundManager) {
                SudoBlitzTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SudoBlitzApp(repository, application as android.app.Application)
                    }
                }
            }
        }
    }
}

@Composable
fun SudoBlitzApp(repository: UserPreferencesRepository, application: android.app.Application) {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel(
        factory = GameViewModel.provideFactory(repository)
    )
    val shopViewModel: ShopViewModel = viewModel(
        factory = ShopViewModel.provideFactory(application, repository)
    )

    val soundManager = LocalSoundManager.current

    val coins by gameViewModel.coins.collectAsState()
    val gameState by gameViewModel.gameState.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                coins = coins,
                onPlayClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.BoostSelection.route)
                },
                onDailyChallengeClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.DailyChallenge.route)
                },
                onLeaderboardClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Progress.route)
                },
                onSettingsClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Settings.route)
                },
                onShopClicked = {
                    soundManager.playTap()
                    navController.navigate(Screen.Shop.route)
                }
            )
        }

        composable(Screen.BoostSelection.route) {
            BoostSelectionScreen(
                onBack = {
                    soundManager.playTap()
                    navController.popBackStack()
                },
                onStartGame = { size, difficulty ->
                    soundManager.playTap()
                    gameViewModel.startNewGame(size, difficulty)
                    navController.navigate(Screen.Game.route)
                }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(onBack = {
                soundManager.playTap()
                navController.popBackStack()
            })
        }

        composable(Screen.DailyChallenge.route) {
            DailyChallengeScreen(onBack = {
                soundManager.playTap()
                navController.popBackStack()
            })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = {
                soundManager.playTap()
                navController.popBackStack()
            })
        }

        composable(Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                gameState = gameState,
                onPlayAgain = {
                    soundManager.playTap()
                    if (gameState.isVictory) {
                        gameViewModel.nextLevel()
                    } else {
                        gameViewModel.startNewGame(
                            gameState.currentSize,
                            gameState.currentDifficulty
                        )
                    }
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                },
                onHome = {
                    soundManager.playTap()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Shop.route) {
            ShopScreen(
                viewModel = shopViewModel,
                onBack = {
                    soundManager.playTap()
                    navController.popBackStack()
                }
            )
        }
    }
}
