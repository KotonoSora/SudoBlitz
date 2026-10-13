package local.sudoku.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.sudoku.ui.infrastructure.audio.LocalSoundManager
import local.sudoku.ui.presentation.theme.CoinGold
import local.sudoku.ui.presentation.theme.NeonCyan
import local.sudoku.ui.presentation.theme.NeonYellow
import local.sudoku.ui.presentation.theme.PressStart2P

val RetroFont = PressStart2P

@Composable
fun NeonButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    textSize: TextUnit = 16.sp,
    height: Int = 64,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(shape)
            .background(color.copy(alpha = 0.15f))
            .border(2.dp, color, shape)
            .clickable { onClick() }
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(
            text = text,
            color = color,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            fontFamily = RetroFont,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun NeonIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = NeonCyan
) {
    val soundManager = LocalSoundManager.current
    IconButton(
        onClick = {
            soundManager.playTap()
            onClick()
        },
        modifier = modifier
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint)
    }
}

@Composable
fun CoinCapsule(
    coins: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(NeonYellow.copy(alpha = 0.1f))
            .border(1.dp, NeonYellow.copy(alpha = 0.5f), RoundedCornerShape(50))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Rounded.MonetizationOn,
                contentDescription = null,
                tint = CoinGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            NeonText(
                text = coins.toString(),
                color = NeonYellow,
                fontSize = 12,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Circular power-up button (coin-spent directly in gameplay).
 * Adapted from Trixo's gameplay PowerUpButton. Callers own tap sounds.
 */
@Composable
fun PowerUpButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (enabled) NeonCyan else Color.DarkGray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, NeonCyan.copy(alpha = if (enabled) 0.3f else 0.1f), CircleShape)
                .clickable(enabled = enabled) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        NeonText(
            text = label,
            color = if (enabled) NeonYellow else Color.DarkGray,
            fontSize = 12,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NeonTitle(
    resId: Int, color: Color, modifier: Modifier = Modifier, fontSize: Int = 48
) {
    NeonTitle(
        text = stringResource(id = resId), color = color, modifier = modifier, fontSize = fontSize
    )
}

@Composable
fun NeonTitle(
    text: String, color: Color, modifier: Modifier = Modifier, fontSize: Int = 48,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Black,
        fontFamily = RetroFont,
        modifier = modifier,
        style = TextStyle(
            shadow = Shadow(
                color = color, blurRadius = 16f
            )
        ),
        textAlign = textAlign
    )
}

@Composable
fun NeonText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    fontSize: Int = 20,
    fontWeight: FontWeight = FontWeight.Bold,
    textAlign: TextAlign? = null
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize.sp,
        fontWeight = fontWeight,
        fontFamily = RetroFont,
        modifier = modifier,
        textAlign = textAlign,
        lineHeight = (fontSize * 1.2).sp
    )
}
