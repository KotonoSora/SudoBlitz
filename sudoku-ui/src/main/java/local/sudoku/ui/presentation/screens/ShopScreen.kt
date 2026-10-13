package local.sudoku.ui.presentation.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.core.billing.BillingStatus
import local.core.shop.coins.CoinPackItem
import local.sudoku.shop.domain.model.PowerUpShopItem
import local.sudoku.ui.infrastructure.audio.LocalSoundManager
import local.sudoku.ui.presentation.components.NeonText
import local.sudoku.ui.presentation.components.RetroFont
import local.sudoku.ui.presentation.theme.CoinGold
import local.sudoku.ui.presentation.theme.DarkBackground
import local.sudoku.ui.presentation.theme.GameTheme
import local.sudoku.ui.presentation.theme.NeonCyan
import local.sudoku.ui.presentation.theme.NeonGreen
import local.sudoku.ui.presentation.theme.NeonMagenta
import local.sudoku.ui.presentation.theme.SurfaceDark
import local.sudoku.ui.presentation.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: ShopViewModel, onBack: () -> Unit, modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val status by viewModel.status.collectAsState()
    val powerUpCounts by viewModel.powerUpCounts.collectAsState()
    val soundManager = LocalSoundManager.current
    var lastPurchaseOk by remember { mutableStateOf<Boolean?>(null) }

    ShopContent(
        coinPacks = products,
        powerUpItems = viewModel.powerUpItems.filterIsInstance<PowerUpShopItem>(),
        powerUpCounts = powerUpCounts,
        coins = coins,
        status = status,
        purchaseFailed = lastPurchaseOk == false,
        onBack = onBack,
        onBuyCoinPack = { product, activity ->
            soundManager.playTap()
            viewModel.buyCoinPack(activity, product)
        },
        onBuyPowerUp = { item ->
            viewModel.buyPowerUp(item) { ok ->
                lastPurchaseOk = ok
                if (ok) soundManager.playWin() else soundManager.playError()
            }
        },
        modifier = modifier
    )
}

private fun powerUpIcon(inventoryId: String): ImageVector = when (inventoryId) {
    "extra_time" -> Icons.Rounded.AccessTime
    "undo" -> Icons.AutoMirrored.Rounded.Undo
    else -> Icons.Rounded.Lightbulb
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopContent(
    coinPacks: List<CoinPackItem>,
    powerUpItems: List<PowerUpShopItem>,
    powerUpCounts: Map<String, Int>,
    coins: Int,
    status: BillingStatus,
    purchaseFailed: Boolean,
    onBack: () -> Unit,
    onBuyCoinPack: (CoinPackItem, android.app.Activity) -> Unit,
    onBuyPowerUp: (PowerUpShopItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    NeonText("Shop", NeonCyan, fontSize = 24)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MonetizationOn,
                            contentDescription = "Coins",
                            tint = CoinGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        NeonText(
                            text = coins.toString(), color = CoinGold, fontSize = 18
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = NeonCyan,
                    navigationIconContentColor = NeonCyan,
                    actionIconContentColor = NeonCyan
                ),
            )
        }, modifier = modifier.fillMaxSize(), containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(DarkBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonText("BOOSTS", NeonGreen, fontSize = 16)
            Spacer(modifier = Modifier.height(8.dp))
            if (purchaseFailed) {
                NeonText(
                    text = "NOT ENOUGH COINS",
                    color = NeonMagenta,
                    fontSize = 14,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            powerUpItems.forEach { item ->
                PowerUpRow(
                    item = item,
                    owned = powerUpCounts[item.inventoryId] ?: 0,
                    canAfford = coins >= item.price,
                    onBuy = { onBuyPowerUp(item) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            NeonText("COIN PACKS", NeonCyan, fontSize = 16)
            Spacer(modifier = Modifier.height(8.dp))
            if (coinPacks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val message = when (status) {
                        BillingStatus.CONNECTING -> "Connecting to Store..."
                        BillingStatus.ERROR -> "Store Unavailable"
                        BillingStatus.EMPTY -> "No products found"
                        else -> "Loading store items..."
                    }
                    NeonText(
                        text = message,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 16
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                ) {
                    items(coinPacks) { product ->
                        ProductItem(
                            product = product, onPurchaseClick = {
                                activity?.let { onBuyCoinPack(product, it) }
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun PowerUpRow(
    item: PowerUpShopItem,
    owned: Int,
    canAfford: Boolean,
    onBuy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = powerUpIcon(item.inventoryId),
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                NeonText(text = "${item.name} ×$owned", color = NeonCyan, fontSize = 14)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onBuy,
                enabled = canAfford,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonGreen, contentColor = DarkBackground
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "${item.price}",
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    fontFamily = RetroFont
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: CoinPackItem, onPurchaseClick: () -> Unit, modifier: Modifier = Modifier
) {
    val price = product.price
    val coinsAmount = product.coinAmount

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Coin Icon / Stack
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(DarkBackground, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MonetizationOn,
                    contentDescription = null,
                    tint = CoinGold,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            NeonText(
                text = "$coinsAmount", color = CoinGold, fontSize = 14
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Coins",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onPurchaseClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonMagenta, contentColor = DarkBackground
                ),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
            ) {
                Text(
                    text = price.ifEmpty { "Unknown" },
                    color = DarkBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    fontFamily = RetroFont
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShopScreenPreview() {
    GameTheme {
        ShopContent(
            coinPacks = PreviewData.mockCoinPacks,
            powerUpItems = PreviewData.mockPowerUpItems,
            powerUpCounts = mapOf("extra_time" to 1, "hint" to 2, "undo" to 0),
            coins = 500,
            status = BillingStatus.CONNECTED,
            purchaseFailed = false,
            onBack = {},
            onBuyCoinPack = { _, _ -> },
            onBuyPowerUp = {})
    }
}
