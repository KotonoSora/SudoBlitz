package local.sudoku.ui.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
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
fun GameTheme(content: @Composable () -> Unit) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val context = view.context
            if (context is Activity) {
                val window = context.window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                    false
            }
        }
    }

    MaterialTheme(
        colorScheme = ArcadeColorScheme, typography = Typography, content = content
    )
}
