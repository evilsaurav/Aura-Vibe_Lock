package com.vibelock.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float = 1f,
    val color: Color
)

@Composable
fun ParticleCannon(
    modifier: Modifier = Modifier,
    triggerTime: Long, // Pass a new System.currentTimeMillis() to trigger the burst
    originX: Float,
    originY: Float,
    particleColor: Color = Color(0xFF39FF14) // Neon Green Default
) {
    val particles = remember { mutableStateListOf<Particle>() }
    
    // Physical constants from specification
    val gravity = 0.3f
    val friction = 0.98f
    val maxLifeSpanMs = 1500f // 1.5 seconds

    LaunchedEffect(triggerTime) {
        if (triggerTime == 0L) return@LaunchedEffect
        
        // Spawn 30 particles on trigger
        particles.clear()
        for (i in 0 until 30) {
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val speed = Random.nextDouble(5.0, 25.0)
            particles.add(
                Particle(
                    x = originX,
                    y = originY,
                    vx = (Math.cos(angle) * speed).toFloat(),
                    vy = (Math.sin(angle) * speed).toFloat(),
                    color = particleColor
                )
            )
        }

        val startTime = System.currentTimeMillis()
        
        while (true) {
            withFrameNanos {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed > maxLifeSpanMs) {
                    particles.clear()
                } else {
                    val fadeFactor = 1f - (elapsed / maxLifeSpanMs)
                    
                    particles.forEach { particle ->
                        particle.x += particle.vx
                        particle.y += particle.vy
                        
                        // Apply gravity and friction
                        particle.vy += gravity
                        particle.vx *= friction
                        particle.vy *= friction
                        
                        // Fade out alpha
                        particle.alpha = fadeFactor.coerceIn(0f, 1f)
                    }
                }
            }
            if (particles.isEmpty()) break
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha),
                radius = 8f,
                center = Offset(particle.x, particle.y)
            )
        }
    }
}
