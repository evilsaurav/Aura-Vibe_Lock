package com.vibelock.app.engine

import com.vibelock.app.data.UserRepository

class StreakProtectionEngine {

    companion object {
        const val MAX_SHIELDS = 5

        /**
         * Runs after a successful check-in to see if the user earned a new shield.
         * Logic: Earn 1 shield for every 7 days of consecutive check-ins.
         */
        fun evaluateShieldEarnings(userState: UserState): UserState {
            if (userState.currentStreak > 0 && userState.currentStreak % 7 == 0 && userState.shields < MAX_SHIELDS) {
                // Earned a shield
                return userState.copy(shields = userState.shields + 1)
            }
            return userState
        }

        /**
         * Runs daily (or on app open) to check if the user missed a day.
         * If they missed a day, it auto-consumes a shield to protect the streak.
         * If no shields, the streak is broken.
         * Returns a pair of the new UserState and a status message/flag.
         */
        fun evaluateMissedDays(userState: UserState, currentTime: Long): Pair<UserState, String> {
            if (userState.lastCheckInTimestamp == 0L || userState.currentStreak == 0) {
                return Pair(userState, "NO_ACTION")
            }

            val msInDay = 24 * 60 * 60 * 1000L
            val daysSinceLastCheckIn = (currentTime - userState.lastCheckInTimestamp) / msInDay

            return if (daysSinceLastCheckIn >= 2) {
                if (userState.shields > 0) {
                    // Consume a shield, keep streak alive, update timestamp to pretend they checked in yesterday
                    val newTimestamp = userState.lastCheckInTimestamp + msInDay
                    val newState = userState.copy(
                        shields = userState.shields - 1,
                        lastCheckInTimestamp = newTimestamp
                    )
                    Pair(newState, "SHIELD_USED")
                } else {
                    // No shields left, streak broken!
                    val newState = userState.copy(currentStreak = 0)
                    Pair(newState, "STREAK_BROKEN")
                }
            } else {
                Pair(userState, "STREAK_SAFE")
            }
        }

        /**
         * Revives a broken streak by spending XP.
         * Cost = streak_length * 3 (Max 300)
         */
        fun reviveStreak(userState: UserState, oldStreakLength: Int, currentTime: Long): UserState? {
            val cost = Math.min(oldStreakLength * 3, 300)
            if (userState.xp >= cost) {
                return userState.copy(
                    xp = userState.xp - cost,
                    currentStreak = oldStreakLength,
                    lastCheckInTimestamp = currentTime
                )
            }
            return null // Not enough XP
        }
    }
}
