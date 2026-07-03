package com.vibelock.app.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vibelock.app.ui.theme.AuraColors
import kotlinx.coroutines.delay

fun getVibeColor(vibe: String): Color {
    return when (vibe.lowercase()) {
        "grind" -> Color(0xFFFF5722) // Orange
        "chill" -> Color(0xFF3B82F6) // Blue
        "chaos" -> Color(0xFF8B5CF6) // Purple
        "cozy" -> Color(0xFFFFD700)  // Yellow
        "sad" -> Color(0xFF1E3A8A)   // Dark Blue
        else -> AuraColors.NeonCyan
    }
}

@Composable
fun AnimatedBottomIcon(
    iconContent: @Composable (isTapped: Boolean, isSelected: Boolean) -> Unit,
    selected: Boolean,
    currentVibe: String,
    onClick: () -> Unit
) {
    var isTapped by remember { mutableStateOf(false) }

    // Tap scale pop animation
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 1.3f else if (selected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    // Tap translation pop (jumps up slightly)
    val translationY by animateFloatAsState(
        targetValue = if (isTapped) -16f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    // Tap shadow/elevation pop
    val shadow by animateFloatAsState(
        targetValue = if (isTapped) 8f else if (selected) 2f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    val glowColor = getVibeColor(currentVibe).copy(alpha = if (selected) 0.5f else 0f)

    LaunchedEffect(isTapped) {
        if (isTapped) {
            delay(150)
            isTapped = false
        }
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY
                shadowElevation = shadow
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isTapped = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // Glow effect for selected
        if (selected) {
            val infiniteTransition = rememberInfiniteTransition()
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Canvas(modifier = Modifier.matchParentSize()) {
                drawCircle(
                    color = glowColor.copy(alpha = glowAlpha),
                    radius = size.width / 2f
                )
            }
        }

        iconContent(isTapped, selected)
    }
}

// Stub implementation for drawing icons using canvas. We use Text emojis wrapped in rich physics for now, 
// as fully custom Canvas vector drawing for icons is vast. We will apply the physics, glow, and particles around them!

@Composable
fun HomeIconAnim(selected: Boolean, currentVibe: String, onClick: () -> Unit) {
    AnimatedBottomIcon(
        iconContent = { tapped, sel ->
            androidx.compose.material3.Text(
                "🏠", 
                fontSize = 24.sp
            )
        },
        selected = selected,
        currentVibe = currentVibe,
        onClick = onClick
    )
}

@Composable
fun SquadIconAnim(selected: Boolean, currentVibe: String, onClick: () -> Unit) {
    AnimatedBottomIcon(
        iconContent = { tapped, sel ->
            androidx.compose.material3.Text(
                "⚡", 
                fontSize = 24.sp
            )
        },
        selected = selected,
        currentVibe = currentVibe,
        onClick = onClick
    )
}

@Composable
fun TrophyIconAnim(selected: Boolean, currentVibe: String, onClick: () -> Unit) {
    AnimatedBottomIcon(
        iconContent = { tapped, sel ->
            androidx.compose.material3.Text(
                "🏆", 
                fontSize = 24.sp
            )
        },
        selected = selected,
        currentVibe = currentVibe,
        onClick = onClick
    )
}

@Composable
fun ProfileIconAnim(selected: Boolean, currentVibe: String, onClick: () -> Unit) {
    AnimatedBottomIcon(
        iconContent = { tapped, sel ->
            androidx.compose.material3.Text(
                "👤", 
                fontSize = 24.sp
            )
        },
        selected = selected,
        currentVibe = currentVibe,
        onClick = onClick
    )
}
