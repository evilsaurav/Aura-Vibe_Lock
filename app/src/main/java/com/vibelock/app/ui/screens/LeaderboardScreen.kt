package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.data.FriendsRepository
import com.vibelock.app.data.UserProfile
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.engine.UserState
import com.vibelock.app.ui.components.AuraGlassCard

@Composable
fun LeaderboardScreen(
    userState: UserState,
    friendsRepository: FriendsRepository,
    globalCheckIns: List<com.vibelock.app.data.GlobalCheckIn> = emptyList()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Friends Rank", "Global Top 100")
    
    val friendsList by friendsRepository.observeFriendsList().collectAsState(initial = emptyList())
    
    // Include user in friends list for ranking
    val userProfile = UserProfile(
        uid = "self",
        displayName = "You",
        auraCode = userState.auraCode,
        level = userState.level,
        xp = userState.xp,
        currentStreak = userState.currentStreak,
        highestStreak = userState.highestStreak,
        lastCheckInTimestamp = userState.lastCheckInTimestamp
    )
    
    val rankedList = (friendsList + userProfile).sortedByDescending { it.weeklyXP }
    val userRank = rankedList.indexOfFirst { it.uid == "self" } + 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(16.dp)
    ) {
        Text(
            text = "LEADERBOARD",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            tabs.forEachIndexed { index, title ->
                TextButton(
                    onClick = { selectedTab = index },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (selectedTab == index) Color(0xFF8B5CF6) else Color(0xFF1E1E1E)
                    )
                ) {
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (selectedTab == 0) {
            // Podium
            if (rankedList.isNotEmpty()) {
                PodiumSection(rankedList.take(3))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // List (Rank 4+)
            if (rankedList.size > 3) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    itemsIndexed(rankedList.drop(3)) { index, profile ->
                        RankRow(rank = index + 4, profile = profile, isSelf = profile.uid == "self")
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            
            // Sticky Footer for User
            AuraGlassCard(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Your rank: #$userRank", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (userRank > 1) {
                            val nextUp = rankedList[userRank - 2]
                            val diff = nextUp.weeklyXP - userState.xp
                            Text("Need $diff XP to beat ${nextUp.displayName}", color = Color.LightGray, fontSize = 12.sp)
                        } else {
                            Text("You are the Aura King 👑", color = Color(0xFFF59E0B), fontSize = 12.sp)
                        }
                    }
                    Text(userState.xp.toString(), color = Color(0xFF8B5CF6), fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
            }
        } else {
            // Global Map Screen
            GlobalScreen(globalCheckIns = globalCheckIns)
        }
    }
}

@Composable
fun PodiumSection(topThree: List<UserProfile>) {
    Row(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place
        if (topThree.size >= 2) {
            PodiumPedestal(profile = topThree[1], rank = 2, height = 120.dp, color = Color(0xFF9E9E9E))
        }
        
        // 1st Place
        if (topThree.isNotEmpty()) {
            PodiumPedestal(profile = topThree[0], rank = 1, height = 160.dp, color = Color(0xFFF59E0B), showCrown = true)
        }
        
        // 3rd Place
        if (topThree.size >= 3) {
            PodiumPedestal(profile = topThree[2], rank = 3, height = 90.dp, color = Color(0xFFCD7F32))
        }
    }
}

@Composable
fun PodiumPedestal(profile: UserProfile, rank: Int, height: androidx.compose.ui.unit.Dp, color: Color, showCrown: Boolean = false) {
    val tier = AuraTier.getTierForLevel(profile.level)
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (showCrown) {
            Text("👑", fontSize = 24.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(tier.colorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(profile.displayName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Text(profile.displayName, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
        Text("${profile.weeklyXP} XP", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        
        Box(
            modifier = Modifier.width(70.dp).height(height).background(color.copy(alpha = 0.2f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Text("#$rank", color = color, fontWeight = FontWeight.Black, fontSize = 32.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun RankRow(rank: Int, profile: UserProfile, isSelf: Boolean) {
    val tier = AuraTier.getTierForLevel(profile.level)
    val bgColor = if (isSelf) Color(0xFF8B5CF6).copy(alpha = 0.2f) else Color(0xFF1E1E1E)
    
    Row(
        modifier = Modifier.fillMaxWidth().background(bgColor, RoundedCornerShape(8.dp)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#$rank", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
        
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(tier.colorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(profile.displayName.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(profile.displayName, color = Color.White, fontWeight = FontWeight.Bold)
            Text(tier.name.replace("_", " "), color = Color(tier.colorHex), fontSize = 10.sp)
        }
        
        Column(horizontalAlignment = Alignment.End) {
            Text(profile.weeklyXP.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text("🔥 ${profile.currentStreak}", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
