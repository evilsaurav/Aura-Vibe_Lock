package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.gamification.SeasonPass
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun SeasonPassScreen(userSeasonXp: Int) {
    val currentLevel = SeasonPass.getCurrentLevel(userSeasonXp)

    Column(
        modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase).padding(24.dp)
    ) {
        // Header
        Text(SeasonPass.seasonName, style = AuraTypography.HeadingXL, color = AuraColors.NeonPurple)
        Text("${SeasonPass.daysRemaining} Days Remaining", style = AuraTypography.HeadingM, color = AuraColors.NeonPink)
        
        Spacer(modifier = Modifier.height(32.dp))

        // Horizontal Track
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(SeasonPass.levels) { level ->
                val isUnlocked = level.level <= currentLevel
                val isCurrent = level.level == currentLevel + 1
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    // Level Node
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (isUnlocked) AuraColors.NeonGreen else AuraColors.BackgroundSurface)
                            .border(2.dp, if (isCurrent) AuraColors.NeonPurple else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isUnlocked) "✓" else level.level.toString(),
                            color = if (isUnlocked) Color.Black else AuraColors.TextSecondary,
                            style = AuraTypography.HeadingM
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Free Reward
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .width(80.dp)
                            .background(AuraColors.BackgroundGlass)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(level.freeReward, fontSize = 10.sp, color = AuraColors.TextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Premium Reward
                    Box(
                        modifier = Modifier
                            .height(60.dp)
                            .width(80.dp)
                            .background(AuraColors.BackgroundElevated)
                            .border(1.dp, AuraColors.NeonGold)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(level.premiumReward, fontSize = 10.sp, color = AuraColors.NeonGold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        NeonButton(
            text = "Unlock Premium Rewards →",
            onClick = {},
            color = AuraColors.NeonGold,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
