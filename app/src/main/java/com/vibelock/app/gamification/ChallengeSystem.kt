package com.vibelock.app.gamification

data class CommunityChallenge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val currentProgress: Int,
    val targetGoal: Int,
    val daysRemaining: Int,
    val userContribution: Int,
    val rewardText: String
)

object ChallengeSystem {
    
    val currentChallenge = CommunityChallenge(
        id = "c_001",
        title = "GRIND WEEK",
        description = "Select Grind vibe every day for 7 days. The whole community must hit 10,000 total checks.",
        emoji = "⚡",
        currentProgress = 6700,
        targetGoal = 10000,
        daysRemaining = 4,
        userContribution = 5,
        rewardText = "If we hit goal: everyone gets 2x XP for 24 hours!"
    )
}
