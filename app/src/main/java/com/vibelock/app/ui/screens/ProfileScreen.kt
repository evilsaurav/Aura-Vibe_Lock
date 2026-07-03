package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import com.google.firebase.auth.FirebaseAuth
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.engine.UserState
import com.vibelock.app.engine.Achievement
import com.vibelock.app.engine.AchievementEngine
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.utils.ImageShareHelper

import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import coil.compose.AsyncImage
import android.os.Build
import com.vibelock.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userState: UserState,
    tier: AuraTier,
    onAvatarChange: (String) -> Unit,
    onShareStory: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToWrapped: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val displayName = user?.displayName ?: "Aura God"
    val email = user?.email ?: ""
    
    val context = LocalContext.current
    val activity = context as? Activity

    var showAvatarPicker by remember { mutableStateOf(false) }
    var isAvatarUpdating by remember { mutableStateOf(false) }
    var customAvatarInput by remember { mutableStateOf("") }
    
    val gifDatabase = remember {
        mapOf(
            "Desi" to listOf(
                "https://i.giphy.com/media/l1IYgVpg5B9dO/giphy.gif",
                "https://i.giphy.com/media/26n61GEYh2H0k/giphy.gif",
                "https://i.giphy.com/media/3o6wrvdHFbwBrStrjG/giphy.gif",
                "https://i.giphy.com/media/3o6nUX2Wlmka5Nlq4U/giphy.gif",
                "https://i.giphy.com/media/3ohfFqNgHB0qOisfDU/giphy.gif",
                "https://i.giphy.com/media/xT1XGXg11JkG1R2u88/giphy.gif",
                "https://i.giphy.com/media/l2YWkF5pQ7cEB0F/giphy.gif",
                "https://i.giphy.com/media/3o6fH0DEx6P09/giphy.gif"
            ),
            "Happy" to listOf(
                "https://i.giphy.com/media/3o7aD2saalBwwftBIY/giphy.gif",
                "https://i.giphy.com/media/26tn33aiTi1jIGsO4/giphy.gif",
                "https://i.giphy.com/media/l41lFw057lAJQMwg0/giphy.gif",
                "https://i.giphy.com/media/xT0xezQGU5xCDJuCPe/giphy.gif",
                "https://i.giphy.com/media/3o6Zt481isNvuFIWc0/giphy.gif",
                "https://i.giphy.com/media/l0MYt5jPR6QX5pnqM/giphy.gif",
                "https://i.giphy.com/media/chzz1FQgqhytWRWbp3/giphy.gif"
            ),
            "Sad" to listOf(
                "https://i.giphy.com/media/BEob5qwFkSJ7G/giphy.gif",
                "https://i.giphy.com/media/L95W4wv8nnb9K/giphy.gif",
                "https://i.giphy.com/media/d2lcHJTG5Tscg/giphy.gif",
                "https://i.giphy.com/media/W0c3xcZ3F1d0EYYb0f/giphy.gif",
                "https://i.giphy.com/media/qQdL532ZANbjy/giphy.gif",
                "https://i.giphy.com/media/l3vR4CdLInXOhr3O0/giphy.gif"
            ),
            "Chill" to listOf(
                "https://i.giphy.com/media/3o7TK15UeSng2XmGTC/giphy.gif",
                "https://i.giphy.com/media/iYK1uqbfkvDpe/giphy.gif",
                "https://i.giphy.com/media/3o7WTqVPev1E9E493O/giphy.gif",
                "https://i.giphy.com/media/5wWf7HapUvxApsZASs/giphy.gif",
                "https://i.giphy.com/media/ToMjGpKniGqRNLGBrhu/giphy.gif",
                "https://i.giphy.com/media/l0HlOaQcLJ2hHpYcw/giphy.gif"
            ),
            "Vibing" to listOf(
                "https://i.giphy.com/media/blSTtZehjAZ8I/giphy.gif",
                "https://i.giphy.com/media/AcfTF7tyikWyroPzPN/giphy.gif",
                "https://i.giphy.com/media/3osxY7eI6enqNBo2mQ/giphy.gif",
                "https://i.giphy.com/media/5xaOcLDE64VMF4LqqrK/giphy.gif",
                "https://i.giphy.com/media/mXnO9IiWWz3z0vMs/giphy.gif",
                "https://i.giphy.com/media/3o7abKhOpu0NwenH3O/giphy.gif"
            )
        )
    }

    var selectedMood by remember { mutableStateOf("Desi") }
    var currentGifSet by remember { mutableStateOf(gifDatabase["Desi"]!!.shuffled().take(6)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "AURA PROFILE",
                color = Color(tier.colorHex),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar Section
        val infiniteTransition = rememberInfiniteTransition()
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        val auraBrush = Brush.sweepGradient(
            colors = listOf(
                Color(tier.colorHex),
                Color(tier.colorHex).copy(alpha = 0.3f),
                Color(tier.colorHex),
                Color(tier.colorHex).copy(alpha = 0.1f),
                Color(tier.colorHex)
            )
        )
        // Adjust border thickness based on level (min 2dp, max 8dp)
        val borderThickness = (2 + (userState.level / 5f)).coerceAtMost(8f).dp

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(tier.colorHex).copy(alpha = 0.2f))
                .border(borderThickness, auraBrush, CircleShape)
                .graphicsLayer { rotationZ = rotation }
                .clickable { showAvatarPicker = true },
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.fillMaxSize().graphicsLayer { rotationZ = -rotation }, contentAlignment = Alignment.Center) {
                if (isAvatarUpdating) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Wait...", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                    LaunchedEffect(userState.avatarUrl) {
                        kotlinx.coroutines.delay(1500)
                        isAvatarUpdating = false
                    }
                } else if (userState.avatarUrl.isNotEmpty()) {
                    androidx.compose.runtime.key(userState.avatarUrl) {
                        com.vibelock.app.ui.components.AuraAvatar(
                            url = userState.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Text("🤖", fontSize = 40.sp)
                }
            }
        }
        Text(
            text = "Tap to change avatar",
            color = Color.Gray,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name and Title
        Text(
            text = displayName,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = tier.name.replace("_", " "),
            color = Color(tier.colorHex),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = email,
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("🔥 Streak", "${userState.currentStreak} Days")
            StatCard("⚡ XP", "${userState.xp}")
            StatCard("🏆 Level", "${userState.level}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Share Story Button
        NeonButton(
            text = "VIEW AURA STORY",
            color = AuraColors.NeonPurple,
            onClick = onNavigateToWrapped,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Achievements Section ---
        Text(
            text = "TROPHY ROOM",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )
        
        val achievements = remember(userState) { AchievementEngine.getAchievementsForUser(userState) }
        var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(achievements) { achievement ->
                val bgColor = if (achievement.isUnlocked) Color(0xFF1E1E1E) else Color.Black
                val borderColor = if (achievement.isUnlocked) Color(tier.colorHex).copy(alpha = 0.5f) else Color.DarkGray
                
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = achievement.isUnlocked) { selectedAchievement = achievement }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (achievement.isUnlocked) achievement.iconEmoji else "🔒",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = achievement.title,
                            color = if (achievement.isUnlocked) Color.White else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
        
        // Achievement Dialog
        selectedAchievement?.let { achievement ->
            Dialog(onDismissRequest = { selectedAchievement = null }) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFF0F0F0F))
                        .border(1.dp, Color(tier.colorHex).copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // 4D Lottie Animation (Placeholder Trophy)
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.achievement_trophy))
                        
                        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
                            if (composition != null) {
                                LottieAnimation(
                                    composition = composition,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(achievement.iconEmoji, fontSize = 100.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = achievement.title.uppercase(),
                            color = Color(tier.colorHex),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = achievement.message,
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        NeonButton(
                            text = "SHARE ACHIEVEMENT",
                            color = Color(tier.colorHex),
                            onClick = {
                                activity?.let { ImageShareHelper.shareScreenshot(it) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        // ------------------------------

        Spacer(modifier = Modifier.height(32.dp))

        // Activity History
        Text(
            text = "RECENT ACTIVITY",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            // Mock history list for UI
            val history = listOf(
                "Checked in with ⚡ Grind vibe",
                "Checked in with 🌊 Chill vibe",
                "Earned 50 XP bonus!",
                "Checked in with ✨ Slay vibe"
            )
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                history.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = item, color = Color.LightGray, fontSize = 14.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // End of Profile content
    }

    if (showAvatarPicker) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarPicker = false },
            containerColor = Color(0xFF121212)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Text(
                    text = "CHOOSE YOUR AVATAR",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Custom URL Input
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = customAvatarInput,
                        onValueChange = { customAvatarInput = it },
                        placeholder = { Text("Paste GIF/Image URL", color = Color.Gray, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).height(50.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.DarkGray,
                            focusedBorderColor = AuraColors.NeonPurple,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (customAvatarInput.isNotEmpty()) {
                                isAvatarUpdating = true
                                onAvatarChange(customAvatarInput)
                                showAvatarPicker = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraColors.NeonPurple),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // Mood Filters
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(gifDatabase.keys.toList()) { mood ->
                            val isSelected = mood == selectedMood
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) AuraColors.NeonPurple else Color(0xFF1E1E1E))
                                    .clickable {
                                        selectedMood = mood
                                        currentGifSet = gifDatabase[mood]!!.shuffled().take(6)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = mood,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            currentGifSet = gifDatabase[selectedMood]!!.shuffled().take(6)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E1E1E))
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.height(300.dp)
                ) {
                    items(currentGifSet) { url ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .clickable {
                                    isAvatarUpdating = true
                                    onAvatarChange(url)
                                    showAvatarPicker = false
                                }
                        ) {
                            com.vibelock.app.ui.components.AuraAvatar(
                                url = url,
                                contentDescription = "Avatar Option",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    AuraGlassCard(modifier = Modifier.width(100.dp).height(80.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = Color.Gray, fontSize = 12.sp)
        }
    }
}


