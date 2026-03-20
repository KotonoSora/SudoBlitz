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
import com.kotonosora.sudoblitz.ui.components.NeonButton
import com.kotonosora.sudoblitz.ui.components.NeonText
import com.kotonosora.sudoblitz.ui.components.NeonTitle
import com.kotonosora.sudoblitz.ui.theme.DarkBackground
import com.kotonosora.sudoblitz.ui.theme.NeonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonYellow
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
            NeonTitle("DAILY CHALLENGE", NeonYellow, fontSize = 32)
            Spacer(modifier = Modifier.height(24.dp))
            NeonText(
                "Beat the clock with today's special 6x6 grid!",
                androidx.compose.ui.graphics.Color.White,
                fontSize = 16
            )
            Spacer(modifier = Modifier.height(48.dp))
            NeonButton("START CHALLENGE", NeonYellow, onClick = { /* TODO */ })
        }
    }
}
