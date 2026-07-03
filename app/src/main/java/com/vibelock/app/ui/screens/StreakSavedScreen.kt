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
import com.vibelock.app.ui.theme.AuraTypography
import com.vibelock.app.ui.theme.PremiumBlack
import kotlinx.coroutines.delay

@Composable
fun StreakSavedScreen(
    onDismiss: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.streak_guardian))
    
    LaunchedEffect(Unit) {
        // Show for 4 seconds then return to home
        delay(4000)
        onDismiss()
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
                    Text("🛡️", fontSize = 100.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "STREAK SAVED",
                style = AuraTypography.HeadingL,
                color = Color(0xFFEAB308) // Gold/Yellow
            )
            
            Text(
                text = "The Streak Guardian consumed a shield.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Text(
                text = "Your Aura remains intact.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}
