package com.vibelock.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface JournalDao {
    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntity): Long

    @JvmSuppressWildcards
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    suspend fun getAllEntries(): List<JournalEntity>

    @JvmSuppressWildcards
    @Query("SELECT * FROM journal_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    suspend fun getEntriesInRange(startTime: Long, endTime: Long): List<JournalEntity>
}
