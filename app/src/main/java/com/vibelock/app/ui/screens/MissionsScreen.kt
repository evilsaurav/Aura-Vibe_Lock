package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.engine.DailyMission
import com.vibelock.app.engine.MissionEngine
import com.vibelock.app.engine.UserState
import com.vibelock.app.ui.components.AuraGlassCard
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun MissionsScreen(
    userState: UserState,
    onBack: () -> Unit
) {
    val currentDate = MissionEngine.getCurrentDateString()
    val completedIds = if (userState.lastMissionDate == currentDate) {
        userState.completedMissionIds.split(",")
    } else {
        emptyList()
    }
    
    val currentMissions = remember(userState.lastMissionDate, userState.completedMissionIds, currentDate) {
        MissionEngine.generateDailyMissions(currentDate, completedIds)
    }

    val totalCompleted = currentMissions.count { it.isComplete }
    val allComplete = totalCompleted == 3

    var timeRemaining by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)
            val currentSecond = calendar.get(Calendar.SECOND)
            
            val hoursLeft = 23 - currentHour
            val minutesLeft = 59 - currentMinute
            val secondsLeft = 59 - currentSecond
            
            timeRemaining = String.format("%02dh %02dm %02ds", hoursLeft, minutesLeft, secondsLeft)
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DAILY MISSIONS",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            Text(timeRemaining, color = Color.Gray, fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Progress Section
        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("PROGRESS", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("$totalCompleted/3", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { totalCompleted / 3f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF10B981),
                    trackColor = Color.DarkGray
                )
                
                if (allComplete) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ALL 3 COMPLETE! +150 BONUS XP EARNED", color = Color(0xFFF59E0B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Missions List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(currentMissions) { mission ->
                MissionCardFull(mission)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Text("Back to Dashboard", color = Color.White)
        }
    }
}

@Composable
fun MissionCardFull(mission: DailyMission) {
    var flipped by remember { mutableStateOf(false) }
    LaunchedEffect(mission.isComplete) {
        if (mission.isComplete) {
            flipped = true
        }
    }
    
    val rotationY by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (flipped) 360f else 0f,
        animationSpec = androidx.compose.animation.core.tween(800, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "flip"
    )

    val bgColor = if (mission.isComplete) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFF121212)
    val borderColor = if (mission.isComplete) Color(0xFF10B981).copy(alpha = 0.5f) else Color.Transparent

    AuraGlassCard(modifier = Modifier.fillMaxWidth().graphicsLayer { this.rotationY = rotationY }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Checkmark circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (mission.isComplete) Color(0xFF10B981) else Color.DarkGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (mission.isComplete) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(mission.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(mission.description, color = Color.LightGray, fontSize = 12.sp)
                }
            }
            
            // XP Badge
            Box(
                modifier = Modifier
                    .background(Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("+${mission.xpReward} XP", color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
