package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.AuraOrb
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.components.OrbState
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography
import kotlinx.coroutines.delay

@Composable
fun CheckInScreen(
    tier: AuraTier,
    onCheckInComplete: () -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Hold to Lock, 2: Celebration
    var isHolding by remember { mutableStateOf(false) }
    var holdProgress by remember { mutableStateOf(0f) }

    // Simulating hold detection manually or using simple pointerInput
    LaunchedEffect(isHolding) {
        if (isHolding) {
            while (holdProgress < 1f) {
                delay(10)
                holdProgress += 0.01f
            }
            if (holdProgress >= 1f) {
                step = 2
                onCheckInComplete()
            }
        } else {
            holdProgress = 0f
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundElevated)) {
        if (step == 1) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Ready to lock your aura?", style = AuraTypography.HeadingXL, color = AuraColors.TextPrimary)
                Spacer(modifier = Modifier.height(48.dp))
                
                AuraOrb(tier = tier, orbState = OrbState.CHARGED, onClick = {})
                
                Spacer(modifier = Modifier.height(64.dp))
                
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(if (holdProgress > 0) AuraColors.NeonPurple.copy(alpha = holdProgress) else AuraColors.BackgroundSurface)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isHolding = true
                                    tryAwaitRelease()
                                    isHolding = false
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("LOCK\nVIBE", style = AuraTypography.DisplayM, color = AuraColors.TextPrimary)
                }
                
                if (holdProgress > 0f && holdProgress < 1f) {
                    Text("Holding...", color = AuraColors.NeonCyan, modifier = Modifier.padding(top = 16.dp))
                }
            }
        } else {
            // Step 2: Celebration
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AuraOrb(tier = tier, orbState = OrbState.LEVEL_UP, onClick = {})
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("+150 XP ⚡", style = AuraTypography.DisplayL, color = AuraColors.NeonGold)
                Spacer(modifier = Modifier.height(16.dp))
                
                AuraGlassCard {
                    Column {
                        Text("Base: +100 XP", color = AuraColors.TextPrimary)
                        Text("Grind Vibe: +20 XP (1.2x)", color = AuraColors.TextPrimary)
                        Text("Early Bird: +30 XP (1.3x)", color = AuraColors.TextPrimary)
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                NeonButton("Share Aura Story", onClick = {})
            }
        }
    }
}
