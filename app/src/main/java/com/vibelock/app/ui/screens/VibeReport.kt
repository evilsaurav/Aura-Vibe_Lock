package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun VibeReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraColors.BackgroundBase)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Weekly Aura Report", color = AuraColors.TextSecondary, letterSpacing = 2.sp)
        Text("June 16 - June 23", style = AuraTypography.HeadingXL, color = AuraColors.TextPrimary)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Huge Emoji Header
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("🌀", fontSize = 120.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("DOMINANT VIBE:", color = AuraColors.TextSecondary, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text("CHAOS MODE", style = AuraTypography.DisplayM, color = AuraColors.NeonCyan, modifier = Modifier.align(Alignment.CenterHorizontally), letterSpacing = 3.sp)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // AI Generated Text (Mocked)
        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Your energy this week? Chaotic unhinged girlboss. Checking in at 1 AM, 2 AM — the darkness fuels you. You missed one day but we're going to ignore that because your comeback was legendary. Aura: Goblin Mode ✅",
                    style = AuraTypography.BodyL,
                    color = AuraColors.TextPrimary,
                    lineHeight = 28.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Your Archetype:", color = AuraColors.TextSecondary)
        Text("Chaotic Neutral King", style = AuraTypography.HeadingL, color = AuraColors.NeonPurple)
        
        Spacer(modifier = Modifier.height(48.dp))
        
        NeonButton(
            text = "Share Your Report →",
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}
