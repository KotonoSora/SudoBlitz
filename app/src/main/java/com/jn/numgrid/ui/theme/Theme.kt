package com.jn.numgrid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Enforce strict dark arcade theme
private val ArcadeColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DarkBackground,
    primaryContainer = SurfaceDark,
    onPrimaryContainer = NeonCyan,
    secondary = NeonMagenta,
    onSecondary = DarkBackground,
    secondaryContainer = SurfaceDark,
    onSecondaryContainer = NeonMagenta,
    tertiary = NeonYellow,
    onTertiary = DarkBackground,
    tertiaryContainer = SurfaceDark,
    onTertiaryContainer = NeonYellow,
    background = DarkBackground,
    onBackground = Color.White,
    surface = SurfaceDark,
    onSurface = Color.White,
    error = NeonRed,
    onError = DarkBackground
)

@Composable
fun GameTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArcadeColorScheme, typography = Typography, content = content
    )
}
