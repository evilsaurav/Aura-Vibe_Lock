package com.vibelock.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HolographicBorderContainer(
    modifier: Modifier = Modifier,
    borderWidth: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(32.dp),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "holographic_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_anim"
    )

    val holographicColors = listOf(
        Color(0xFFB026FF), // Neon Purple
        Color(0xFF39FF14), // Neon Green
        Color(0xFFFFD700), // Gold
        Color(0xFF00E5FF), // Cyan
        Color(0xFFB026FF)  // Back to Purple
    )

    Box(
        modifier = modifier
            .clip(shape)
            .drawWithContent {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw the animated gradient behind
                rotate(rotation) {
                    drawRect(
                        brush = Brush.sweepGradient(holographicColors),
                        size = size.copy(width = canvasWidth * 1.5f, height = canvasHeight * 1.5f)
                    )
                }
                
                // Draw the content layer
                drawContent()
            }
            .padding(borderWidth)
            .clip(shape)
            .background(Color(0xFF121212)) // Inner dark surface
    ) {
        content()
    }
}
