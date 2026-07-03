package com.vibelock.app.ui.animations

import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    var size: Float,
    var color: Color,
    var lifetime: Long = 0L,
    val maxLifetime: Long = Random.nextLong(1000L, 3000L),
    var isDead: Boolean = false,
    // Chaos specific
    val chaosId: Int = Random.nextInt(100)
)

enum class VibeParticleConfig {
    GRIND, CHILL, CHAOS, COZY, SAD
}

@Composable
fun VibeParticleSystem(
    modifier: Modifier = Modifier,
    vibeConfig: VibeParticleConfig = VibeParticleConfig.CHILL
) {
    var particles by remember { mutableStateOf(listOf<Particle>()) }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    LaunchedEffect(vibeConfig, width, height) {
        if (width == 0f || height == 0f) return@LaunchedEffect

        // Determine properties based on vibe
        val targetCount = when (vibeConfig) {
            VibeParticleConfig.GRIND -> 40
            VibeParticleConfig.CHILL -> 20
            VibeParticleConfig.CHAOS -> 60
            VibeParticleConfig.COZY -> 30
            VibeParticleConfig.SAD -> 25
        }

        // Initialize particles
        particles = List(targetCount) { createParticle(vibeConfig, width, height) }

        var lastFrameTime = System.nanoTime()

        while (true) {
            withInfiniteAnimationFrameNanos { frameTimeNanos ->
                val dt = (frameTimeNanos - lastFrameTime) / 1_000_000f // Delta time in milliseconds
                lastFrameTime = frameTimeNanos

                particles = particles.map { p ->
                    var newP = updateParticle(p, dt, vibeConfig, width, height)
                    if (newP.isDead) {
                        newP = createParticle(vibeConfig, width, height)
                    }
                    newP
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        width = size.width
        height = size.height

        particles.forEach { p ->
            drawParticle(p, vibeConfig)
        }
    }
}

private fun createParticle(config: VibeParticleConfig, w: Float, h: Float): Particle {
    return when (config) {
        VibeParticleConfig.GRIND -> {
            Particle(
                x = Random.nextFloat() * w,
                y = h + Random.nextFloat() * 100f,
                vx = Random.nextFloat() * 0.2f - 0.1f, // Drift slightly L/R
                vy = -(Random.nextFloat() * 0.8f + 0.4f), // Fast upward
                alpha = Random.nextFloat() * 0.5f + 0.5f,
                size = Random.nextFloat() * 6f + 4f, // 2-4dp (mapped to px loosely)
                color = if (Random.nextBoolean()) Color(0xFFFF5722) else Color(0xFFFF9800),
                maxLifetime = Random.nextLong(1000L, 2000L)
            )
        }
        VibeParticleConfig.CHILL -> {
            Particle(
                x = Random.nextFloat() * w,
                y = Random.nextFloat() * h,
                vx = Random.nextFloat() * 0.05f - 0.025f,
                vy = Random.nextFloat() * 0.05f - 0.025f,
                alpha = 0f, // Fades in
                size = Random.nextFloat() * 15f + 10f, // Medium orbs
                color = if (Random.nextBoolean()) Color(0xFF3B82F6) else Color(0xFF8B5CF6),
                maxLifetime = Random.nextLong(4000L, 8000L)
            )
        }
        VibeParticleConfig.CHAOS -> {
            Particle(
                x = Random.nextFloat() * w,
                y = Random.nextFloat() * h,
                vx = 0f, vy = 0f,
                alpha = 1f,
                size = Random.nextFloat() * 12f + 4f,
                color = listOf(Color.Green, Color.Magenta, Color.Cyan, Color.White).random(),
                maxLifetime = Random.nextLong(50L, 300L) // Extremely short lived
            )
        }
        VibeParticleConfig.COZY -> {
            Particle(
                x = Random.nextFloat() * w,
                y = h + Random.nextFloat() * 100f,
                vx = Random.nextFloat() * 0.02f - 0.01f,
                vy = -(Random.nextFloat() * 0.1f + 0.05f), // Very slow upward
                alpha = 0f,
                size = Random.nextFloat() * 5f + 2f,
                color = Color(0xFFFFD700),
                maxLifetime = Random.nextLong(3000L, 6000L)
            )
        }
        VibeParticleConfig.SAD -> {
            Particle(
                x = Random.nextFloat() * w,
                y = -Random.nextFloat() * 100f,
                vx = 0f,
                vy = Random.nextFloat() * 0.6f + 0.3f, // Slow straight down
                alpha = Random.nextFloat() * 0.3f + 0.2f,
                size = 1f, // width handled in draw
                color = Color(0xFF1E3A8A), // Dark blue
                maxLifetime = 10000L // Dies off screen
            )
        }
    }
}

private fun updateParticle(p: Particle, dt: Float, config: VibeParticleConfig, w: Float, h: Float): Particle {
    p.lifetime += dt.toLong()
    if (p.lifetime >= p.maxLifetime) {
        p.isDead = true
        return p
    }

    p.x += p.vx * dt
    p.y += p.vy * dt

    // Out of bounds check
    if (p.x < -50f || p.x > w + 50f || p.y < -50f || p.y > h + 50f) {
        p.isDead = true
        return p
    }

    // Config specific updates (Alpha mostly)
    when (config) {
        VibeParticleConfig.GRIND -> {
            // Fade out sharp at end of life
            if (p.maxLifetime - p.lifetime < 300) {
                p.alpha = ((p.maxLifetime - p.lifetime) / 300f).coerceIn(0f, 1f)
            }
        }
        VibeParticleConfig.CHILL -> {
            // Soft glow in and out
            val halfLife = p.maxLifetime / 2f
            p.alpha = if (p.lifetime < halfLife) {
                (p.lifetime / halfLife).coerceIn(0f, 0.6f)
            } else {
                ((p.maxLifetime - p.lifetime) / halfLife).coerceIn(0f, 0.6f)
            }
        }
        VibeParticleConfig.CHAOS -> {
            // Glitchy pixels randomly teleporting is handled by short lifetime (re-creating particle)
        }
        VibeParticleConfig.COZY -> {
            // Twinkle effect (pulse alpha)
            val baseAlpha = if (p.lifetime < 1000) p.lifetime / 1000f else if (p.maxLifetime - p.lifetime < 1000) (p.maxLifetime - p.lifetime) / 1000f else 1f
            val pulse = (kotlin.math.sin(p.lifetime / 200.0) * 0.5 + 0.5).toFloat()
            p.alpha = (baseAlpha * pulse * 0.7f).coerceIn(0f, 1f)
        }
        VibeParticleConfig.SAD -> {
            // No fade, just fall
        }
    }

    return p
}

private fun DrawScope.drawParticle(p: Particle, config: VibeParticleConfig) {
    if (p.alpha <= 0f) return

    when (config) {
        VibeParticleConfig.SAD -> {
            // Rain drop (thin line)
            drawLine(
                color = p.color.copy(alpha = p.alpha),
                start = Offset(p.x, p.y),
                end = Offset(p.x, p.y - 15f), // tail length
                strokeWidth = 2f
            )
        }
        VibeParticleConfig.CHAOS -> {
            // Glitchy pixels (squares)
            drawRect(
                color = p.color.copy(alpha = p.alpha),
                topLeft = Offset(p.x, p.y),
                size = androidx.compose.ui.geometry.Size(p.size, p.size)
            )
        }
        else -> {
            // Default circles
            drawCircle(
                color = p.color.copy(alpha = p.alpha),
                radius = p.size / 2f,
                center = Offset(p.x, p.y)
            )
        }
    }
}
