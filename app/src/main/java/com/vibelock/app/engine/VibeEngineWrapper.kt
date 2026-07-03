package com.vibelock.app.engine

enum class AuraTier(val title: String, val levelRange: IntRange, val colorHex: Long) {
    NOOB_CIVILIAN("NOOB CIVILIAN", 1..5, 0xFFB0BEC5), // Pastel Gray
    CERTIFIED_COOKER("CERTIFIED COOKER", 6..15, 0xFF39FF14), // Electric Neon Green
    VIBE_ARCHITECT("VIBE ARCHITECT", 16..30, 0xFFB026FF), // Cyberpunk Purple Glow
    CHIEF_RIZZER("CHIEF RIZZER", 31..50, 0xFFFFD700), // Premium Black & Gold
    AURA_GOD("AURA GOD", 51..Int.MAX_VALUE, 0xFF000000); // Handled specially for holographic gradient

    companion object {
        fun getTierForLevel(level: Int): AuraTier {
            return values().firstOrNull { level in it.levelRange } ?: AURA_GOD
        }
    }
}

data class UserState(
    val xp: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val highestStreak: Int = 0,
    val lastCheckInTimestamp: Long = 0L,
    val auraCode: String = "",
    val shields: Int = 0,
    val lastMissionDate: String = "",
    val completedMissionIds: String = "",
    val avatarUrl: String = "",
    val currentVibe: String = ""
)

class VibeEngineWrapper {

    fun calculateXpRequiredForNextLevel(level: Int): Int {
        return level * 100
    }

    fun processCheckIn(currentState: UserState, currentTimestamp: Long): UserState {
        val lastCheckIn = currentState.lastCheckInTimestamp
        
        // Initial CheckIn
        if (lastCheckIn == 0L) {
            return applyXpAndLevelUp(currentState.copy(
                currentStreak = 1,
                highestStreak = 1,
                lastCheckInTimestamp = currentTimestamp
            ), 100) // Initial bonus
        }

        val calCurrent = java.util.Calendar.getInstance().apply { timeInMillis = currentTimestamp }
        val calLast = java.util.Calendar.getInstance().apply { timeInMillis = lastCheckIn }

        // Strip time to just compare days
        calCurrent.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calCurrent.set(java.util.Calendar.MINUTE, 0)
        calCurrent.set(java.util.Calendar.SECOND, 0)
        calCurrent.set(java.util.Calendar.MILLISECOND, 0)

        calLast.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calLast.set(java.util.Calendar.MINUTE, 0)
        calLast.set(java.util.Calendar.SECOND, 0)
        calLast.set(java.util.Calendar.MILLISECOND, 0)

        val daysDifference = ((calCurrent.timeInMillis - calLast.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()

        return when {
            daysDifference == 1 -> {
                // Next day: Streak Continues
                val newStreak = currentState.currentStreak + 1
                val highest = maxOf(newStreak, currentState.highestStreak)
                applyXpAndLevelUp(currentState.copy(
                    currentStreak = newStreak,
                    highestStreak = highest,
                    lastCheckInTimestamp = currentTimestamp
                ), 50 + (newStreak * 10)) // Full daily XP + streak bonus
            }
            daysDifference == 0 -> {
                // Same day check-in: Give them a small base XP for locking vibe again
                applyXpAndLevelUp(currentState.copy(
                    lastCheckInTimestamp = currentTimestamp
                ), 15) // Re-lock base XP
            }
            else -> {
                // Streak breaks (daysDifference > 1 or negative somehow)
                applyXpAndLevelUp(currentState.copy(
                    currentStreak = 1,
                    lastCheckInTimestamp = currentTimestamp
                ), 50) // Just base daily XP, no streak
            }
        }
    }

    private fun applyXpAndLevelUp(state: UserState, xpGained: Int): UserState {
        var newXp = state.xp + xpGained
        var newLevel = state.level
        var xpRequired = calculateXpRequiredForNextLevel(newLevel)

        while (newXp >= xpRequired) {
            newXp -= xpRequired
            newLevel++
            xpRequired = calculateXpRequiredForNextLevel(newLevel)
        }

        return state.copy(
            level = newLevel,
            xp = newXp
        )
    }
}
