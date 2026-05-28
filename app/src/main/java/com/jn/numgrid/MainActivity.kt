package com.jn.numgrid

import android.app.Application
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jn.numgrid.audio.SoundManager
import com.jn.numgrid.data.AppDatabase
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.data.dataStore
import com.jn.numgrid.ui.theme.GameTheme

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

        val repository = UserPreferencesRepository(applicationContext.dataStore)
        val database = AppDatabase.getDatabase(applicationContext)
        val gameRecordDao = database.gameRecordDao()

        setContent {
            val soundManager = remember {
                SoundManager(
                    applicationContext, repository
                )
            }

            DisposableEffect(Unit) {
                onDispose { soundManager.release() }
            }

            GameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    GameApplication(
                        repository = repository,
                        gameRecordDao = gameRecordDao,
                        application = application as Application,
                        soundManager = soundManager
                    )
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
