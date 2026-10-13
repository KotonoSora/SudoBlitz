package local.core.ui.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RunEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao
}

/**
 * Policy: schema changes MUST ship a tested `Migration` — never
 * `fallbackToDestructiveMigration()`. High score derives from `runs`, so a
 * wipe would silently reset it.
 */

fun createGameDatabase(context: Context): GameDatabase =
    Room.databaseBuilder(context.applicationContext, GameDatabase::class.java, "game_history.db")
        .build()
