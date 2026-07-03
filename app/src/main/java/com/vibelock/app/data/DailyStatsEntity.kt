package com.vibelock.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val dateString: String, // format: "YYYY-MM-DD" to ensure one entry per day
    val timestamp: Long,
    val xp: Int,
    val level: Int,
    val currentStreak: Int
)

@Dao
interface DailyStatsDao {
    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyStat(stat: DailyStatsEntity): Long

    @JvmSuppressWildcards
    @Query("SELECT * FROM daily_stats WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getStatsInRange(startTime: Long, endTime: Long): List<DailyStatsEntity>
    
    @JvmSuppressWildcards
    @Query("SELECT * FROM daily_stats ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestStat(): DailyStatsEntity?
}
