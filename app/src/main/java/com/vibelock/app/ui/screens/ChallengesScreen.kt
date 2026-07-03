package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.gamification.ChallengeSystem
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun ChallengesScreen() {
    val challenge = ChallengeSystem.currentChallenge

    Column(modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase).padding(24.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("COMMUNITY", style = AuraTypography.HeadingXL, color = AuraColors.TextPrimary, letterSpacing = 2.sp)
        Text("Global Co-op Events", color = AuraColors.TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(challenge.emoji, fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(challenge.title, style = AuraTypography.DisplayM, color = AuraColors.NeonGold)
                Text("Ends in ${challenge.daysRemaining} days", color = AuraColors.NeonPink, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress Bar
                val progressRatio = challenge.currentProgress.toFloat() / challenge.targetGoal.toFloat()
                val percent = (progressRatio * 100).toInt()
                
                Text("$percent% complete — ${challenge.currentProgress}/${challenge.targetGoal} check-ins", color = AuraColors.TextPrimary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).clip(CircleShape).background(Color.DarkGray)) {
                    Box(modifier = Modifier.fillMaxWidth(progressRatio).height(16.dp).clip(CircleShape).background(AuraColors.NeonGold))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("You've added ${challenge.userContribution} check-ins to the goal", color = AuraColors.NeonCyan, fontSize = 14.sp)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(challenge.rewardText, color = AuraColors.NeonGreen, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                
                Spacer(modifier = Modifier.height(32.dp))
                NeonButton(text = "Participate Now", onClick = {}, modifier = Modifier.fillMaxWidth(), color = AuraColors.NeonGold)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("Past Events", style = AuraTypography.HeadingL, color = AuraColors.TextSecondary)
        Spacer(modifier = Modifier.height(16.dp))
        
        AuraGlassCard {
            Text("✅ Early Bird Season — Goal Hit! Earned 2x XP Bonus", color = AuraColors.TextPrimary, fontSize = 14.sp)
        }
    }
}
