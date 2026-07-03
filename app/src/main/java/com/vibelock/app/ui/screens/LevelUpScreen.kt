package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.vibelock.app.R
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography
import com.vibelock.app.ui.theme.PremiumBlack
import kotlinx.coroutines.delay

@Composable
fun LevelUpScreen(
    newLevel: Int,
    onCutsceneComplete: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.xp_oracle))
    
    LaunchedEffect(Unit) {
        // Play cinematic cutscene for 3 seconds
        delay(3000)
        onCutsceneComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(250.dp), contentAlignment = Alignment.Center) {
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier.fillMaxSize()
                )
                if (composition == null) {
                    Text("🧙‍♂️", fontSize = 100.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "LEVEL UP",
                style = AuraTypography.HeadingL,
                color = AuraColors.NeonPurple
            )
            
            Text(
                text = "You are now Level $newLevel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Text(
                text = "The XP Oracle acknowledges your grind.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
