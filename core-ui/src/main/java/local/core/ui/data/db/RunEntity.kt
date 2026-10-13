package local.core.ui.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "runs", indices = [Index(value = ["ended_at"])])
data class RunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val level: Int,
    val score: Int,
    val reward: Int,
    @ColumnInfo(name = "duration_ms") val durationMs: Long,
    @ColumnInfo(name = "pipes_passed") val pipesPassed: Int,
    @ColumnInfo(name = "end_cause") val endCause: String,
    @ColumnInfo(name = "shield_uses") val shieldUses: Int = 0,
    @ColumnInfo(name = "multiplier_uses") val multiplierUses: Int = 0,
    @ColumnInfo(name = "auto_play_uses") val autoPlayUses: Int = 0,
    @ColumnInfo(name = "death_x") val deathX: Float? = null,
    @ColumnInfo(name = "death_y") val deathY: Float? = null,
    @ColumnInfo(name = "started_at") val startedAt: Long,
    @ColumnInfo(name = "ended_at") val endedAt: Long
)
