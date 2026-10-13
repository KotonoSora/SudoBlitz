package local.sudoku.ui.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.sudoku.ui.presentation.components.NeonButton
import local.sudoku.ui.presentation.components.NeonText
import local.sudoku.ui.presentation.components.NeonTitle
import local.sudoku.ui.presentation.theme.DarkBackground
import local.sudoku.ui.presentation.theme.ErrorRed
import local.sudoku.ui.presentation.theme.GameTheme
import local.sudoku.ui.presentation.theme.NeonBlue
import local.sudoku.ui.presentation.theme.NeonCyan
import local.sudoku.ui.presentation.theme.NeonGreen
import local.sudoku.ui.presentation.theme.NeonMagenta
import local.sudoku.ui.presentation.theme.NeonYellow
import local.sudoku.ui.presentation.theme.SuccessGreen
import local.sudoku.ui.presentation.theme.SurfaceDark
import local.sudoku.ui.presentation.viewmodel.GameState

@Composable
fun ResultScreen(
    gameState: GameState,
    onNextLevel: () -> Unit,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = DarkBackground
    // Lock these values to prevent UI flicker when ViewModel resets state for navigation
    val isVictory = remember { gameState.isVictory }
    val finalScore = remember { gameState.score }
    val finalStreak = remember { gameState.streak }
    val coinsEarned = remember { gameState.coinsEarned }
    val coinDetails = remember { gameState.coinDetails }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeonTitle(
            text = if (isVictory) "VICTORY!" else "GAME OVER",
            color = if (isVictory) SuccessGreen else ErrorRed,
            fontSize = 36,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats summary
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                NeonText("Final Score", NeonBlue, fontSize = 20)
                Spacer(modifier = Modifier.height(8.dp))
                NeonText(
                    text = finalScore.toString(), color = NeonYellow, fontSize = 32
                )

                Spacer(modifier = Modifier.height(24.dp))

                NeonText("Streak", NeonMagenta, fontSize = 16)
                Spacer(modifier = Modifier.height(8.dp))
                NeonText(
                    text = finalStreak.toString(), color = NeonCyan, fontSize = 24
                )

                if (isVictory && coinsEarned > 0) {
                    Spacer(modifier = Modifier.height(24.dp))
                    NeonText("Coins Earned", NeonGreen, fontSize = 16)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeonText(
                        text = "+$coinsEarned", color = NeonYellow, fontSize = 24
                    )
                    if (coinDetails.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        NeonText(
                            text = coinDetails, color = NeonCyan.copy(alpha = 0.7f), fontSize = 12,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        if (isVictory) {
            NeonButton(
                text = "NEXT LEVEL",
                color = NeonGreen,
                icon = Icons.Rounded.Refresh,
                onClick = onNextLevel
            )
        } else {
            NeonButton(
                text = "RETRY",
                color = NeonCyan,
                icon = Icons.Rounded.Refresh,
                onClick = onPlayAgain
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeonButton(
            text = "HOME", color = NeonMagenta, icon = Icons.Rounded.Home, onClick = onHome
        )
    }
}

@Preview(name = "Victory", showBackground = true)
@Composable
fun ResultScreenVictoryPreview() {
    GameTheme {
        ResultScreen(
            gameState = GameState(
                score = 2450,
                streak = 3,
                isVictory = true,
                isGameOver = true,
                coinsEarned = 45,
                coinDetails = "Base: 10, Time: +31, Size: +4"
            ), onNextLevel = {}, onPlayAgain = {}, onHome = {})
    }
}

@Preview(name = "Defeat", showBackground = true)
@Composable
fun ResultScreenDefeatPreview() {
    GameTheme {
        ResultScreen(
            gameState = GameState(
                score = 800, streak = 0, isVictory = false, isGameOver = true
            ), onNextLevel = {}, onPlayAgain = {}, onHome = {})
    }
}
