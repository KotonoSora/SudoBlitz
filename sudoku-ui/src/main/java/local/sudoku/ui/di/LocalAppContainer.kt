package local.sudoku.ui.di

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("No AppContainer provided")
}
