package com.kotonosora.sudoblitz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.VideogameAsset
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
import androidx.compose.ui.unit.dp
import com.kotonosora.sudoblitz.ui.components.NeonButton
import com.kotonosora.sudoblitz.ui.components.NeonText
import com.kotonosora.sudoblitz.ui.components.NeonTitle
import com.kotonosora.sudoblitz.ui.theme.CoinGold
import com.kotonosora.sudoblitz.ui.theme.DarkBackground
import com.kotonosora.sudoblitz.ui.theme.NeonBlue
import com.kotonosora.sudoblitz.ui.theme.NeonCyan
import com.kotonosora.sudoblitz.ui.theme.NeonGreen
import com.kotonosora.sudoblitz.ui.theme.NeonMagenta
import com.kotonosora.sudoblitz.ui.theme.NeonYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    coins: Int,
    onPlayClicked: () -> Unit,
    onDailyChallengeClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onShopClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            Icons.Rounded.MonetizationOn,
                            contentDescription = "Coins",
                            tint = CoinGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        NeonText(coins.toString(), CoinGold, fontSize = 18)
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = onShopClicked) {
                            Icon(
                                Icons.Rounded.ShoppingCart,
                                contentDescription = "Shop",
                                tint = NeonCyan
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            NeonTitle("SUDO", NeonCyan)
            NeonTitle("BLITZ", NeonMagenta)
            Spacer(modifier = Modifier.height(48.dp))

            NeonButton(
                "PLAY GAME",
                NeonGreen,
                icon = Icons.Rounded.VideogameAsset,
                onClick = onPlayClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonButton(
                "DAILY CHALLENGE",
                NeonYellow,
                icon = Icons.Rounded.Event,
                onClick = onDailyChallengeClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonButton(
                "LEADERBOARD",
                NeonBlue,
                icon = Icons.Rounded.EmojiEvents,
                onClick = onLeaderboardClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
            NeonButton(
                "SETTINGS",
                NeonMagenta,
                icon = Icons.Rounded.Settings,
                onClick = onSettingsClicked
            )
        }
    }
}
