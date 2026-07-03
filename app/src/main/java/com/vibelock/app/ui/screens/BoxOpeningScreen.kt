package com.vibelock.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.gamification.AuraBoxReward
import com.vibelock.app.gamification.AuraBoxSystem
import com.vibelock.app.gamification.BoxRarity
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.components.ParticleCannon
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BoxOpeningScreen(
    rarity: BoxRarity,
    onClose: (AuraBoxReward?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var stage by remember { mutableStateOf(0) } // 0: Floating, 1: Shaking, 2: Exploded, 3: Reward Reveal
    
    val shakeOffset = remember { Animatable(0f) }
    val boxScale = remember { Animatable(1f) }
    var particleTriggerTime by remember { mutableStateOf(0L) }
    
    var reward by remember { mutableStateOf<AuraBoxReward?>(null) }

    val boxColor = when(rarity) {
        BoxRarity.BRONZE -> Color(0xFFCD7F32)
        BoxRarity.SILVER -> Color(0xFFC0C0C0)
        BoxRarity.GOLD -> AuraColors.NeonGold
        BoxRarity.LEGENDARY -> AuraColors.NeonPurple
    }

    LaunchedEffect(stage) {
        if (stage == 1) {
            // Shaking phase
            for (i in 0..10) {
                shakeOffset.animateTo(
                    targetValue = if (i % 2 == 0) 15f else -15f,
                    animationSpec = tween(50)
                )
            }
            shakeOffset.animateTo(0f, tween(50))
            
            // Explosion
            stage = 2
            boxScale.animateTo(0f, tween(200))
            particleTriggerTime = System.currentTimeMillis()
            reward = AuraBoxSystem.openBox(rarity)
            
            delay(500)
            stage = 3
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase)) {
        
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (stage < 2) {
                // The Box
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(boxScale.value)
                        .offset { IntOffset(shakeOffset.value.toInt(), 0) }
                        .background(boxColor, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                        .clickable(enabled = stage == 0) { stage = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("📦", fontSize = 100.sp)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (stage == 0) {
                    Text(
                        "TAP TO OPEN",
                        style = AuraTypography.HeadingXL,
                        color = boxColor
                    )
                }
            }
            
            if (stage == 3 && reward != null) {
                // Reward Reveal
                Text(
                    text = if (reward!!.isRare) "LEGENDARY!" else "RARE FIND",
                    style = AuraTypography.HeadingM,
                    color = boxColor,
                    letterSpacing = 4.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = reward!!.title,
                    style = AuraTypography.DisplayL,
                    color = AuraColors.TextPrimary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                AuraGlassCard {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("+${reward!!.xpBoost} XP", fontSize = 48.sp, fontWeight = FontWeight.Black, color = AuraColors.NeonGold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("+${reward!!.streakShields} Streak Shields", fontSize = 24.sp, color = AuraColors.NeonCyan)
                        if (reward!!.profileBorder != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Border: ${reward!!.profileBorder}", fontSize = 18.sp, color = AuraColors.NeonPurple)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                NeonButton("Collect", onClick = { onClose(reward) }, color = boxColor)
            }
        }

        // Particle System
        ParticleCannon(
            triggerTime = particleTriggerTime,
            originX = 540f, // roughly center screen assuming 1080 width
            originY = 960f, // roughly center assuming 1920 height
            particleColor = boxColor
        )
    }
}
