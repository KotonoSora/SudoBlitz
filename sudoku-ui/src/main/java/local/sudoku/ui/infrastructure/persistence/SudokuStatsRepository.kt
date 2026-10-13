package local.sudoku.ui.infrastructure.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sudokuStatsDataStore: DataStore<Preferences> by preferencesDataStore(name = "sudoku_stats")

/**
 * Sudoku-specific stats that intentionally stay OUT of `:core-ui`:
 * streak and games counters. Coins/inventory/audio/history live in core
 * (`user_settings` + `game_history.db` v1, no migration).
 */
class SudokuStatsRepository(private val context: Context) {

    private object Keys {
        val BEST_STREAK = intPreferencesKey("best_streak")
        val GAMES_PLAYED = intPreferencesKey("games_played")
        val GAMES_WON = intPreferencesKey("games_won")
    }

    val bestStreakFlow: Flow<Int> = context.sudokuStatsDataStore.data
        .map { it[Keys.BEST_STREAK] ?: 0 }

    val gamesPlayedFlow: Flow<Int> = context.sudokuStatsDataStore.data
        .map { it[Keys.GAMES_PLAYED] ?: 0 }

    val gamesWonFlow: Flow<Int> = context.sudokuStatsDataStore.data
        .map { it[Keys.GAMES_WON] ?: 0 }

    suspend fun updateBestStreak(streak: Int) {
        context.sudokuStatsDataStore.edit { prefs ->
            val current = prefs[Keys.BEST_STREAK] ?: 0
            if (streak > current) {
                prefs[Keys.BEST_STREAK] = streak
            }
        }
    }

    suspend fun incrementGamesPlayed() {
        context.sudokuStatsDataStore.edit { prefs ->
            prefs[Keys.GAMES_PLAYED] = (prefs[Keys.GAMES_PLAYED] ?: 0) + 1
        }
    }

    suspend fun incrementGamesWon() {
        context.sudokuStatsDataStore.edit { prefs ->
            prefs[Keys.GAMES_WON] = (prefs[Keys.GAMES_WON] ?: 0) + 1
        }
    }
}
