package local.sudoku.ui.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Event
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.sudoku.ui.R
import local.sudoku.ui.presentation.components.CoinCapsule
import local.sudoku.ui.presentation.components.NeonButton
import local.sudoku.ui.presentation.components.NeonTitle
import local.sudoku.ui.presentation.theme.DarkBackground
import local.sudoku.ui.presentation.theme.GameTheme
import local.sudoku.ui.presentation.theme.NeonBlue
import local.sudoku.ui.presentation.theme.NeonCyan
import local.sudoku.ui.presentation.theme.NeonGreen
import local.sudoku.ui.presentation.theme.NeonMagenta
import local.sudoku.ui.presentation.theme.NeonYellow

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
    BackHandler(enabled = true) {}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        CoinCapsule(coins = coins, onClick = onShopClicked)
                        IconButton(onClick = onShopClicked) {
                            Icon(
                                Icons.Rounded.ShoppingCart,
                                contentDescription = "Shop",
                                tint = NeonCyan
                            )
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }, containerColor = DarkBackground
    ) { innerPadding ->
        HomeScreenContent(
            onPlayClicked = onPlayClicked,
            onDailyChallengeClicked = onDailyChallengeClicked,
            onLeaderboardClicked = onLeaderboardClicked,
            onSettingsClicked = onSettingsClicked,
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun HomeScreenContent(
    onPlayClicked: () -> Unit,
    onDailyChallengeClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeonTitle(R.string.app_name, NeonCyan, fontSize = 40)

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonButton(
                "PLAY GAME",
                NeonGreen,
                icon = Icons.Rounded.VideogameAsset,
                onClick = onPlayClicked,
                textSize = 14.sp,
                height = 56
            )
            NeonButton(
                "DAILY CHALLENGE",
                NeonYellow,
                icon = Icons.Rounded.Event,
                onClick = onDailyChallengeClicked,
                textSize = 14.sp,
                height = 56
            )
            NeonButton(
                "LEADERBOARD",
                NeonBlue,
                icon = Icons.Rounded.EmojiEvents,
                onClick = onLeaderboardClicked,
                textSize = 14.sp,
                height = 56
            )
            NeonButton(
                "SETTINGS",
                NeonMagenta,
                icon = Icons.Rounded.Settings,
                onClick = onSettingsClicked,
                textSize = 14.sp,
                height = 56
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GameTheme {
        HomeScreen(
            coins = 120,
            onPlayClicked = {},
            onDailyChallengeClicked = {},
            onLeaderboardClicked = {},
            onSettingsClicked = {},
            onShopClicked = {})
    }
}
