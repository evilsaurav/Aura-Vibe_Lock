package com.vibelock.app.retention

data class ProtectionState(
    var streakShieldsCount: Int = 0,
    var streakLastBrokenTimestamp: Long = 0L,
    var pendingRevivalStreakDays: Int = 0
)

object StreakProtection {

    fun consumeShieldIfAvailable(state: ProtectionState): Boolean {
        if (state.streakShieldsCount > 0) {
            state.streakShieldsCount -= 1
            return true
        }
        return false
    }

    fun calculateRevivalCost(streakDaysLost: Int): Int {
        val baseCost = streakDaysLost * 5
        return baseCost.coerceAtMost(500)
    }

    fun canRevive(state: ProtectionState): Boolean {
        val twentyFourHours = 24 * 60 * 60 * 1000L
        val timeSinceBroken = System.currentTimeMillis() - state.streakLastBrokenTimestamp
        return timeSinceBroken <= twentyFourHours && state.pendingRevivalStreakDays > 0
    }
}
