package com.vibelock.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.vibelock.app.data.WrappedData
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.utils.ImageShareHelper
import com.vibelock.app.engine.UserState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WrappedScreen(
    wrappedData: WrappedData,
    userState: UserState,
    onClose: () -> Unit,
    activity: android.app.Activity?
) {
    if (wrappedData.isMock) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050505)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Text(
                    text = wrappedData.aiMessage,
                    color = Color.LightGray,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(24.dp))
                NeonButton(text = "BACK TO PROFILE", onClick = onClose)
            }
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { 6 })
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> Card1Overview(wrappedData, pagerState.currentPage == 0)
                1 -> Card2DominantVibe(wrappedData, pagerState.currentPage == 1)
                2 -> Card3Streak(wrappedData, pagerState.currentPage == 2)
                3 -> Card4Themes(wrappedData, pagerState.currentPage == 3)
                4 -> Card5AIMessage(wrappedData, pagerState.currentPage == 4)
                5 -> Card6Share(wrappedData, userState, activity, onClose)
            }
        }
        
        // Pager indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(6) { iteration ->
                val color = if (pagerState.currentPage == iteration) AuraColors.NeonCyan else Color.DarkGray
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(50))
                        .background(color)
                )
            }
        }
        
        if (wrappedData.isMock) {
            Text(
                text = "PREVIEW MODE",
                color = AuraColors.NeonPurple,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun Card1Overview(data: WrappedData, isVisible: Boolean) {
    val count = remember { Animatable(0f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            count.animateTo(
                targetValue = data.totalCheckIns.toFloat(),
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )
        } else {
            count.snapTo(0f)
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("This month, you checked in", color = Color.LightGray, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "${count.value.toInt()} TIMES",
            color = AuraColors.NeonCyan,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("Top 3% of all Aura users.", color = AuraColors.NeonPurple, fontSize = 18.sp)
    }
}

@Composable
fun Card2DominantVibe(data: WrappedData, isVisible: Boolean) {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        } else {
            scale.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(scale.value)
                .background(Brush.radialGradient(listOf(AuraColors.NeonPurple, Color.Transparent)))
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Your dominant vibe was...", color = Color.White, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = data.dominantVibe.uppercase(),
                color = AuraColors.NeonCyan,
                fontSize = 56.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun Card3Streak(data: WrappedData, isVisible: Boolean) {
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            alpha.animateTo(1f, tween(1000))
        } else {
            alpha.snapTo(0f)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Your best streak this month:", color = Color.LightGray, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "🔥 ${data.bestStreak} DAYS",
            color = Color(0xFFFF5722),
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.alpha(alpha.value)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("You didn't break.", color = Color.White, fontSize = 20.sp, modifier = Modifier.alpha(alpha.value))
    }
}

@Composable
fun Card4Themes(data: WrappedData, isVisible: Boolean) {
    var animatedProgress by remember { mutableStateOf(0f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            animate(0f, 1f, animationSpec = tween(1500)) { value, _ ->
                animatedProgress = value
            }
        } else {
            animatedProgress = 0f
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Your mind was focused on:", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(48.dp))
        
        data.themes.forEach { theme ->
            Text("${theme.title} — ${theme.percentage}%", color = AuraColors.NeonCyan, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color.DarkGray, RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(theme.percentage / 100f * animatedProgress)
                        .height(8.dp)
                        .background(AuraColors.NeonPurple, RoundedCornerShape(4.dp))
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun Card5AIMessage(data: WrappedData, isVisible: Boolean) {
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            displayedText = ""
            for (i in data.aiMessage.indices) {
                displayedText += data.aiMessage[i]
                delay(30)
            }
        } else {
            displayedText = ""
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Aura AI Take:", color = AuraColors.NeonPurple, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = displayedText,
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 32.sp
        )
    }
}

@Composable
fun Card6Share(data: WrappedData, userState: UserState, activity: android.app.Activity?, onClose: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Poster Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(9f/16f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF111111))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("AURA", color = AuraColors.NeonCyan, fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 8.sp)
                Text("Month Wrapped", color = Color.White, fontSize = 16.sp)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text("Vibe: ${data.dominantVibe.uppercase()}", color = AuraColors.NeonPurple, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Streak: ${data.bestStreak} days \uD83D\uDD25", color = Color.White, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Check-ins: ${data.totalCheckIns}", color = Color.LightGray, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Level ${data.startLevel} → ${data.endLevel}", color = AuraColors.NeonCyan, fontSize = 18.sp)
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text("Check your Aura Wrapped → aura.app", color = Color.DarkGray, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        NeonButton(
            text = "FLEX ON INSTAGRAM",
            onClick = {
                activity?.let { ImageShareHelper.shareScreenshot(it) }
            },
            color = AuraColors.NeonPurple,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onClose) {
            Text("Close", color = Color.Gray)
        }
    }
}
