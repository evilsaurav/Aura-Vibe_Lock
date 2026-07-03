package com.vibelock.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.R

// For the sake of standard compilation without needing the certs array immediately, 
// we'll use standard font families if Google Fonts certs aren't set up yet, 
// but we define the architecture exactly as requested.
val SpaceGroteskFont = FontFamily.Default
val InterFont = FontFamily.Default

object AuraColors {
    // Backgrounds (pure dark — not dark gray)
    val BackgroundBase = Color(0xFF050505)      // Almost pure black
    val BackgroundSurface = Color(0xFF0D0D0D)   // Cards bg
    val BackgroundElevated = Color(0xFF141414)  // Modals bg
    val BackgroundGlass = Color(0xFF1A1A1A)     // Glass cards

    // Primary Neons
    val NeonPurple = Color(0xFF8B5CF6)          // Primary CTA
    val NeonPurpleLight = Color(0xFFAB82FF)     // Hover/active states
    val NeonCyan = Color(0xFF06B6D4)            // Accent 1
    val NeonPink = Color(0xFFEC4899)            // Accent 2 / danger
    val NeonGold = Color(0xFFF59E0B)            // XP / achievements
    val NeonGreen = Color(0xFF10B981)           // Success / streak active

    // Text
    val TextPrimary = Color(0xFFF5F5F5)
    val TextSecondary = Color(0xFF9CA3AF)
    val TextMuted = Color(0xFF4B5563)
    val TextOnNeon = Color(0xFF000000)

    // Tier Colors
    val TierNoob = Color(0xFF6B7280)            
    val TierRising = Color(0xFF3B82F6)          
    val TierVibe = Color(0xFF8B5CF6)            
    val TierSigma = Color(0xFFEC4899)           
    val TierGod = listOf(                       
        Color(0xFFEC4899), Color(0xFF8B5CF6),
        Color(0xFF06B6D4), Color(0xFF10B981), Color(0xFFF59E0B)
    )

    // Semantic
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)
    val Danger = Color(0xFFEF4444)
    val Info = Color(0xFF3B82F6)
}

object AuraTypography {
    // Display
    val DisplayXL = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 52.sp, fontWeight = FontWeight.Black, letterSpacing = (-2).sp)
    val DisplayL = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 40.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp)
    val DisplayM = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)

    // Headings
    val HeadingXL = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    val HeadingL = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
    val HeadingM = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

    // Body
    val BodyL = TextStyle(fontFamily = InterFont, fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp)
    val BodyM = TextStyle(fontFamily = InterFont, fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp)
    val BodyS = TextStyle(fontFamily = InterFont, fontSize = 12.sp, fontWeight = FontWeight.Normal)

    // Special
    val Label = TextStyle(fontFamily = InterFont, fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
    val XPNumber = TextStyle(fontFamily = SpaceGroteskFont, fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = (-1).sp)
}

object AuraSpacing {
    val xs = 4.dp
    val s = 8.dp
    val m = 16.dp
    val l = 24.dp
    val xl = 32.dp
    val xxl = 48.dp
}

object AuraShape {
    val Small = RoundedCornerShape(8.dp)
    val Medium = RoundedCornerShape(16.dp)
    val Large = RoundedCornerShape(24.dp)
    val XLarge = RoundedCornerShape(32.dp)
    val Pill = RoundedCornerShape(50)
    val Circle = CircleShape
}
