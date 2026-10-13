package local.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import local.core.ui.theme.AppTheme
import local.core.ui.theme.NeonCyan
import local.core.ui.theme.NeonGreen
import local.core.ui.theme.NeonMagenta

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = NeonCyan,
    glowColor: Color? = null,
    textColor: Color = Color.Black,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
    height: Dp = 56.dp,
    borderWidth: Dp = 2.dp,
    borderColor: Color = Color.White,
    textStyle: TextStyle? = null,
    horizontalPadding: Dp = 16.dp
) {
    val finalGlowColor = glowColor ?: backgroundColor.copy(alpha = 0.5f)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(if (enabled) scale else 1f)
            .height(height)
            .drawBehind {
                if (enabled) {
                    val paint = Paint().apply {
                        color = finalGlowColor
                        nativePaint.apply {
                            setShadowLayer(
                                15.dp.toPx(),
                                0f, 0f,
                                finalGlowColor.toArgb()
                            )
                        }
                    }
                    drawIntoCanvas { canvas ->
                        canvas.drawOutline(
                            outline = shape.createOutline(size, layoutDirection, this),
                            paint = paint
                        )
                    }
                }
            }
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (enabled) {
                        listOf(
                            backgroundColor.copy(alpha = 0.7f),
                            backgroundColor
                        )
                    } else {
                        listOf(
                            Color.DarkGray,
                            Color.Black
                        )
                    }
                )
            )
            .border(borderWidth, borderColor, shape)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) {
                    CompositionLocalProvider(LocalContentColor provides textColor) {
                        icon()
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                style = textStyle ?: MaterialTheme.typography.labelLarge,
                color = if (enabled) textColor else Color.Gray,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Button - Cyan (Default)", group = "Components")
@Composable
fun GameButtonCyanPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameButton(text = "START GAME", onClick = {})
        }
    }
}

@Preview(name = "Button - Green (Challenge)", group = "Components")
@Composable
fun GameButtonGreenPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameButton(text = "PLAY CHALLENGE", onClick = {}, backgroundColor = NeonGreen)
        }
    }
}

@Preview(name = "Button - Magenta (Shop)", group = "Components")
@Composable
fun GameButtonMagentaPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameButton(
                text = "BUY SKIN",
                onClick = {},
                backgroundColor = NeonMagenta,
                textColor = Color.White
            )
        }
    }
}

@Preview(name = "Button - Disabled", group = "Components")
@Composable
fun GameButtonDisabledPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            GameButton(text = "LOCKED", onClick = {}, enabled = false)
        }
    }
}
