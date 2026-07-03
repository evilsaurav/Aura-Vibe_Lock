package com.vibelock.app.gamification

data class SeasonLevel(
    val level: Int,
    val xpRequired: Int,
    val freeReward: String,
    val premiumReward: String,
    val isGrandPrize: Boolean = false
)

object SeasonPass {
    val seasonName = "Season 1: Dark Energy"
    val daysRemaining = 12

    val levels = (1..30).map { level ->
        SeasonLevel(
            level = level,
            xpRequired = level * 500, // example scaling
            freeReward = when {
                level % 5 == 0 -> "Silver Box"
                level == 30 -> "Grand Title"
                else -> "${level * 10} XP"
            },
            premiumReward = when {
                level % 5 == 0 -> "Gold Box"
                level == 30 -> "Legendary Orb Skin"
                else -> "Profile Border"
            },
            isGrandPrize = level == 30
        )
    }

    fun getCurrentLevel(totalSeasonXp: Int): Int {
        var current = 0
        for (lvl in levels) {
            if (totalSeasonXp >= lvl.xpRequired) {
                current = lvl.level
            } else {
                break
            }
        }
        return current
    }
}
