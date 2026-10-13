package local.sudoku.ui.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import local.sudoku.ui.infrastructure.audio.SoundManager
import local.sudoku.engine.domain.model.Difficulty
import local.sudoku.ui.presentation.components.NeonButton
import local.sudoku.ui.presentation.components.NeonText
import local.sudoku.ui.presentation.components.NeonTitle
import local.sudoku.ui.presentation.theme.DarkBackground
import local.sudoku.ui.presentation.theme.GameTheme
import local.sudoku.ui.presentation.theme.NeonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    soundManager: SoundManager,
    onBack: () -> Unit,
    onStartChallenge: (Int, Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, navigationIcon = {
                    IconButton(onClick = {
                        soundManager.playTap()
                        onBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonYellow
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }, containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NeonTitle("DAILY CHALLENGE", NeonYellow, fontSize = 32)
            Spacer(modifier = Modifier.height(24.dp))
            NeonText(
                "Beat the clock with today's special 6x6 grid!", Color.White, fontSize = 16
            )
            Spacer(modifier = Modifier.height(48.dp))
            NeonButton("START CHALLENGE", NeonYellow, onClick = {
                soundManager.playTap()
                // Daily Challenge is always a 6x6 grid on Medium difficulty
                onStartChallenge(6, Difficulty.MEDIUM)
            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DailyChallengeScreenPreview() {
    GameTheme {
        DailyChallengeScreen(
            soundManager = rememberPreviewSoundManager(),
            onBack = {},
            onStartChallenge = { _, _ -> }
        )
    }
}
