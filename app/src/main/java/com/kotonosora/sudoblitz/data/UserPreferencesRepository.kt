package com.kotonosora.sudoblitz.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_stats")

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val COINS_KEY = intPreferencesKey("coins")
        val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        val BEST_STREAK_KEY = intPreferencesKey("best_streak")
    }

    val coinsFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[COINS_KEY] ?: 100 // Start with 100 coins
    }

    val highScoreFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[HIGH_SCORE_KEY] ?: 0
    }

    val bestStreakFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[BEST_STREAK_KEY] ?: 0
    }

    suspend fun updateCoins(delta: Int) {
        dataStore.edit { preferences ->
            val current = preferences[COINS_KEY] ?: 100
            preferences[COINS_KEY] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun updateHighScore(score: Int) {
        dataStore.edit { preferences ->
            val current = preferences[HIGH_SCORE_KEY] ?: 0
            if (score > current) {
                preferences[HIGH_SCORE_KEY] = score
            }
        }
    }

    suspend fun updateBestStreak(streak: Int) {
        dataStore.edit { preferences ->
            val current = preferences[BEST_STREAK_KEY] ?: 0
            if (streak > current) {
                preferences[BEST_STREAK_KEY] = streak
            }
        }
    }
}
