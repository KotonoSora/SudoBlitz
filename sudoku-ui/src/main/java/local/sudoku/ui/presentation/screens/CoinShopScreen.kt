package local.sudoku.ui.presentation.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.core.billing.BillingStatus
import local.core.shop.coins.CoinPackItem
import local.sudoku.ui.infrastructure.audio.LocalSoundManager
import local.sudoku.ui.presentation.components.CoinCapsule
import local.sudoku.ui.presentation.components.NeonButton
import local.sudoku.ui.presentation.components.NeonText
import local.sudoku.ui.presentation.components.RetroFont
import local.sudoku.ui.presentation.theme.DarkBackground
import local.sudoku.ui.presentation.theme.GameTheme
import local.sudoku.ui.presentation.theme.NeonCyan
import local.sudoku.ui.presentation.theme.NeonMagenta
import local.sudoku.ui.presentation.viewmodel.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinShopScreen(
    viewModel: ShopViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val status by viewModel.status.collectAsState()
    val soundManager = LocalSoundManager.current

    CoinShopContent(
        coinPacks = products,
        coins = coins,
        status = status,
        onBack = onBack,
        onRetry = { viewModel.retryConnection() },
        onBuyCoinPack = { product, activity ->
            soundManager.playTap()
            viewModel.buyCoinPack(activity, product)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinShopContent(
    coinPacks: List<CoinPackItem>,
    coins: Int,
    status: BillingStatus,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onBuyCoinPack: (CoinPackItem, android.app.Activity) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    NeonText("COIN SHOP", NeonCyan, fontSize = 24)
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
                    CoinCapsule(
                        coins = coins,
                        modifier = Modifier.padding(end = 16.dp)
                    )
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
            NeonText("COIN PACKS", NeonCyan, fontSize = 16)
            Spacer(modifier = Modifier.height(8.dp))
            when {
                status == BillingStatus.ERROR -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NeonText(
                            text = "Store currently unavailable",
                            color = Color.Gray,
                            fontSize = 14,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        NeonButton(
                            text = "TRY AGAIN",
                            color = NeonCyan,
                            onClick = onRetry,
                            textSize = 12.sp,
                            height = 48
                        )
                    }
                }

                status == BillingStatus.EMPTY ||
                    (status == BillingStatus.CONNECTED && coinPacks.isEmpty()) -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val message = when (status) {
                            BillingStatus.EMPTY -> "No products found"
                            else -> "Loading store items..."
                        }
                        NeonText(
                            text = message,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 16
                        )
                    }
                }

                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        coinPacks.forEach { product ->
                            CoinPackRow(
                                product = product,
                                onPurchaseClick = {
                                    activity?.let { onBuyCoinPack(product, it) }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CoinPackRow(
    product: CoinPackItem, onPurchaseClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.name,
                modifier = Modifier
                    .size(48.dp)
                    .background(DarkBackground, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                NeonText(
                    text = product.name,
                    color = Color.White,
                    fontSize = 14,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onPurchaseClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonMagenta, contentColor = DarkBackground
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = product.price.ifEmpty { "Unknown" },
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
fun CoinShopScreenPreview() {
    GameTheme {
        CoinShopContent(
            coinPacks = PreviewData.mockCoinPacks,
            coins = 500,
            status = BillingStatus.CONNECTED,
            onBack = {},
            onRetry = {},
            onBuyCoinPack = { _, _ -> })
    }
}
