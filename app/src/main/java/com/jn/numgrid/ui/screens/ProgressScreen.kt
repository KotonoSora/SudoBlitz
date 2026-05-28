package com.jn.numgrid.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jn.numgrid.data.GameRecord
import com.jn.numgrid.ui.components.NeonText
import com.jn.numgrid.ui.components.NeonTitle
import com.jn.numgrid.ui.theme.DarkBackground
import com.jn.numgrid.ui.theme.GameTheme
import com.jn.numgrid.ui.theme.NeonBlue
import com.jn.numgrid.ui.theme.NeonCyan
import com.jn.numgrid.ui.theme.NeonMagenta
import com.jn.numgrid.ui.theme.NeonYellow
import com.jn.numgrid.ui.theme.SurfaceDark
import com.jn.numgrid.viewmodel.ProgressViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel, onBack: () -> Unit, modifier: Modifier = Modifier
) {
    val highScore by viewModel.highScore.collectAsState()
    val bestStreak by viewModel.bestStreak.collectAsState()
    val recentRecords by viewModel.recentRecords.collectAsState()

    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = NeonBlue
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonTitle("STATISTICS", NeonBlue, fontSize = 32)
            Spacer(modifier = Modifier.height(24.dp))

            // User's Best Section
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NeonText("PERSONAL RECORDS", NeonYellow, fontSize = 14)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            NeonText("BEST SCORE", NeonCyan, fontSize = 12)
                            Spacer(modifier = Modifier.height(10.dp))
                            NeonText(numberFormat.format(highScore), NeonCyan, fontSize = 24)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            NeonText("LONG STREAK", NeonMagenta, fontSize = 12)
                            Spacer(modifier = Modifier.height(10.dp))
                            NeonText(bestStreak.toString(), NeonMagenta, fontSize = 24)
                        }
                    }
                }
            }

            NeonText(
                "RECENT GAMES", NeonBlue, fontSize = 18, modifier = Modifier.padding(bottom = 8.dp)
            )

            // Game History Section
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NeonText("DATE", NeonBlue, fontSize = 12, modifier = Modifier.weight(1f))
                        NeonText(
                            "DIFF",
                            NeonBlue,
                            fontSize = 12,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                        )
                        NeonText(
                            "SCORE",
                            NeonBlue,
                            fontSize = 12,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End,
                        )
                    }
                    HorizontalDivider(
                        color = NeonBlue, modifier = Modifier.padding(vertical = 8.dp)
                    )

                    LazyColumn {
                        items(recentRecords) { record ->
                            HistoryRow(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryRow(record: GameRecord) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd\nHH:mm", LocalLocale.current.platformLocale)
    val dateStr = dateFormat.format(Date(record.timestamp))
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NeonText(dateStr, Color.White, fontSize = 10, modifier = Modifier.weight(1f))
        NeonText(
            record.difficulty.take(4),
            NeonCyan,
            fontSize = 10,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        NeonText(
            numberFormat.format(record.score),
            NeonMagenta,
            fontSize = 10,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    GameTheme {
        ProgressScreen(
            viewModel = rememberPreviewProgressViewModel(), onBack = {})
    }
}
