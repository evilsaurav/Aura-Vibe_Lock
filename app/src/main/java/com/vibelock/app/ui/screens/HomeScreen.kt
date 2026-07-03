package com.vibelock.app.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.blur
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.engine.MissionEngine
import com.vibelock.app.engine.UserState
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.AuraOrb
import com.vibelock.app.ui.components.AuraXPProgressBar
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.components.NeonText
import com.vibelock.app.ui.components.HowToAuraTutorial
import com.vibelock.app.ui.components.OrbState
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraShape
import com.vibelock.app.ui.theme.AuraSpacing
import com.vibelock.app.ui.theme.AuraTypography
import com.vibelock.app.ui.animations.VibeParticleSystem
import com.vibelock.app.ui.animations.VibeParticleConfig
import com.vibelock.app.ui.animations.rememberParallaxOffset
import com.vibelock.app.haptics.VibeHapticEngine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun AmbientNebulaBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    
    val p1 by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(30000, easing = LinearEasing), repeatMode = RepeatMode.Reverse)
    )
    val p2 by transition.animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(animation = tween(25000, easing = LinearEasing), repeatMode = RepeatMode.Reverse)
    )

    Canvas(modifier = modifier.fillMaxSize().background(AuraColors.BackgroundBase)) {
        // Gradient 1: NeonPurple
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuraColors.NeonPurple.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(size.width * p1, size.height * 0.2f),
                radius = 400.dp.toPx()
            ),
            radius = 400.dp.toPx(),
            center = Offset(size.width * p1, size.height * 0.2f)
        )
        // Gradient 2: NeonCyan
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuraColors.NeonCyan.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(size.width * p2, size.height * 0.8f),
                radius = 350.dp.toPx()
            ),
            radius = 350.dp.toPx(),
            center = Offset(size.width * p2, size.height * 0.8f)
        )
        // Gradient 3: NeonPink
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AuraColors.NeonPink.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(size.width * 0.5f, size.height * p1),
                radius = 300.dp.toPx()
            ),
            radius = 300.dp.toPx(),
            center = Offset(size.width * 0.5f, size.height * p1)
        )
    }
}

