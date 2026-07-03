package com.vibelock.app.data

import com.vibelock.app.engine.UserState
import java.util.Calendar

data class WrappedTheme(val title: String, val percentage: Int)

data class WrappedData(
    val totalCheckIns: Int,
    val dominantVibe: String,
    val bestStreak: Int,
    val startLevel: Int,
    val endLevel: Int,
    val themes: List<WrappedTheme>,
    val aiMessage: String,
    val isMock: Boolean = false
)

class WrappedRepository(
    private val journalDao: JournalDao,
    private val dailyStatsDao: DailyStatsDao
) {
    suspend fun getWrappedDataForCurrentMonth(userState: UserState): WrappedData {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endTime = calendar.timeInMillis

        val monthEntries = journalDao.getEntriesInRange(startTime, endTime)
        
        // If not enough entries for a meaningful wrapped (e.g. less than 5), return Mock Data
        if (monthEntries.size < 5) {
            return getMockWrappedData(userState)
        }

        val totalCheckIns = monthEntries.size
        
        // Calculate Dominant Vibe
        val vibeCounts = monthEntries.groupingBy { it.selectedVibe }.eachCount()
        val dominantVibe = vibeCounts.maxByOrNull { it.value }?.key ?: "Unknown"

        val stats = dailyStatsDao.getStatsInRange(startTime, endTime)
        val startLevel = stats.firstOrNull()?.level ?: userState.level
        val endLevel = stats.lastOrNull()?.level ?: userState.level

        // Best streak is tracked overall in UserState for now, or we could find the max in daily stats
        val bestStreak = stats.maxOfOrNull { it.currentStreak } ?: userState.currentStreak

        // Themes and AI message will be populated by Gemini in the next step, so we leave them empty for now.
        return WrappedData(
            totalCheckIns = totalCheckIns,
            dominantVibe = dominantVibe,
            bestStreak = bestStreak,
            startLevel = startLevel,
            endLevel = endLevel,
            themes = emptyList(),
            aiMessage = ""
        )
    }

    private fun getMockWrappedData(userState: UserState): WrappedData {
        return WrappedData(
            totalCheckIns = 0,
            dominantVibe = "Unknown",
            bestStreak = 0,
            startLevel = userState.level,
            endLevel = userState.level,
            themes = emptyList(),
            aiMessage = "Not enough data for Aura Vibe Wrapped.",
            isMock = true
        )
    }
}
