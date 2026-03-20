package com.kotonosora.sudoblitz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.kotonosora.sudoblitz.LocalSoundManager
import com.kotonosora.sudoblitz.ui.components.NeonText
import com.kotonosora.sudoblitz.ui.components.Numpad
import com.kotonosora.sudoblitz.ui.components.SudokuGrid
import com.kotonosora.sudoblitz.ui.theme.ErrorRed
import com.kotonosora.sudoblitz.ui.theme.NeonCyan
import com.kotonosora.sudoblitz.ui.theme.NeonYellow
import com.kotonosora.sudoblitz.viewmodel.GameState
import com.kotonosora.sudoblitz.viewmodel.GameViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gameState by viewModel.gameState.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val soundManager = LocalSoundManager.current

    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
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
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
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
                    cost = 20,
                    enabled = coins >= 20 && !gameState.isGameOver,
                    onClick = {
                        soundManager.playTap()
                        viewModel.addTime()
                    }
                )
                BoostButton(
                    icon = Icons.Rounded.Lightbulb,
                    label = "Hint",
                    cost = 30,
                    enabled = coins >= 30 && !gameState.isGameOver,
                    onClick = {
                        soundManager.playTap()
                        viewModel.useHint()
                    }
                )
                BoostButton(
                    icon = Icons.AutoMirrored.Rounded.Undo,
                    label = "Undo",
                    cost = 15,
                    enabled = coins >= 15 && gameState.mistakes > 0 && !gameState.isGameOver,
                    onClick = {
                        soundManager.playTap()
                        viewModel.undoMistake()
                    }
                )
            }

            Numpad(
                size = gameState.currentSize,
                onNumberSelected = {
                    soundManager.playTap()
                    viewModel.inputNumber(it)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun GameStatsHeader(gameState: GameState, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val minutes = gameState.timeRemaining / 60
        val seconds = gameState.timeRemaining % 60
        val timeStr = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        NeonText(
            text = timeStr,
            color = if (gameState.timeRemaining < 10) ErrorRed else NeonCyan,
            fontSize = 32
        )

        if (gameState.comboMultiplier > 1) {
            NeonText(
                text = "x${gameState.comboMultiplier} COMBO",
                color = NeonYellow,
                fontSize = 18
            )
        }

        Row {
            for (i in 1..gameState.maxMistakes) {
                NeonText(
                    text = "X",
                    color = if (i <= gameState.mistakes) ErrorRed else androidx.compose.ui.graphics.Color.DarkGray,
                    fontSize = 24,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BoostButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    cost: Int,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(64.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = NeonCyan
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = label)
            Spacer(modifier = Modifier.height(4.dp))
            NeonText(text = "$cost Coins", color = NeonYellow, fontSize = 10)
        }
    }
}
