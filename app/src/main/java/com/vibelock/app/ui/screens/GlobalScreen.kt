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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.components.AuraGlassCard
import com.vibelock.app.ui.components.NeonText
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun GlobalScreen(globalCheckIns: List<com.vibelock.app.data.GlobalCheckIn> = emptyList()) {
    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(20000, easing = LinearEasing), repeatMode = RepeatMode.Restart)
    )

    Column(modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase).padding(24.dp)) {
        
        // Map Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                NeonText("GLOBAL MAP", style = AuraTypography.HeadingXL, color = AuraColors.NeonCyan)
                Text("Live Auras Around the World", color = AuraColors.TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Map Composable
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(LatLng(20.5937, 78.9629), 3f) // Centered roughly on India
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, AuraColors.NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = false)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // #1 Global Champion Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AuraColors.NeonGold.copy(alpha = 0.15f))
                .border(2.dp, AuraColors.NeonGold, RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👑 Current Global Champion", color = AuraColors.NeonGold, fontSize = 12.sp, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.DarkGray).border(3.dp, AuraColors.NeonGold, CircleShape))
                Spacer(modifier = Modifier.height(8.dp))
                Text("GigaChad99 ⚡", style = AuraTypography.HeadingL, color = AuraColors.TextPrimary)
                Text("145,200 XP", style = AuraTypography.DisplayM, color = AuraColors.NeonGold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Top 100 List Placeholder
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(15) { index ->
                val rank = index + 4
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#$rank", style = AuraTypography.HeadingM, color = AuraColors.TextSecondary, modifier = Modifier.width(40.dp))
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Gray))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("User_Random_$rank", color = AuraColors.TextPrimary)
                        Text("Sigma Aura", color = AuraColors.NeonPink, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${100000 - (rank * 1000)} XP", color = AuraColors.NeonCyan)
                        Text("🔥 ${100 - rank}d", color = AuraColors.NeonGold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Aura Pulse (Live activity feed)
        var pulseMessage by remember { mutableStateOf("Scanning the matrix... 📡") }
        LaunchedEffect(globalCheckIns) {
            val fallbacks = listOf(
                "Someone in Mumbai just hit a 100-day streak! 🔥",
                "Anonymous locked Grind Vibe in New York! ⚡",
                "A Legendary Aura was just unlocked in Tokyo! 👑",
                "10,000 users are currently Vibing! 🌐"
            )
            while (true) {
                if (globalCheckIns.isNotEmpty()) {
                    val randomCheckIn = globalCheckIns.random()
                    val city = if (randomCheckIn.lat > 0) "North" else "South"
                    pulseMessage = "Someone in the $city Hemisphere locked ${randomCheckIn.vibe} Vibe! ✨"
                } else {
                    pulseMessage = fallbacks.random()
                }
                kotlinx.coroutines.delay(4000)
            }
        }

        AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("📡", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("AURA PULSE", color = AuraColors.NeonGreen, fontSize = 10.sp, letterSpacing = 2.sp)
                    androidx.compose.animation.AnimatedContent(targetState = pulseMessage, label = "pulse") { targetMsg ->
                        Text(targetMsg, color = AuraColors.TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
