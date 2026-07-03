package com.vibelock.app.gamification

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isHidden: Boolean = false,
    var isUnlocked: Boolean = false
)

object AchievementSystem {
    
    val achievements = listOf(
        // Streak Achievements
        Achievement("S_1", "First Spark", "First ever check-in", "🔥", isHidden = false),
        Achievement("S_2", "Week Warrior", "7-day streak", "🔥", isHidden = false),
        Achievement("S_3", "Monthly Legend", "30-day streak", "🔥", isHidden = false),
        Achievement("S_4", "Century Club", "100-day streak", "🔥", isHidden = false),
        Achievement("S_5", "Year of Aura", "365-day streak", "⚡", isHidden = false),

        // Time Achievements
        Achievement("T_1", "Early God", "Check in before 6 AM", "🌅", isHidden = true),
        Achievement("T_2", "3 AM Vibes", "Check in between 3-4 AM", "🦉", isHidden = true),
        Achievement("T_3", "Midnight Rush", "Check in at exactly 11:59 PM", "⚡", isHidden = true),

        // Vibe Achievements
        Achievement("V_1", "Grind Never Stops", "Select Grind 30 times", "😤", isHidden = true),
        Achievement("V_2", "Chill Master", "Select Chill 30 times", "🌊", isHidden = true),

        // Secret Achievements
        Achievement("SEC_1", "What Are You Doing??", "Check in at exactly 4:00 AM", "💀", isHidden = true),
        Achievement("SEC_2", "Ultra Rare", "Open a Legendary Box", "🤯", isHidden = true),
        Achievement("SEC_3", "Aura God Ascension", "Reach Aura God tier", "🏆", isHidden = true)
    )

    fun checkUnlocks(userState: Any) {
        // Evaluate user state against achievements logic and trigger unlocks
    }
}
