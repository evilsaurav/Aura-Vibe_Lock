package com.vibelock.app.gamification

data class DailyMission(
    val id: String,
    val title: String,
    val xpReward: Int,
    val type: MissionType,
    var isComplete: Boolean = false
)

enum class MissionType { BASE, ROTATING, WEEKLY_SPECIAL }

object MissionEngine {
    
    // Always present mission
    val baseMission = DailyMission(
        id = "M_BASE_1",
        title = "🔒 Lock Your Vibe Today",
        xpReward = 100,
        type = MissionType.BASE
    )

    // Pool of rotating missions
    private val rotatingPool = listOf(
        DailyMission("R_1", "Check in before 9 AM", 150, MissionType.ROTATING),
        DailyMission("R_2", "Select 'Grind' vibe", 80, MissionType.ROTATING),
        DailyMission("R_3", "Open the app 3 times today", 60, MissionType.ROTATING),
        DailyMission("R_4", "Check in after 10 PM", 100, MissionType.ROTATING)
    )

    // Weekly special
    val weeklyMission = DailyMission(
        id = "W_1",
        title = "Grind Week — select Grind vibe 5 times",
        xpReward = 500,
        type = MissionType.WEEKLY_SPECIAL
    )

    fun getTodayMissions(): List<DailyMission> {
        // In reality, select based on current date seed
        val todayRotating = rotatingPool.first()
        return listOf(baseMission, todayRotating, weeklyMission)
    }

    fun completeMission(missionId: String, currentXP: Int): Int {
        // Find mission, mark complete, add XP.
        // Return new total XP.
        return currentXP // Placeholder for DB logic
    }
}
