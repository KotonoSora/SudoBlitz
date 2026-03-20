package com.kotonosora.sudoblitz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotonosora.sudoblitz.ui.components.NeonText
import com.kotonosora.sudoblitz.ui.components.NeonTitle
import com.kotonosora.sudoblitz.ui.theme.DarkBackground
import com.kotonosora.sudoblitz.ui.theme.NeonMagenta
import com.kotonosora.sudoblitz.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val musicEnabled by viewModel.musicEnabled.collectAsState()
    val hapticEnabled by viewModel.hapticEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonMagenta
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonTitle("SETTINGS", NeonMagenta, fontSize = 32)
            Spacer(modifier = Modifier.height(48.dp))

            SettingToggle(
                label = "Sound Effects",
                color = NeonMagenta,
                checked = soundEnabled,
                onCheckedChange = { viewModel.toggleSound(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingToggle(
                label = "Background Music",
                color = NeonMagenta,
                checked = musicEnabled,
                onCheckedChange = { viewModel.toggleMusic(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SettingToggle(
                label = "Haptic Feedback",
                color = NeonMagenta,
                checked = hapticEnabled,
                onCheckedChange = { viewModel.toggleHaptic(it) }
            )
        }
    }
}

@Composable
fun SettingToggle(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonText(label, color, fontSize = 18)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = color,
                checkedTrackColor = color.copy(alpha = 0.3f)
            )
        )
    }
}
