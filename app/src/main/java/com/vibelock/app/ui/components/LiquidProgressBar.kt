package com.vibelock.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun LiquidProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = Color(0xFF00E5FF),
    backgroundColor: Color = Color(0xFF1A1A1A)
) {
    var time by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val startTime = System.nanoTime()
        while (true) {
            withInfiniteAnimationFrameNanos { frameTime ->
                time = (frameTime - startTime) / 1_000_000_000f // time in seconds
            }
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000)
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val w = size.width
        val h = size.height

        // Draw background
        drawRoundRect(
            color = backgroundColor,
            size = Size(w, h),
            cornerRadius = CornerRadius(h / 2f)
        )

        if (animatedProgress > 0f) {
            val fillWidth = w * animatedProgress
            val path = Path()

            path.moveTo(0f, h)
            path.lineTo(0f, 0f)

            // Draw wave on the leading edge
            val waveAmplitude = if (animatedProgress > 0.9f) h * 0.15f else h * 0.05f
            val waveFreq = 10f

            for (y in 0..h.toInt()) {
                val waveOffset = sin((y / h) * waveFreq + (time * 5f)) * waveAmplitude
                val x = (fillWidth + waveOffset).coerceIn(0f, w)
                path.lineTo(x, y.toFloat())
            }

            path.lineTo(0f, h)
            path.close()

            // Draw glow if almost full
            if (animatedProgress > 0.9f) {
                drawPath(
                    path = path,
                    color = fillColor.copy(alpha = 0.5f) // Glow base
                )
            }
            
            drawPath(
                path = path,
                color = fillColor
            )
        }
    }
}
