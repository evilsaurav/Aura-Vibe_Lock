package com.vibelock.app.social

data class AuraBattle(
    val id: String,
    val player1Id: String,
    val player1Name: String,
    val player2Id: String,
    val player2Name: String,
    val startTime: Long,
    val player1XpGained: Int,
    val player2XpGained: Int,
    val player1StreakActive: Boolean,
    val player2StreakActive: Boolean,
    val status: BattleStatus
)

enum class BattleStatus { PENDING, ACTIVE, COMPLETED }

object BattleSystem {
    
    // Mock Active Battle
    val currentBattle = AuraBattle(
        id = "b_101",
        player1Id = "u_me",
        player1Name = "You",
        player2Id = "u_them",
        player2Name = "AuraGod99",
        startTime = System.currentTimeMillis() - (4 * 24 * 60 * 60 * 1000L), // Day 4 of 7
        player1XpGained = 3450,
        player2XpGained = 2800,
        player1StreakActive = true,
        player2StreakActive = true,
        status = BattleStatus.ACTIVE
    )

    fun getDaysRemaining(battle: AuraBattle): Int {
        val elapsedMs = System.currentTimeMillis() - battle.startTime
        val daysElapsed = (elapsedMs / (1000 * 60 * 60 * 24)).toInt()
        return maxOf(0, 7 - daysElapsed)
    }
}
