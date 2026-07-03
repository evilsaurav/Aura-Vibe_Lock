package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun LegalScreen(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF1E1E1E))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title.uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val policyText = when(title) {
            "Privacy Policy" -> """
                **AURA Privacy Policy**
                
                At AURA, we respect your vibe and your data. Here is what we collect and how we use it:
                
                1. **Location Data**: We collect approximate location for the 4D Matrix Globe. To protect your privacy, we apply a 5km radius "jitter" to your coordinates. Your exact GPS location is never stored on our servers. Global check-ins are retained for only 24 hours.
                
                2. **Journal & AI Processing**: Your Vibe Journal entries are processed using Google's Gemini AI to generate insights and summaries. Sensitive emotional data is handled securely, and if a crisis is detected, AURA drops its persona to provide genuine support.
                
                3. **Account Deletion**: You own your data. You can request full account deletion from the app, which instantly removes your profile, journal entries, and social links from our Firebase servers.
                
                4. **Third-Party Services**: We use Firebase for authentication, database, and crash reporting. All data is transmitted over secure HTTPS connections.
            """.trimIndent()
            
            "Terms of Service" -> """
                **AURA Terms of Service**
                
                By using AURA, you agree to the following terms:
                
                1. **Age Requirement**: You must be at least 13 years old to use AURA.
                
                2. **Acceptable Use**: AURA is a space for tracking your daily grind and vibe. Do not use the app to harass others, spam the global check-in globe, or exploit the XP/Leveling system.
                
                3. **Content**: You are responsible for the journals and custom avatars you upload. We reserve the right to ban accounts that violate these terms.
                
                4. **AI Disclaimer**: The AURA AI is a gamified companion. It is not a substitute for professional mental health services. If you are experiencing a crisis, please seek professional help.
            """.trimIndent()
            
            "Help Center" -> """
                **AURA Help Center**
                
                **How do I level up?**
                Lock in your vibe daily. Complete your daily missions (like viewing the 4D globe or sharing a streak) to earn XP.
                
                **What is Roast Mode?**
                In your Profile settings, you can toggle AI Personality. 'Hype Mode' will encourage you, while 'Roast Mode' will give you brutally honest, sarcastic motivation.
                
                **Why is my location wrong on the Globe?**
                For your privacy, we intentionally scramble your location by up to 5km. It shows your city vibe, not your exact street address!
                
                **Contact Us**: support@vibelock.com
            """.trimIndent()
            else -> "Information not available."
        }

        Text(
            text = policyText,
            color = Color.LightGray,
            style = AuraTypography.BodyM,
            lineHeight = 24.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