@Composable
fun HomeScreen(
    userState: UserState,
    tier: AuraTier,
    xpRequired: Int,
    globalCheckIns: List<com.vibelock.app.data.GlobalCheckIn>,
    friendCount: Int,
    onNavigateToCheckIn: (String) -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToWrapped: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFriends: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToMissions: () -> Unit,
    onGlobeDrag: (Float) -> Unit = {},
    onVibeSelect: (String) -> Unit = {}
) {
    val vibes = listOf("⚡ Grind", "🛋️ Chill", "💀 Chaos", "☕ Cozy", "🌑 Dark", "💅 Slay")
    var selectedVibe by remember { mutableStateOf(vibes[0]) }
    val isCheckedIn = (System.currentTimeMillis() - userState.lastCheckInTimestamp) < 24 * 60 * 60 * 1000
    
    val context = LocalContext.current
    val hapticEngine = remember { VibeHapticEngine(context) }
    
    // Focus Mode State
    var hasInteracted by remember { mutableStateOf(isCheckedIn) }
    val focusAlpha by animateFloatAsState(
        targetValue = if (!hasInteracted) 0.3f else 1f,
        animationSpec = tween(800)
    )
    val focusBlur by animateFloatAsState(
        targetValue = if (!hasInteracted) 12f else 0f,
        animationSpec = tween(800)
    )
    
    val activeParticleVibe = remember(isCheckedIn, userState.currentVibe) {
        if (isCheckedIn && userState.currentVibe.isNotEmpty()) {
            when {
                userState.currentVibe.contains("Grind", ignoreCase = true) -> VibeParticleConfig.GRIND
                userState.currentVibe.contains("Chill", ignoreCase = true) -> VibeParticleConfig.CHILL
                userState.currentVibe.contains("Chaos", ignoreCase = true) -> VibeParticleConfig.CHAOS
                userState.currentVibe.contains("Cozy", ignoreCase = true) -> VibeParticleConfig.COZY
                userState.currentVibe.contains("Dark", ignoreCase = true) -> VibeParticleConfig.SAD
                else -> VibeParticleConfig.CHILL
            }
        } else {
            VibeParticleConfig.CHILL
        }
    }

    val parallax = rememberParallaxOffset()

    // Level Up Tracker
    var previousLevel by remember { mutableStateOf(userState.level) }
    var showLevelUp by remember { mutableStateOf(false) }
    var showTutorial by remember { mutableStateOf(false) }

    LaunchedEffect(userState.level) {
        if (userState.level > previousLevel) {
            showLevelUp = true
        }
        previousLevel = userState.level
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        
        // Deep background layer (moves slowly)
        AmbientNebulaBackground(
            modifier = Modifier.offset { IntOffset((parallax.x * 20f).roundToInt(), (parallax.y * 20f).roundToInt()) }
        )
        
        // Particle system layer (moves moderately)
        VibeParticleSystem(
            vibeConfig = activeParticleVibe,
            modifier = Modifier.offset { IntOffset((parallax.x * 60f).roundToInt(), (parallax.y * 60f).roundToInt()) }
        )

        // Main UI Layer (moves the most)
    Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset((parallax.x * 120f).roundToInt(), (parallax.y * 120f).roundToInt()) }
        ) {
            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AuraSpacing.l)
                    .padding(top = AuraSpacing.xxl, bottom = AuraSpacing.m),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Bar
            val greetings = listOf("Hey, Aura God", "What's good, Boss?", "Ready to slay?", "Vibe check, Captain", "Stay toxic, Legend", "Lock in, Player")
            val randomGreeting = remember { greetings.random() }

            Row(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                      Box(modifier = Modifier.size(40.dp).clip(AuraShape.Circle).background(Color.Gray).clickable { onNavigateToProfile() }) {
                          if (userState.avatarUrl.isNotEmpty()) {
                              com.vibelock.app.ui.components.AuraAvatar(
                                  url = userState.avatarUrl,
                                  contentDescription = "Avatar",
                                  modifier = Modifier.fillMaxSize()
                              )
                          }
                      }
                    Spacer(modifier = Modifier.width(AuraSpacing.s))
                    Text(randomGreeting, style = AuraTypography.HeadingM, color = AuraColors.TextPrimary)
                }
                NeonText(text = "AURA", style = AuraTypography.HeadingXL)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showTutorial = true }) {
                        Icon(Icons.Default.Info, contentDescription = "How to Aura", tint = AuraColors.NeonCyan)
                    }
                    Text("🔥${userState.currentStreak}", style = AuraTypography.HeadingM, color = AuraColors.TextPrimary)
                }
            }
            
            // Companion / Friend Ticker
            if (friendCount == 0) {
                // Solo Companion Mode
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.vibelock.app.ui.components.AuraBotCompanion(vibe = selectedVibe, modifier = Modifier.padding(end = 8.dp))
                    Column {
                        Text(
                            text = "Invite 1 friend to get a special shield 🛡️",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .clip(AuraShape.Pill)
                                .background(AuraColors.NeonPurple.copy(alpha = 0.2f))
                                .border(1.dp, AuraColors.NeonPurple, AuraShape.Pill)
                                .clickable { onNavigateToFriends() }
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Invite Friend", color = AuraColors.NeonPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "🔥 $friendCount Friends Active",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { onNavigateToFriends() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.l))

            // Center Piece: 3D AuraGlobe spinning behind the AuraOrb
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .graphicsLayer { alpha = focusAlpha }
                    .blur(radius = focusBlur.dp),
                contentAlignment = Alignment.Center
            ) {
                com.vibelock.app.ui.AuraGlobe(
                    checkIns = globalCheckIns,
                    onGlobeDrag = onGlobeDrag,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Level and XP
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AuraOrb(
                        tier = tier,
                        orbState = if (isCheckedIn) OrbState.CHARGED else OrbState.IDLE,
                        onClick = { onNavigateToCheckIn(selectedVibe) },
                        modifier = Modifier.padding(vertical = AuraSpacing.xl)
                    )
                    Text(
                        text = "${tier.name.replace("_", " ")} Lvl ${userState.level}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    
                    // Streak Shields
                    if (userState.shields > 0 || userState.currentStreak > 3) {
                        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
                            repeat(5) { index ->
                                if (index < userState.shields) {
                                    Text("🛡️", fontSize = 16.sp, modifier = Modifier.padding(horizontal = 2.dp))
                                } else {
                                    Text("🛡️", fontSize = 16.sp, modifier = Modifier.padding(horizontal = 2.dp), color = Color.DarkGray.copy(alpha = 0.3f)) // Dim placeholder
                                }
                            }
                        }
                        if (userState.shields == 0 && userState.currentStreak > 3) {
                            Text("No shields! Reach 7-day streak.", color = Color(0xFFE53935), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }

            Text("🔥 ${userState.currentStreak} Day Streak", style = AuraTypography.HeadingL, color = AuraColors.NeonGold)
            
            Spacer(modifier = Modifier.height(AuraSpacing.m))
            
            AuraXPProgressBar(currentXP = userState.xp, maxXP = xpRequired, color = Color(tier.colorHex))
            
            Spacer(modifier = Modifier.height(AuraSpacing.l))

            // Vibe Selector
            Column(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                Text("Today's Vibe", style = AuraTypography.Label, color = AuraColors.TextSecondary)
                Spacer(modifier = Modifier.height(AuraSpacing.s))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(AuraSpacing.s)) {
                    items(vibes) { vibe ->
                        val isSelected = selectedVibe == vibe
                        Box(
                            modifier = Modifier
                                .clip(AuraShape.Pill)
                                .background(if (isSelected) AuraColors.NeonPurple else Color.Transparent)
                                .border(1.dp, if (isSelected) Color.Transparent else AuraColors.NeonPurple.copy(alpha=0.3f), AuraShape.Pill)
                                .clickable { 
                                    selectedVibe = vibe 
                                    hasInteracted = true
                                    onVibeSelect(vibe)
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            com.vibelock.app.ui.components.VibeButtonContent(vibe = vibe, isSelected = isSelected)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.l))

            // Missions Preview
            val completedIds = userState.completedMissionIds.split(",")
            val currentMissions = remember(userState.lastMissionDate, userState.completedMissionIds) {
                MissionEngine.generateDailyMissions(MissionEngine.getCurrentDateString(), completedIds)
            }
            val totalCompleted = currentMissions.count { it.isComplete }
            val xpReward = currentMissions.filter { !it.isComplete }.sumOf { it.xpReward }

            AuraGlassCard(modifier = Modifier.fillMaxWidth().clickable { onNavigateToMissions() }) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🎯 TODAY'S MISSIONS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("$totalCompleted/3 done", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { totalCompleted / 3f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF10B981),
                        trackColor = Color.DarkGray
                    )
                    if (totalCompleted < 3) {
                        Text("${3 - totalCompleted} remaining for $xpReward XP", color = Color.LightGray, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
                    } else {
                        Text("All missions complete! +150 BONUS XP", color = Color(0xFFF59E0B), fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.l))

            // Aura AI Insights CTA
            AuraGlassCard(modifier = Modifier.fillMaxWidth().clickable { onNavigateToAI() }) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🤖 AURA AI", color = AuraColors.NeonPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Weekly Analysis & Roast", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Get your brutal vibe check.", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(AuraShape.Circle)
                            .background(AuraColors.NeonPurple.copy(alpha = 0.2f))
                            .border(1.dp, AuraColors.NeonPurple, AuraShape.Circle),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧠", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.l))

            // Vibe Wrapped CTA
            AuraGlassCard(modifier = Modifier.fillMaxWidth().clickable { onNavigateToWrapped() }) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🔮 VIBE WRAPPED", color = AuraColors.NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Your Aura Story", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Explore your 4D cinematic recap.", color = Color.LightGray, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(AuraShape.Circle)
                            .background(AuraColors.NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, AuraColors.NeonCyan, AuraShape.Circle),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✨", fontSize = 20.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.l))

            // Check In CTA
            if (!isCheckedIn) {
                // Subtle reminder from Aura Bot
                AuraGlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🤖", fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                        Text(
                            text = "Aaj ka vibe abhi lock nahi hua — kya chal raha hai?",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
                NeonButton(text = "LOCK VIBE", onClick = { onNavigateToCheckIn(selectedVibe) }, modifier = Modifier.fillMaxWidth())
            } else {
                AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text("🔒 Vibe Locked", style = AuraTypography.HeadingL, color = AuraColors.NeonGreen, modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.height(AuraSpacing.xl))

            // How To Aura (App Rules)
            AuraGlassCard(modifier = Modifier.fillMaxWidth().graphicsLayer { alpha = focusAlpha }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📖 HOW TO AURA", color = AuraColors.NeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("1. Lock Your Vibe daily to earn XP and level up.", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("2. Keep your streak alive to multiply your rewards.", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("3. Complete daily missions for bonus XP.", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("4. Reach higher tiers (Silver, Gold) to assert dominance.", color = Color.LightGray, fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(AuraSpacing.xl))
        }
        
        // Full Screen Overlays
        if (showLevelUp) {
            com.vibelock.app.ui.animations.LevelUpLootBox(
                newLevel = userState.level,
                hapticEngine = hapticEngine,
                onDismiss = { showLevelUp = false }
            )
        }
        
        if (showTutorial) {
            HowToAuraTutorial(onDismiss = { showTutorial = false })
        }
    }
}
}
