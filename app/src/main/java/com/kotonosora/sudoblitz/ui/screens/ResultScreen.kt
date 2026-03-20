package com.kotonosora.sudoblitz.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotonosora.sudoblitz.ui.components.NeonButton
import com.kotonosora.sudoblitz.ui.components.NeonText
import com.kotonosora.sudoblitz.ui.components.NeonTitle
import com.kotonosora.sudoblitz.ui.theme.DarkBackground
import com.kotonosora.sudoblitz.ui.theme.ErrorRed
import com.kotonosora.sudoblitz.ui.theme.NeonBlue
import com.kotonosora.sudoblitz.ui.theme.NeonCyan
import com.kotonosora.sudoblitz.ui.theme.NeonGreen
import com.kotonosora.sudoblitz.ui.theme.NeonMagenta
import com.kotonosora.sudoblitz.ui.theme.NeonYellow
import com.kotonosora.sudoblitz.ui.theme.SuccessGreen
import com.kotonosora.sudoblitz.ui.theme.SurfaceDark
import com.kotonosora.sudoblitz.viewmodel.GameState

@Composable
fun ResultScreen(
    gameState: GameState,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = DarkBackground

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
            text = if (gameState.isVictory) "VICTORY!" else "GAME OVER",
            color = if (gameState.isVictory) SuccessGreen else ErrorRed,
            fontSize = 40
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats summary
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NeonText("Final Score", NeonBlue, fontSize = 20)
                Spacer(modifier = Modifier.height(8.dp))
                NeonText(
                    text = gameState.score.toString(),
                    color = NeonYellow,
                    fontSize = 32
                )

                Spacer(modifier = Modifier.height(24.dp))

                NeonText("Streak", NeonMagenta, fontSize = 16)
                Spacer(modifier = Modifier.height(8.dp))
                NeonText(
                    text = gameState.streak.toString(),
                    color = NeonCyan,
                    fontSize = 24
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        NeonButton(
            text = if (gameState.isVictory) "NEXT LEVEL" else "RETRY",
            color = if (gameState.isVictory) NeonGreen else NeonCyan,
            icon = Icons.Rounded.Refresh,
            onClick = onPlayAgain
        )

        Spacer(modifier = Modifier.height(16.dp))

        NeonButton(
            text = "HOME",
            color = NeonMagenta,
            icon = Icons.Rounded.Home,
            onClick = onHome
        )
    }
}
