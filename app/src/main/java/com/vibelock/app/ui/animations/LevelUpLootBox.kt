package com.vibelock.app.ui.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.haptics.VibeHapticEngine
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun LevelUpLootBox(
    newLevel: Int,
    hapticEngine: VibeHapticEngine,
    onDismiss: () -> Unit
) {
    var stage by remember { mutableStateOf(0) }
    // 0 = Initial Shake
    // 1 = Flash Bang (White Out)
    // 2 = Reveal New Level

    val shakeOffset by animateFloatAsState(
        targetValue = if (stage == 0) 20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake"
    )

    val scale by animateFloatAsState(
        targetValue = when (stage) {
            0 -> 1f
            1 -> 50f // Massive scale out to simulate flash
            2 -> 1f
            else -> 1f
        },
        animationSpec = tween(if (stage == 1) 300 else 800, easing = FastOutSlowInEasing),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        // Stage 0: Shake and charge up
        for (i in 1..20) {
            hapticEngine.triggerAILoading() // Rapid light ticks
            delay(50)
        }
        
        // Stage 1: FLASH BANG
        hapticEngine.triggerLevelUp() // Heavy burst
        stage = 1
        delay(300)
        
        // Stage 2: Reveal
        stage = 2
    }

    // Full screen overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (stage == 1) Color.White else Color(0xEE000000))
            .clickable(enabled = stage == 2) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        if (stage == 0) {
            // Charging Orb
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer {
                        translationX = shakeOffset * (if (Random.nextBoolean()) 1 else -1)
                        translationY = shakeOffset * (if (Random.nextBoolean()) 1 else -1)
                        scaleX = 1f + (shakeOffset / 100f)
                        scaleY = 1f + (shakeOffset / 100f)
                    }
                    .clip(CircleShape)
                    .background(AuraColors.NeonGold)
                    .border(4.dp, Color.White, CircleShape)
            )
            Text(
                "ENERGY CRITICAL...",
                color = AuraColors.NeonGold,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        if (stage == 1) {
            // Pure white flash handled by Box background + massive scaling box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(Color.White, CircleShape)
            )
        }

        if (stage == 2) {
            // Rank Reveal
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(tween(600, easing = OvershootInterpolator(2f))) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF111111))
                        .border(2.dp, AuraColors.NeonGold, RoundedCornerShape(32.dp))
                        .padding(48.dp)
                ) {
                    Text("AURA EVOLVED", color = AuraColors.NeonGold, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "LEVEL $newLevel",
                        style = AuraTypography.DisplayL,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your matrix just expanded.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Tap anywhere to continue",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Particle explosion overlay
            VibeParticleSystem(vibeConfig = VibeParticleConfig.GRIND)
        }
    }
}

// Simple overshoot interpolator for Compose
fun OvershootInterpolator(tension: Float = 2.0f): Easing = Easing { t ->
    val t1 = t - 1.0f
    t1 * t1 * ((tension + 1) * t1 + tension) + 1.0f
}
