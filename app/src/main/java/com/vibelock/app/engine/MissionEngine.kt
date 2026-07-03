package com.vibelock.app.engine

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MissionType {
    CHECK_IN,          // Basic check-in mission (always present)
    EARLY_BIRD,        // Check in before 9 AM
    NIGHT_OWL,         // Check in after 10 PM
    SPECIFIC_VIBE,     // Check in with a specific vibe
    VISIT_LEADERBOARD, // Open the leaderboard screen
    COMPLETE_ALL_3     // Meta-mission: complete all 3 missions
}

data class DailyMission(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val type: MissionType,
    val targetVibe: String? = null,
    val isComplete: Boolean = false
)

object MissionEngine {

    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Generates 3 daily missions based on the given date string.
     * @param dateString format "yyyy-MM-dd"
     * @param completedMissionIds List of mission IDs that the user has already completed today
     */
    fun generateDailyMissions(dateString: String, completedMissionIds: List<String>): List<DailyMission> {
        val missions = mutableListOf<DailyMission>()
        
        // Calculate stable day index based on dateString
        val dayIndex = dateString.hashCode().let { if (it < 0) -it else it }
        val weekNumber = dayIndex / 7

        // MISSION 1: Always check-in (base mission)
        missions.add(
            DailyMission(
                id = "daily_checkin",
                title = "Lock Your Vibe",
                description = "Check in today",
                xpReward = 100,
                type = MissionType.CHECK_IN,
                isComplete = completedMissionIds.contains("daily_checkin")
            )
        )

        // MISSION 2: Time or vibe based (rotates daily)
        val mission2 = when (dayIndex % 5) {
            0 -> DailyMission(id = "early_bird", title = "Early Bird", description = "Check in before 9 AM", xpReward = 150, type = MissionType.EARLY_BIRD)
            1 -> DailyMission(id = "grind_vibe", title = "Grind Mode", description = "Select '⚡ Grind' vibe today", xpReward = 100, type = MissionType.SPECIFIC_VIBE, targetVibe = "⚡ Grind")
            2 -> DailyMission(id = "leaderboard", title = "Check the Ranks", description = "Visit the leaderboard", xpReward = 50, type = MissionType.VISIT_LEADERBOARD)
            3 -> DailyMission(id = "night_owl", title = "Night Owl", description = "Check in after 10 PM", xpReward = 120, type = MissionType.NIGHT_OWL)
            else -> DailyMission(id = "chill_vibe", title = "Chill Vibes Only", description = "Select '🌊 Chill' vibe today", xpReward = 100, type = MissionType.SPECIFIC_VIBE, targetVibe = "🌊 Chill")
        }
        missions.add(mission2.copy(isComplete = completedMissionIds.contains(mission2.id)))

        // MISSION 3: Weekly special
        val mission3 = when (weekNumber % 4) {
            0 -> DailyMission(id = "week_slay", title = "Slay Week", description = "It's Slay Week! Select '✨ Slay' vibe", xpReward = 200, type = MissionType.SPECIFIC_VIBE, targetVibe = "✨ Slay")
            1 -> DailyMission(id = "week_early", title = "Early Bird Season", description = "This week: check in before 9 AM", xpReward = 200, type = MissionType.EARLY_BIRD)
            2 -> DailyMission(id = "week_social", title = "Social Week", description = "Visit the leaderboard to check your rank", xpReward = 175, type = MissionType.VISIT_LEADERBOARD)
            else -> DailyMission(id = "week_chaos", title = "Chaos Week", description = "Select '🌀 Chaos' vibe", xpReward = 200, type = MissionType.SPECIFIC_VIBE, targetVibe = "🌀 Chaos")
        }
        missions.add(mission3.copy(isComplete = completedMissionIds.contains(mission3.id)))

        return missions
    }
    
    /**
     * Call this when an action occurs to see if any missions complete.
     */
    fun processAction(
        actionType: MissionType, 
        userState: UserState, 
        currentVibe: String? = null, 
        actionTime: Long = System.currentTimeMillis()
    ): Pair<UserState, Int> {
        val currentDate = getCurrentDateString()
        
        // Reset missions if it's a new day
        var state = if (userState.lastMissionDate != currentDate) {
            userState.copy(lastMissionDate = currentDate, completedMissionIds = "")
        } else {
            userState
        }
        
        val completedIds = state.completedMissionIds.split(",").filter { it.isNotEmpty() }.toMutableList()
        val currentMissions = generateDailyMissions(currentDate, completedIds)
        
        var totalXpGained = 0
        var newlyCompleted = false

        for (mission in currentMissions) {
            if (mission.isComplete) continue

            var missionMatches = false
            when (mission.type) {
                MissionType.CHECK_IN -> {
                    if (actionType == MissionType.CHECK_IN) missionMatches = true
                }
                MissionType.VISIT_LEADERBOARD -> {
                    if (actionType == MissionType.VISIT_LEADERBOARD) missionMatches = true
                }
                MissionType.EARLY_BIRD -> {
                    if (actionType == MissionType.CHECK_IN) {
                        val calendar = java.util.Calendar.getInstance()
                        calendar.timeInMillis = actionTime
                        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                        if (hour < 9) missionMatches = true
                    }
                }
                MissionType.NIGHT_OWL -> {
                    if (actionType == MissionType.CHECK_IN) {
                        val calendar = java.util.Calendar.getInstance()
                        calendar.timeInMillis = actionTime
                        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                        if (hour >= 22) missionMatches = true
                    }
                }
                MissionType.SPECIFIC_VIBE -> {
                    if (actionType == MissionType.CHECK_IN && currentVibe == mission.targetVibe) {
                        missionMatches = true
                    }
                }
                else -> {}
            }

            if (missionMatches) {
                completedIds.add(mission.id)
                totalXpGained += mission.xpReward
                newlyCompleted = true
            }
        }

        // Check if all 3 are completed for the first time
        if (newlyCompleted && completedIds.size >= 3 && !completedIds.contains("ALL_3_BONUS")) {
            completedIds.add("ALL_3_BONUS")
            totalXpGained += 150 // Bonus XP
        }

        if (newlyCompleted) {
            state = state.copy(
                xp = state.xp + totalXpGained,
                completedMissionIds = completedIds.joinToString(",")
            )
        }

        return Pair(state, totalXpGained)
    }
}
