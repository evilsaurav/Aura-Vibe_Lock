package com.vibelock.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val selectedVibe: String,
    val rawText: String,
    val aiSummary: String,
    val aiPerspective: String,
    val aiSuggestion: String
)
