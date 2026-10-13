package local.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import local.core.ui.AppConstants
import local.core.ui.R
import local.core.ui.theme.AppTheme
import local.core.ui.theme.NeonCyan
import local.core.ui.theme.NeonMagenta
import local.core.ui.theme.NeonYellow

@Composable
fun GameHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    coins: Int? = null,
    onBackClick: (() -> Unit)? = null,
    onShopClick: (() -> Unit)? = null,
    onCoinsClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .safeContentPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .zIndex(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(44.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val paint = Paint().apply {
                                    color = Color.White.copy(alpha = 0.2f)
                                    nativePaint.apply {
                                        setShadowLayer(10.dp.toPx(), 0f, 0f, NeonCyan.toArgb())
                                    }
                                }
                                canvas.drawOutline(
                                    outline = CircleShape.createOutline(
                                        size,
                                        layoutDirection,
                                        this
                                    ),
                                    paint = paint
                                )
                            }
                        }
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.desc_back),
                        tint = NeonCyan
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (coins != null) {
                CoinBadge(coins = coins, onClick = onCoinsClick)
                if (onShopClick != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            if (onShopClick != null) {
                IconButton(
                    onClick = onShopClick,
                    modifier = Modifier
                        .size(44.dp)
                        .drawBehind {
                            drawIntoCanvas { canvas ->
                                val paint = Paint().apply {
                                    color = Color.White.copy(alpha = 0.2f)
                                    nativePaint.apply {
                                        setShadowLayer(10.dp.toPx(), 0f, 0f, NeonMagenta.toArgb())
                                    }
                                }
                                canvas.drawOutline(
                                    outline = CircleShape.createOutline(
                                        size,
                                        layoutDirection,
                                        this
                                    ),
                                    paint = paint
                                )
                            }
                        }
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shop",
                        tint = NeonMagenta
                    )
                }
            }
        }
    }
}

@Composable
fun CoinBadge(
    coins: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.6f),
        border = BorderStroke(2.dp, NeonCyan),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color.Transparent
                        nativePaint.apply {
                            setShadowLayer(12.dp.toPx(), 0f, 0f, NeonCyan.toArgb())
                        }
                    }
                    canvas.drawOutline(
                        outline = RoundedCornerShape(20.dp).createOutline(
                            size,
                            layoutDirection,
                            this
                        ),
                        paint = paint
                    )
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = coins.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = NeonCyan
            )
        }
    }
}

@Composable
fun AudioSettingsGroup(
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    onMusicToggle: (Boolean) -> Unit,
    onSfxToggle: (Boolean) -> Unit
) {
    Text(
        text = stringResource(R.string.title_sound).uppercase(),
        style = MaterialTheme.typography.titleLarge,
        color = NeonYellow,
        modifier = Modifier.padding(start = 8.dp, bottom = 12.dp)
    )

    SettingsToggleItem(
        icon = Icons.Default.MusicNote,
        title = stringResource(R.string.label_music),
        description = stringResource(R.string.desc_music_toggle),
        color = NeonCyan,
        enabled = musicEnabled,
        onToggle = onMusicToggle
    )

    Spacer(modifier = Modifier.height(16.dp))

    SettingsToggleItem(
        icon = Icons.AutoMirrored.Filled.VolumeUp,
        title = stringResource(R.string.label_sfx),
        description = stringResource(R.string.desc_sfx_toggle),
        color = NeonMagenta,
        enabled = sfxEnabled,
        onToggle = onSfxToggle
    )
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    color: Color,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Black.copy(alpha = 0.5f),
        border = BorderStroke(2.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (enabled) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            this.color = Color.Transparent
                            nativePaint.apply {
                                setShadowLayer(10.dp.toPx(), 0f, 0f, color.toArgb())
                            }
                        }
                        canvas.drawOutline(
                            outline = RoundedCornerShape(16.dp).createOutline(
                                size,
                                layoutDirection,
                                this
                            ),
                            paint = paint
                        )
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle(!enabled) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = description,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Switch(
                checked = enabled,
                onCheckedChange = onToggle,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Preview(name = "Header - Full", group = "Components")
@Composable
fun GameHeaderFullPreview() {
    AppTheme {
        GameHeader(
            title = "SETTINGS",
            coins = AppConstants.DEFAULT_INITIAL_COINS,
            onBackClick = {},
            onShopClick = {}
        )
    }
}

@Preview(name = "Header - Simple", group = "Components")
@Composable
fun GameHeaderSimplePreview() {
    AppTheme {
        GameHeader(
            title = "SKYHOP",
            coins = AppConstants.DEFAULT_INITIAL_COINS
        )
    }
}

@Preview(name = "Coin Badge", group = "Components")
@Composable
fun CoinBadgePreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CoinBadge(coins = AppConstants.DEFAULT_INITIAL_COINS)
        }
    }
}

@Preview(name = "Settings Toggle - Enabled", group = "Components")
@Composable
fun SettingsToggleEnabledPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SettingsToggleItem(
                icon = Icons.Default.MusicNote,
                title = "Music",
                description = "Enable background music",
                color = NeonCyan,
                enabled = true,
                onToggle = {}
            )
        }
    }
}

@Preview(name = "Settings Toggle - Disabled", group = "Components")
@Composable
fun SettingsToggleDisabledPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SettingsToggleItem(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "SFX",
                description = "Enable sound effects",
                color = NeonMagenta,
                enabled = false,
                onToggle = {}
            )
        }
    }
}
