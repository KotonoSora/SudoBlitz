package com.kotonosora.sudoblitz.ui.screens

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
import androidx.compose.ui.unit.dp
import com.kotonosora.sudoblitz.LocalSoundManager
import com.kotonosora.sudoblitz.model.Difficulty
import com.kotonosora.sudoblitz.ui.components.NeonButton
import com.kotonosora.sudoblitz.ui.components.NeonTitle
import com.kotonosora.sudoblitz.ui.theme.DarkBackground
import com.kotonosora.sudoblitz.ui.theme.NeonCyan
import com.kotonosora.sudoblitz.ui.theme.NeonGreen
import com.kotonosora.sudoblitz.ui.theme.NeonMagenta
import com.kotonosora.sudoblitz.ui.theme.NeonRed
import com.kotonosora.sudoblitz.ui.theme.NeonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoostSelectionScreen(
    onBack: () -> Unit,
    onStartGame: (Int, Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    val soundManager = LocalSoundManager.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        soundManager.playTap()
                        onBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NeonTitle("CHOOSE LEVEL", NeonCyan, fontSize = 40)
            Spacer(modifier = Modifier.height(32.dp))

            NeonButton(
                text = "EASY (4x4)",
                color = NeonGreen,
                onClick = { 
                    soundManager.playTap()
                    onStartGame(4, Difficulty.EASY) 
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            NeonButton(
                text = "MEDIUM (6x6)",
                color = NeonYellow,
                onClick = { 
                    soundManager.playTap()
                    onStartGame(6, Difficulty.MEDIUM) 
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            NeonButton(
                text = "HARD (6x6)",
                color = NeonMagenta,
                onClick = { 
                    soundManager.playTap()
                    onStartGame(6, Difficulty.HARD) 
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            NeonButton(
                text = "EXPERT (9x9)",
                color = NeonRed,
                onClick = { 
                    soundManager.playTap()
                    onStartGame(9, Difficulty.VERY_HARD) 
                }
            )
        }
    }
}
