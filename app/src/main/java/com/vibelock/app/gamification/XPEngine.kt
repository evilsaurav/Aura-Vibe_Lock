package com.vibelock.app.gamification

enum class VibeType {
    GRIND, CHILL, CHAOS, COZY, DARK, SLAY
}

data class XPCalculation(
    val base: Int = 100,
    val multipliers: List<XPMultiplier>,
    val bonuses: List<XPBonus>,
    val total: Int
)

data class XPMultiplier(val label: String, val value: Float, val emoji: String)
data class XPBonus(val label: String, val amount: Int, val emoji: String)

object XPEngine {
    fun calculateXP(
        streak: Int,
        vibeSelected: VibeType,
        checkInHour: Int,  // 0-23
        missionsComplete: Int,
        isWeekend: Boolean
    ): XPCalculation {
        val multipliers = mutableListOf<XPMultiplier>()
        val bonuses = mutableListOf<XPBonus>()
        
        // Streak multipliers (loss aversion)
        if (streak >= 7) multipliers.add(XPMultiplier("7-day streak", 1.5f, "🔥"))
        if (streak >= 30) multipliers.add(XPMultiplier("30-day streak", 2.0f, "💪"))
        if (streak >= 100) multipliers.add(XPMultiplier("100-day streak", 3.0f, "👑"))
        if (streak >= 365) multipliers.add(XPMultiplier("365-day legend", 5.0f, "⚡"))
        
        // Time bonuses
        if (checkInHour < 8) multipliers.add(XPMultiplier("Early Bird", 1.3f, "🌅"))
        if (checkInHour >= 23) multipliers.add(XPMultiplier("Night Owl", 1.2f, "🦉"))
        
        // Vibe bonuses
        if (vibeSelected == VibeType.GRIND) multipliers.add(XPMultiplier("Grind Mode", 1.25f, "⚡"))
        if (vibeSelected == VibeType.CHAOS) multipliers.add(XPMultiplier("Chaos Energy", 1.1f, "🌀"))
        
        // Mission completion bonus
        if (missionsComplete == 3) bonuses.add(XPBonus("All missions done", 150, "✅"))
        else if (missionsComplete == 2) bonuses.add(XPBonus("2 missions done", 50, "🎯"))
        
        // Weekend warrior
        if (isWeekend) bonuses.add(XPBonus("Weekend Warrior", 25, "🏆"))
        
        val multiplierValue = multipliers.fold(1f) { acc, m -> acc * m.value }
        val total = (100 * multiplierValue).toInt() + bonuses.sumOf { it.amount }
        
        return XPCalculation(multipliers = multipliers, bonuses = bonuses, total = total)
    }
}
