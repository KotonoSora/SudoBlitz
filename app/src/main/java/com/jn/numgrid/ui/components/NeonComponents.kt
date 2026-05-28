package com.jn.numgrid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import com.jn.numgrid.ui.theme.PressStart2P

val RetroFont = PressStart2P

@Composable
fun NeonButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    textSize: TextUnit = 16.sp,
) {
    val shape = RoundedCornerShape(50)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
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
