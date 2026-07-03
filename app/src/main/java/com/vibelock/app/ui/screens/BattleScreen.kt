package com.vibelock.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.social.AuraBattle
import com.vibelock.app.social.BattleSystem
import com.vibelock.app.ui.components.NeonText
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun BattleScreen() {
    val battle = BattleSystem.currentBattle
    val daysRemaining = BattleSystem.getDaysRemaining(battle)
    val dayOfBattle = 7 - daysRemaining

    val transition = rememberInfiniteTransition()
    val pulse by transition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("AURA BATTLE", style = AuraTypography.HeadingXL, color = AuraColors.NeonPink, letterSpacing = 2.sp)
        Text("Day $dayOfBattle of 7", color = AuraColors.TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))

        // Who is leading
        val xpDiff = battle.player1XpGained - battle.player2XpGained
        val leadText = if (xpDiff > 0) "You're leading by $xpDiff XP 🔥" 
                       else if (xpDiff < 0) "You're behind by ${-xpDiff} XP ⚠️" 
                       else "It's a tie! ⚔️"
        
        Text(leadText, style = AuraTypography.HeadingM, color = if (xpDiff >= 0) AuraColors.NeonGreen else AuraColors.Danger)

        Spacer(modifier = Modifier.height(64.dp))

        // VS Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player 1
            BattlePlayerColumn(
                name = battle.player1Name,
                xp = battle.player1XpGained,
                color = AuraColors.NeonCyan,
                isStreakActive = battle.player1StreakActive
            )

            // VS Graphic
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(AuraColors.NeonPink.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                NeonText("VS", color = AuraColors.NeonPink, style = AuraTypography.HeadingL)
            }

            // Player 2
            BattlePlayerColumn(
                name = battle.player2Name,
                xp = battle.player2XpGained,
                color = AuraColors.NeonPurple,
                isStreakActive = battle.player2StreakActive
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        
        // Timeline Bar
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp)) {
            Text("Battle Progress", color = AuraColors.TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape).background(Color.DarkGray)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(dayOfBattle / 7f)
                        .height(12.dp)
                        .clip(CircleShape)
                        .background(AuraColors.NeonPink)
                )
            }
        }
    }
}

@Composable
fun BattlePlayerColumn(name: String, xp: Int, color: Color, isStreakActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
            // Circular arc progress
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = color.copy(alpha = 0.2f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = (xp / 10000f) * 360f, // Fake target 10k
                    useCenter = false,
                    style = Stroke(width = 12f, cap = StrokeCap.Round)
                )
            }
            // Inner Avatar
            Box(modifier = Modifier.size(76.dp).clip(CircleShape).background(Color.Gray))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(name, style = AuraTypography.HeadingM, color = AuraColors.TextPrimary)
        Text("$xp XP", style = AuraTypography.HeadingL, color = color, fontWeight = FontWeight.Black)
        
        Spacer(modifier = Modifier.height(8.dp))
        if (isStreakActive) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AuraColors.NeonGreen))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Vibing", color = AuraColors.NeonGreen, fontSize = 12.sp)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Gray))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Sleeping", color = AuraColors.TextSecondary, fontSize = 12.sp)
            }
        }
    }
}
