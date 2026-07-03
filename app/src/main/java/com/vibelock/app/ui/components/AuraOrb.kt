package com.vibelock.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.vibelock.app.engine.AuraTier
import kotlin.math.sin

enum class OrbState {
    IDLE, CHARGED, DYING, LEVEL_UP, AURA_GOD
}

@Composable
fun AuraOrb(
    tier: AuraTier,
    orbState: OrbState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val baseScale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f)
    )

    val infiniteTransition = rememberInfiniteTransition()
    
    // Rotation duration based on state
    val rotationDuration = when(orbState) {
        OrbState.CHARGED -> 8000
        else -> 20000
    }
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(rotationDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Breathing scale
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val tierColor = Color(tier.colorHex)

    Box(
        modifier = modifier
            .size(220.dp)
            .scale(baseScale * breathe)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2.2f

            if (orbState == OrbState.AURA_GOD) {
                // Rainbow gradient rotation
                val godColors = listOf(
                    Color(0xFFEC4899), Color(0xFF8B5CF6),
                    Color(0xFF06B6D4), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEC4899)
                )
                rotate(rotation) {
                    drawCircle(
                        brush = Brush.sweepGradient(godColors, center = center),
                        radius = radius
                    )
                }
            } else {
                // Inner glow / Base
                val activeColor = if (orbState == OrbState.DYING) Color.Gray else tierColor
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(activeColor.copy(alpha = 0.8f), activeColor.copy(alpha = 0.2f)),
                        center = center,
                        radius = radius
                    ),
                    radius = radius
                )
                
                drawCircle(
                    color = activeColor,
                    radius = radius * 0.8f
                )
            }

            // Outer ring
            drawCircle(
                color = tierColor.copy(alpha = 0.4f),
                radius = radius * 1.05f,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
            )
            
            // If charged, draw orbiting particles
            if (orbState == OrbState.CHARGED) {
                rotate(-rotation * 2) {
                    for (i in 0 until 8) {
                        val angle = (i * 45).toFloat()
                        val cx = center.x + (radius * 1.15f) * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                        val cy = center.y + (radius * 1.15f) * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                        drawCircle(color = Color.White, radius = 6f, center = Offset(cx, cy))
                    }
                }
            }
        }
    }
}
