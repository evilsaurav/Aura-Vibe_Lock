package com.vibelock.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.data.Squad
import com.vibelock.app.data.SquadVibe
import com.vibelock.app.data.SquadRepository
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.components.AuraGlassCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.collect

@Composable
fun SquadDetailScreen(
    squadId: String,
    squadRepository: SquadRepository,
    onBack: () -> Unit
) {
    var squad by remember { mutableStateOf<Squad?>(null) }
    var dailyVibes by remember { mutableStateOf<Map<String, SquadVibe>>(emptyMap()) }
    
    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    LaunchedEffect(squadId) {
        // Find squad from list (since we don't have a getSquadById yet, we fetch all and filter for now)
        squadRepository.getUserSquads().collect { list ->
            squad = list.find { it.squadId == squadId }
        }
    }

    LaunchedEffect(squadId) {
        squadRepository.getSquadDailyVibes(squadId, dateString).collect { vibes ->
            dailyVibes = vibes
        }
    }

    if (squad == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050505)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AuraColors.NeonCyan)
        }
        return
    }

    // Compute majority vibe
    val vibeCounts = dailyVibes.values.groupingBy { it.vibe }.eachCount()
    val dominantVibe = vibeCounts.maxByOrNull { it.value }?.key ?: "Pending"
    val maxCount = vibeCounts.maxByOrNull { it.value }?.value ?: 0
    val totalMembers = squad!!.members.size
    val isSyncAchieved = maxCount == totalMembers && totalMembers > 1

    val bgColor = when (dominantVibe.lowercase()) {
        "grind", "focus" -> AuraColors.NeonCyan.copy(alpha = 0.2f)
        "chill", "peace" -> AuraColors.NeonPurple.copy(alpha = 0.2f)
        "chaos", "sad" -> Color.Red.copy(alpha = 0.2f)
        else -> Color.DarkGray.copy(alpha = 0.2f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgColor, Color(0xFF050505))))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text("< Back", color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(squad!!.name.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp)) // balance back button
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Top Section: Squad Energy
        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Squad Energy:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                if (dominantVibe != "Pending") {
                    Text("$dominantVibe DOMINANT", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("$maxCount/$totalMembers members feel this way", color = AuraColors.NeonCyan, fontSize = 14.sp)
                } else {
                    Text("WAITING FOR CHECK-INS", color = Color.Gray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                if (isSyncAchieved) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(AuraColors.NeonPurple.copy(alpha=0.3f), RoundedCornerShape(8.dp)).padding(12.dp)) {
                        Text("SQUAD SYNC ACHIEVED 🔥\nThe whole gang is locked in today. Rare vibes.", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Middle Section: Member Cards
        Text("MEMBERS", color = AuraColors.NeonPurple, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(squad!!.members.indices.toList()) { index ->
                val uid = squad!!.members[index]
                val auraCode = squad!!.memberAuraCodes.getOrNull(index) ?: "Unknown"
                val vibeData = dailyVibes[uid]

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF151515))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Placeholder avatar
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                        Text(auraCode.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(auraCode, color = Color.White, fontWeight = FontWeight.Bold)
                        if (vibeData != null) {
                            Text("Checked in", color = Color.Gray, fontSize = 12.sp)
                        } else {
                            Text("Waiting...", color = Color.DarkGray, fontSize = 12.sp)
                        }
                    }
                    if (vibeData != null) {
                        Text(vibeData.vibe, color = AuraColors.NeonCyan, fontWeight = FontWeight.Bold)
                    } else {
                        Text("—", color = Color.DarkGray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Section: Stats (Mocked for Phase 1)
        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SQUAD STATS", color = AuraColors.NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Squad Streak", color = Color.LightGray)
                    Text("12 days", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Most Consistent", color = Color.LightGray)
                    Text("Arjun 🏆", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
