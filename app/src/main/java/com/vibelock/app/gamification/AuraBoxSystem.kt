package com.vibelock.app.gamification

import kotlin.random.Random

enum class BoxRarity { BRONZE, SILVER, GOLD, LEGENDARY }

data class AuraBoxReward(
    val title: String,
    val xpBoost: Int,
    val streakShields: Int,
    val profileBorder: String? = null,
    val isRare: Boolean = false
)

object AuraBoxSystem {
    fun openBox(rarity: BoxRarity): AuraBoxReward {
        val roll = Random.nextInt(100) // 0-99
        
        return when (rarity) {
            BoxRarity.BRONZE -> {
                val isRare = roll < 10
                AuraBoxReward(
                    title = if (isRare) "Rare Bronze Stash" else "Bronze Stash",
                    xpBoost = Random.nextInt(50, 201),
                    streakShields = 1,
                    isRare = isRare
                )
            }
            BoxRarity.SILVER -> {
                val isRare = roll < 15
                AuraBoxReward(
                    title = if (isRare) "Sigma Certified Stash" else "Silver Stash",
                    xpBoost = Random.nextInt(200, 501),
                    streakShields = 2,
                    profileBorder = "Silver Gradient",
                    isRare = isRare
                )
            }
            BoxRarity.GOLD -> {
                val isRare = roll < 20
                AuraBoxReward(
                    title = if (isRare) "Aura Royalty" else "Golden Stash",
                    xpBoost = Random.nextInt(500, 1501),
                    streakShields = 3,
                    profileBorder = "Gold Animated",
                    isRare = isRare
                )
            }
            BoxRarity.LEGENDARY -> {
                AuraBoxReward(
                    title = "Aura Legend",
                    xpBoost = Random.nextInt(1000, 3001),
                    streakShields = 5,
                    profileBorder = "Rainbow Animated",
                    isRare = true // Always rare
                )
            }
        }
    }
}
