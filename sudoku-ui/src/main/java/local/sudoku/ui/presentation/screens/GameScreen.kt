package local.sudoku.ui.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.sudoku.ui.infrastructure.audio.LocalSoundManager
import local.sudoku.ui.infrastructure.audio.SoundManager
import local.sudoku.ui.presentation.components.NeonText
import local.sudoku.ui.presentation.components.Numpad
import local.sudoku.ui.presentation.components.SudokuGrid
import local.sudoku.ui.presentation.theme.ErrorRed
import local.sudoku.ui.presentation.theme.GameTheme
import local.sudoku.ui.presentation.theme.NeonCyan
import local.sudoku.ui.presentation.theme.NeonGreen
import local.sudoku.ui.presentation.theme.NeonYellow
import local.sudoku.ui.presentation.viewmodel.GameState
import local.sudoku.ui.presentation.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    soundManager: SoundManager = LocalSoundManager.current,
    onNavigateToResult: () -> Unit,
    onShopClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val extraTimeCount by viewModel.extraTimeCount.collectAsState()
    val hintCount by viewModel.hintCount.collectAsState()
    val undoCount by viewModel.undoCount.collectAsState()

    // Ensure we only navigate to result if the game was actually in progress
    // and then reached a game over state.
    LaunchedEffect(gameState.isGameOver, gameState.board) {
        if (gameState.isGameOver && gameState.board != null) {
            if (gameState.isVictory) {
                soundManager.playWin()
            } else {
                soundManager.playLose()
            }
            onNavigateToResult()
        }
    }

    var previousMistakes by remember { mutableStateOf(gameState.mistakes) }
    LaunchedEffect(gameState.mistakes) {
        if (gameState.mistakes > previousMistakes) {
            soundManager.playError()
            previousMistakes = gameState.mistakes
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { NeonText("Lvl ${gameState.streak + 1}", NeonCyan, fontSize = 20) },
                actions = {
                    NeonText(
                        text = "SCORE: ${gameState.score}",
                        color = NeonCyan,
                        fontSize = 18,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }, containerColor = MaterialTheme.colorScheme.background, modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            GameStatsHeader(
                gameState = gameState,
                coins = coins,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            gameState.board?.let { board ->
                SudokuGrid(
                    board = board,
                    selectedCell = gameState.selectedCell,
                    onCellSelected = { r, c ->
                        soundManager.playTap()
                        viewModel.selectCell(r, c)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .aspectRatio(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BoostButton(
                    icon = Icons.Rounded.AccessTime,
                    label = "+30s",
                    owned = extraTimeCount,
                    enabled = extraTimeCount > 0 && !gameState.isGameOver,
                    onClick = {
                        soundManager.playTap()
                        viewModel.addTime()
                    })
                BoostButton(
                    icon = Icons.Rounded.Lightbulb,
                    label = "Hint",
                    owned = hintCount,
                    enabled = hintCount > 0 && !gameState.isGameOver,
                    onClick = {
                        soundManager.playTap()
                        viewModel.useHint()
                    })
                BoostButton(
                    icon = Icons.AutoMirrored.Rounded.Undo,
                    label = "Undo",
                    owned = undoCount,
                    enabled = undoCount > 0 && gameState.mistakes > 0 && !gameState.isGameOver,
                    onClick = {
                        soundManager.playTap()
                        viewModel.undoMistake()
                    })
            }

            if (extraTimeCount + hintCount + undoCount == 0) {
                NeonText(
                    text = "OUT OF BOOSTS — TAP FOR SHOP",
                    color = NeonYellow,
                    fontSize = 12,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clickable { onShopClicked() }
                )
            }

            Numpad(
                size = gameState.currentSize, onNumberSelected = {
                    soundManager.playTap()
                    viewModel.inputNumber(it)
                }, modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@SuppressLint("NonObservableLocale")
@Composable
fun GameStatsHeader(gameState: GameState, coins: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val minutes = gameState.timeRemaining / 60
            val seconds = gameState.timeRemaining % 60
            val locale = LocalConfiguration.current.locales[0] ?: LocalLocale.current.platformLocale
            val timeStr = String.format(locale, "%02d:%02d", minutes, seconds)

            // Timer Section
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                NeonText(
                    text = timeStr,
                    color = if (gameState.timeRemaining < 10) ErrorRed else NeonCyan,
                    fontSize = 24
                )
            }

            // Mistakes Section
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..gameState.maxMistakes) {
                    NeonText(
                        text = "X",
                        color = if (i <= gameState.mistakes) ErrorRed else Color.DarkGray,
                        fontSize = 20,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Combo & Coins Section
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (gameState.comboMultiplier >= 1) {
                NeonText(
                    text = "x${gameState.comboMultiplier} COMBO",
                    color = NeonGreen,
                    fontSize = 12,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
            NeonText(
                text = "$coins COINS",
                color = NeonYellow,
                fontSize = 12,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun BoostButton(
    icon: ImageVector,
    label: String,
    owned: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(68.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (enabled) NeonCyan else Color.DarkGray
        ),
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (enabled) NeonCyan.copy(alpha = 0.1f) else Color.Transparent,
            contentColor = if (enabled) NeonCyan else Color.DarkGray
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            NeonText(
                text = "$label ×$owned",
                color = if (enabled) NeonYellow else Color.DarkGray,
                fontSize = 12
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    GameTheme {
        GameScreen(
            viewModel = rememberPreviewGameViewModel(),
            soundManager = rememberPreviewSoundManager(),
            onNavigateToResult = {})
    }
}
