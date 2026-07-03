package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.vibelock.app.ai.GeminiEngine
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.engine.UserState
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.vibelock.app.utils.ImageShareHelper
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography
import kotlinx.coroutines.launch

@Composable
fun StoryShareScreen(
    userState: UserState,
    tier: AuraTier,
    onClose: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val displayName = user?.displayName ?: "Aura God"
    
    val context = LocalContext.current
    val activity = context as? Activity

    val coroutineScope = rememberCoroutineScope()
    var aiQuote by remember { mutableStateOf("Generating your aura...") }

    // Fetch AI Quote on launch
    LaunchedEffect(Unit) {
        val geminiEngine = GeminiEngine()
        aiQuote = geminiEngine.generateStoryQuote(tier.name.replace("_", " "))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // Close Button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
        }

        // Spotify-style Wrapped Card (Story Container)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .aspectRatio(9f / 16f) // 1080x1920 aspect ratio
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(tier.colorHex).copy(alpha = 0.8f),
                            Color(0xFF111111)
                        )
                    )
                )
                .border(2.dp, Color(tier.colorHex).copy(alpha = 0.3f), RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Text
                Text(
                    text = "AURA WRAPPED",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Avatar / Image
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .border(4.dp, Color(tier.colorHex), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (userState.avatarUrl.isNotEmpty()) {
                        com.vibelock.app.ui.components.AuraAvatar(
                            url = userState.avatarUrl,
                            contentDescription = "Story Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = displayName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // AI Generated Quote
                Text(
                    text = "\"$aiQuote\"",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // User Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "LEVEL", color = Color.Gray, fontSize = 10.sp)
                                Text(text = "${userState.level}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "STREAK", color = Color.Gray, fontSize = 10.sp)
                                Text(text = "${userState.currentStreak}🔥", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "TIER", color = Color.Gray, fontSize = 10.sp)
                                Text(text = tier.name.replace("_", " "), color = Color(tier.colorHex), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Next Target
                        Text(
                            text = "NEXT TARGET",
                            color = AuraColors.NeonGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Reach Level ${userState.level + 1} (${userState.level * 100 - userState.xp} XP away)",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Brand
                Text(
                    text = "AURA",
                    style = AuraTypography.HeadingM,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // Share Action Button (Mock action for now)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            NeonButton(
                text = "SHARE STORY",
                color = AuraColors.NeonCyan,
                onClick = {
                    activity?.let {
                        ImageShareHelper.shareScreenshot(it)
                    }
                }
            )
        }
    }
}
