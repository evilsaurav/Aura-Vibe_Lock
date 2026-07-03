package com.vibelock.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.ui.components.AuraOrb
import com.vibelock.app.ui.components.NeonButton
import com.vibelock.app.ui.components.OrbState
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun CustomizationLabScreen() {
    val skins = listOf("Default", "Void", "Galaxy", "Solar Flare", "Ice", "Lava")
    var selectedSkin by remember { mutableStateOf(skins[0]) }

    Column(modifier = Modifier.fillMaxSize().background(AuraColors.BackgroundBase).padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("AURA LAB", style = AuraTypography.HeadingXL, color = AuraColors.NeonCyan, letterSpacing = 2.sp)
        Text("Express your vibe", color = AuraColors.TextSecondary)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Preview Area
        Box(
            modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(24.dp)).background(AuraColors.BackgroundElevated),
            contentAlignment = Alignment.Center
        ) {
            // Orb Preview
            AuraOrb(tier = AuraTier.VIBE_ARCHITECT, orbState = OrbState.IDLE, onClick = {})
            Text(selectedSkin, modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp), color = AuraColors.TextPrimary)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("Orb Skins", style = AuraTypography.HeadingL, color = AuraColors.TextPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(skins.size) { index ->
                val skin = skins[index]
                val isSelected = skin == selectedSkin
                val isLocked = index > 2
                
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AuraColors.NeonPurple.copy(alpha = 0.3f) else AuraColors.BackgroundSurface)
                        .border(1.dp, if (isSelected) AuraColors.NeonPurple else Color.DarkGray, RoundedCornerShape(12.dp))
                        .clickable(enabled = !isLocked) { selectedSkin = skin },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (isLocked) "🔒" else "✨", fontSize = 24.sp)
                        Text(skin, color = if (isLocked) AuraColors.TextSecondary else AuraColors.TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
        
        NeonButton(text = "Apply Changes", onClick = {}, modifier = Modifier.fillMaxWidth(), color = AuraColors.NeonCyan)
    }
}
