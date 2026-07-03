package com.vibelock.app.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToAuraTutorial(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F0F0F),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NeonText(text = "HOW TO AURA", style = AuraTypography.HeadingL, color = AuraColors.NeonCyan)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome to the real-time social gamification matrix. Here is how you conquer it.",
                color = Color.LightGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TutorialStepTracker()
            
            Spacer(modifier = Modifier.height(24.dp))
            
            NeonButton(
                text = "I'm Ready",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                color = AuraColors.NeonPurple
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TutorialStepTracker() {
    var currentStep by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while(true) {
            delay(4000)
            currentStep = (currentStep + 1) % 3
        }
    }
    
    Column(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        // Step Tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("1. The Loop", "2. XP & Tiers", "3. Global AI").forEachIndexed { index, title ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = title,
                        color = if (currentStep == index) AuraColors.NeonCyan else Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(2.dp)
                            .background(if (currentStep == index) AuraColors.NeonCyan else Color.DarkGray)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Content Area
        Box(modifier = Modifier.fillMaxSize()) {
            androidx.compose.animation.AnimatedContent(
                targetState = currentStep,
                label = "TutorialSteps",
                transitionSpec = {
                    androidx.compose.animation.fadeIn(animationSpec = tween(500)) togetherWith androidx.compose.animation.fadeOut(animationSpec = tween(500))
                }
            ) { targetStep ->
                when (targetStep) {
                    0 -> Step1CoreLoop()
                    1 -> Step2XPTiers()
                    2 -> Step3GlobalAI()
                }
            }
        }
    }
}

@Composable
fun Step1CoreLoop() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.8f, targetValue = 1.1f,
                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(AuraColors.NeonPurple.copy(alpha = 0.3f))
                    .scale(scale)
            )
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AuraColors.NeonPurple, modifier = Modifier.size(64.dp))
        }
        Text("Lock Your Vibe", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Every day, select your vibe (Grind, Chill, Chaos) to check in. Doing this grants XP and keeps your streak alive.",
            color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Step2XPTiers() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        val infiniteTransition = rememberInfiniteTransition()
        val pathEnd by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
        )
        
        Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(0f, size.height)
                    quadraticBezierTo(size.width * 0.5f, size.height * 0.8f, size.width, size.height * 0.2f)
                }
                drawPath(
                    path = path,
                    color = Color.DarkGray,
                    style = Stroke(width = 4f)
                )
                // Draw animated progress
                val pathMeasure = PathMeasure()
                pathMeasure.setPath(path, false)
                val animatedPath = Path()
                pathMeasure.getSegment(0f, pathMeasure.length * pathEnd, animatedPath, true)
                drawPath(
                    path = animatedPath,
                    brush = Brush.horizontalGradient(listOf(AuraColors.NeonPurple, AuraColors.NeonCyan)),
                    style = Stroke(width = 8f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Streaks = Multipliers", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Higher streaks multiply your XP. Level up to unlock new Tiers. If you miss a day, your streak resets unless you have a Shield!",
            color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Step3GlobalAI() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            val infiniteTransition = rememberInfiniteTransition()
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing))
            )
            Canvas(modifier = Modifier.fillMaxSize().rotate(rotation)) {
                drawCircle(color = AuraColors.NeonCyan, radius = size.width / 2, style = Stroke(width = 2f))
                drawLine(color = AuraColors.NeonCyan.copy(alpha=0.5f), start = Offset(size.width/2, 0f), end = Offset(size.width/2, size.height))
                drawLine(color = AuraColors.NeonCyan.copy(alpha=0.5f), start = Offset(0f, size.height/2), end = Offset(size.width, size.height/2))
            }
            Text("AI", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("The Matrix Analyzes You", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Your vibes are plotted on the 3D globe. Gemini AI analyzes your weekly logs to roast or hype you up in the Insights tab.",
            color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center
        )
    }
}
