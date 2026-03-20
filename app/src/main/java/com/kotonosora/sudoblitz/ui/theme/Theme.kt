package com.kotonosora.sudoblitz.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
fun SudoBlitzTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = ArcadeColorScheme,
        typography = Typography,
        content = content
    )
}
