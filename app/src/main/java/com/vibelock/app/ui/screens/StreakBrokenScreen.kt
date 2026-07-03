package com.vibelock.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.components.AuraGlassCard
import kotlinx.coroutines.delay

@Composable
fun StreakBrokenScreen(
    lostStreak: Int,
    userXp: Int,
    onRevive: (cost: Int) -> Unit,
    onStartFresh: () -> Unit
) {
    val reviveCost = Math.min(lostStreak * 3, 300)
    val canRevive = userXp >= reviveCost

    // Animations
    val crackProgress = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        delay(500)
        crackProgress.animateTo(1f, animationSpec = tween(1500, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0505)), // Reddish dark ambient
        contentAlignment = Alignment.Center
    ) {
        // Crack Animation Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width * 0.2f, 0f)
                lineTo(size.width * 0.4f, size.height * 0.3f)
                lineTo(size.width * 0.3f, size.height * 0.5f)
                lineTo(size.width * 0.6f, size.height * 0.8f)
                lineTo(size.width * 0.5f, size.height)
            }
            
            drawPath(
                path = path,
                color = Color(0xFFFF3B30).copy(alpha = 0.5f * crackProgress.value),
                style = Stroke(width = 8f)
            )
        }

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Dying Orb Placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.DarkGray, RoundedCornerShape(60.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("💀", fontSize = 50.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "💔 $lostStreak-day streak",
                color = Color(0xFFFF3B30),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
            Text("gone.", color = Color.Gray, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Your streak multipliers are gone.\nYou lost your leaderboard rank.",
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            if (canRevive) {
                AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("BUT WAIT. There's a way back.", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { onRevive(reviveCost) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("⚡ Revive Streak — Spend $reviveCost XP", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("1 revival per 30 days.", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            } else {
                Text(
                    text = "You need $reviveCost XP to revive it. You only have $userXp.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(onClick = onStartFresh) {
                Text("Start Fresh — Begin Day 1", color = Color.Gray)
            }
        }
    }
}
