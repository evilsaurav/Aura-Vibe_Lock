package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

data class FeedPost(
    val id: String,
    val username: String,
    val tierColor: Color,
    val timeAgo: String,
    val vibeEmoji: String,
    val vibeName: String,
    val isOnTime: Boolean,
    val lateText: String? = null,
    val streak: Int,
    val caption: String
)

@Composable
fun AuraFeedScreen() {
    // Mock Data
    val posts = listOf(
        FeedPost("1", "SigmaRuler", AuraColors.TierSigma, "10m ago", "⚡", "GRIND", true, null, 42, "W rizz day fr"),
        FeedPost("2", "VibeQueen", AuraColors.TierVibe, "25m ago", "☁️", "COZY", false, "Late: 4m 20s", 15, "sleeping through my alarms"),
        FeedPost("3", "AuraGod99", AuraColors.NeonGold, "1h ago", "🌀", "CHAOS", true, null, 142, "unhinged hours"),
    )

    Column(modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase).padding(16.dp)) {
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("AURA FEED", style = AuraTypography.HeadingXL, color = AuraColors.TextPrimary, letterSpacing = 2.sp)
        Text("Real vibes only. No curation.", color = AuraColors.TextSecondary)
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(posts.size) { index ->
                val post = posts[index]
                AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        // Header
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.size(40.dp).clip(CircleShape).border(2.dp, post.tierColor, CircleShape).background(Color.Gray))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(post.username, style = AuraTypography.HeadingM, color = AuraColors.TextPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("🔥 ${post.streak}", color = AuraColors.NeonGold, fontSize = 12.sp)
                                }
                                Text(post.timeAgo, color = AuraColors.TextSecondary, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // The Vibe
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(AuraColors.BackgroundElevated)
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(post.vibeEmoji, fontSize = 64.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(post.vibeName, style = AuraTypography.HeadingL, color = post.tierColor, letterSpacing = 2.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Badges & Caption
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            if (post.isOnTime) {
                                Box(modifier = Modifier.clip(CircleShape).background(AuraColors.NeonGreen.copy(alpha = 0.2f)).border(1.dp, AuraColors.NeonGreen, CircleShape).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("On Time ✓", color = AuraColors.NeonGreen, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                            } else {
                                Box(modifier = Modifier.clip(CircleShape).background(Color.Gray.copy(alpha = 0.2f)).border(1.dp, Color.Gray, CircleShape).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(post.lateText ?: "Late", color = AuraColors.TextSecondary, fontSize = 12.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(post.caption, color = AuraColors.TextPrimary, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Reactions
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReactionPill("🔥 12")
                            ReactionPill("💯 4")
                            ReactionPill("👑 1")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReactionPill(text: String) {
    Box(modifier = Modifier.clip(CircleShape).background(AuraColors.BackgroundSurface).border(1.dp, Color.DarkGray, CircleShape).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text, color = AuraColors.TextPrimary, fontSize = 12.sp)
    }
}
