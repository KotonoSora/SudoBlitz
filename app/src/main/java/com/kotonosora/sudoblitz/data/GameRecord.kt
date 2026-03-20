package com.kotonosora.sudoblitz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val difficulty: String,
    val size: Int,
    val isVictory: Boolean
)
