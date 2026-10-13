package com.jn.numgrid

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import local.sudoku.ui.di.AppContainerHost
import local.sudoku.ui.di.AppViewModelFactory
import local.sudoku.ui.di.LocalAppContainer
import local.sudoku.ui.infrastructure.audio.LocalSoundManager
import local.sudoku.ui.infrastructure.audio.SoundManager
import local.sudoku.ui.presentation.MainApp
import local.sudoku.ui.presentation.theme.GameTheme

class MainActivity : ComponentActivity() {
    private fun enterFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterFullscreen()

        val container = (application as AppContainerHost).container

        setContent {
            val soundManager = remember { SoundManager(applicationContext) }

            DisposableEffect(Unit) {
                onDispose { soundManager.release() }
            }

            CompositionLocalProvider(
                LocalSoundManager provides soundManager,
                LocalAppContainer provides container
            ) {
                GameTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val factory = remember(container) { AppViewModelFactory(container) }
                        MainApp(factory = factory)
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            enterFullscreen()
        }
    }
}
