package com.vibelock.app.gamification

import androidx.compose.ui.graphics.Color
import com.vibelock.app.ui.theme.AuraColors

sealed class AuraTier(
    val name: String,
    val xpMin: Int,
    val xpMax: Int,
    val primaryColor: Color,
    val glowColor: Color,
    val iconEmoji: String,
    val description: String,
    val bgGradient: List<Color>
) {
    object NoobCivilian : AuraTier(
        name = "Noob Civilian",
        xpMin = 0, xpMax = 499,
        primaryColor = AuraColors.TierNoob,
        glowColor = Color(0xFF374151),
        iconEmoji = "👤",
        description = "Just started the grind",
        bgGradient = listOf(Color(0xFF1F2937), Color(0xFF111827))
    )
    object RisingVibe : AuraTier(
        name = "Rising Vibe",
        xpMin = 500, xpMax = 1999,
        primaryColor = AuraColors.TierRising,
        glowColor = Color(0xFF1D4ED8),
        iconEmoji = "🌊",
        description = "The vibe is building",
        bgGradient = listOf(Color(0xFF1E3A5F), Color(0xFF0F1E35))
    )
    object VibeLord : AuraTier(
        name = "Vibe Lord",
        xpMin = 2000, xpMax = 5999,
        primaryColor = AuraColors.TierVibe,
        glowColor = Color(0xFF6D28D9),
        iconEmoji = "👑",
        description = "The vibes hit different",
        bgGradient = listOf(Color(0xFF2D1B69), Color(0xFF1A0F40))
    )
    object SigmaAura : AuraTier(
        name = "Sigma Aura",
        xpMin = 6000, xpMax = 14999,
        primaryColor = AuraColors.TierSigma,
        glowColor = Color(0xFFBE185D),
        iconEmoji = "💎",
        description = "Sigma grindset activated",
        bgGradient = listOf(Color(0xFF4C0519), Color(0xFF2D0B0E))
    )
    object AuraGod : AuraTier(
        name = "Aura God",
        xpMin = 15000, xpMax = Int.MAX_VALUE,
        primaryColor = AuraColors.NeonGold,
        glowColor = AuraColors.NeonGold,
        iconEmoji = "⚡",
        description = "Transcended. You are the aura.",
        bgGradient = listOf(Color(0xFF451A03), Color(0xFF1C0701))
    )

    companion object {
        fun getTierForXP(xp: Int): AuraTier {
            return when {
                xp < 500 -> NoobCivilian
                xp < 2000 -> RisingVibe
                xp < 6000 -> VibeLord
                xp < 15000 -> SigmaAura
                else -> AuraGod
            }
        }
    }
}
