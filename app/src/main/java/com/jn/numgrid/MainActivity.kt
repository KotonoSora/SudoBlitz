package com.jn.numgrid

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.jn.numgrid.audio.HapticManager
import com.jn.numgrid.audio.SoundManager
import com.jn.numgrid.data.AppDatabase
import com.jn.numgrid.data.UserPreferencesRepository
import com.jn.numgrid.data.dataStore
import com.jn.numgrid.ui.theme.GameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = UserPreferencesRepository(applicationContext.dataStore)
        val database = AppDatabase.getDatabase(applicationContext)
        val gameRecordDao = database.gameRecordDao()

        setContent {
            val soundManager = remember {
                SoundManager(
                    applicationContext, repository
                )
            }
            val hapticManager = remember {
                HapticManager(
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
                    GameApp(
                        repository = repository,
                        gameRecordDao = gameRecordDao,
                        application = application as Application,
                        soundManager = soundManager,
                        hapticManager = hapticManager
                    )
                }
            }
        }
    }
}
