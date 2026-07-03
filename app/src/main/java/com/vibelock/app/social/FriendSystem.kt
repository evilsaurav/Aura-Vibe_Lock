package com.vibelock.app.social

import com.vibelock.app.gamification.AuraTier

data class FriendEntity(
    val id: String,
    val username: String,
    val auraCode: String,
    val currentXP: Int,
    val currentStreak: Int,
    val tier: AuraTier,
    val lastCheckIn: Long,
    val weeklyXP: Int,
    val isCheckedInToday: Boolean,
    val battleStatus: String? = null // Battle ID if active
)

object FriendSystem {
    // Mock data for UI building (structured for Firebase Realtime DB future integration)
    val mockFriends = listOf(
        FriendEntity(
            "f1", "SigmaRuler", "AURA-X7K2", 14500, 42, AuraTier.SigmaAura,
            System.currentTimeMillis() - 100000, 3200, true
        ),
        FriendEntity(
            "f2", "VibeQueen", "AURA-B9M1", 4200, 15, AuraTier.VibeLord,
            System.currentTimeMillis() - 50000000, 1800, false
        ),
        FriendEntity(
            "f3", "NoobSlayer", "AURA-C4P9", 800, 3, AuraTier.RisingVibe,
            System.currentTimeMillis() - 90000000, 400, false
        ),
        FriendEntity(
            "f4", "AuraGod99", "AURA-G0D1", 25000, 142, AuraTier.AuraGod,
            System.currentTimeMillis() - 50000, 8500, true, "b_101"
        )
    )
    
    fun getFriendsSortedByWeeklyXP(): List<FriendEntity> {
        return mockFriends.sortedByDescending { it.weeklyXP }
    }
}
