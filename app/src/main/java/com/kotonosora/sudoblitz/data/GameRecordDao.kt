package com.kotonosora.sudoblitz.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {
    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT 50")
    fun getRecentRecords(): Flow<List<GameRecord>>

    @Insert
    suspend fun insertRecord(record: GameRecord)

    @Query("SELECT MAX(score) FROM game_records")
    fun getHighScore(): Flow<Int?>
}
