package com.vibelock.app.engine

data class Achievement(
    val id: String,
    val title: String,
    val message: String,
    val iconEmoji: String, // fallback or small icon
    val lottieResId: Int? = null,
    val isUnlocked: Boolean = false
)

object AchievementEngine {
    fun getAchievementsForUser(userState: UserState): List<Achievement> {
        val list = mutableListOf<Achievement>()
        
        // Example Achievements based on level/streak
        list.add(
            Achievement(
                id = "first_checkin",
                title = "Vibe Pioneer",
                message = "Started the journey and locked in the first vibe.",
                iconEmoji = "🌱",
                lottieResId = com.vibelock.app.R.raw.achievement_star,
                isUnlocked = userState.xp > 0
            )
        )
        
        list.add(
            Achievement(
                id = "aura_warrior",
                title = "Aura Warrior",
                message = "Reached Level 5. The aura is getting stronger.",
                iconEmoji = "⚔️",
                lottieResId = com.vibelock.app.R.raw.achievement_fire,
                isUnlocked = userState.level >= 5
            )
        )
        
        list.add(
            Achievement(
                id = "streak_master",
                title = "Streak Master",
                message = "Maintained a 7-day streak without breaking.",
                iconEmoji = "🔥",
                lottieResId = com.vibelock.app.R.raw.achievement_fire,
                isUnlocked = userState.currentStreak >= 7
            )
        )
        
        list.add(
            Achievement(
                id = "aura_god",
                title = "Aura God",
                message = "Achieved the ultimate Aura God status.",
                iconEmoji = "👑",
                lottieResId = com.vibelock.app.R.raw.achievement_trophy,
                isUnlocked = userState.level >= 50
            )
        )
        
        return list
    }
}
