package com.vibelock.app.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import coil.compose.SubcomposeAsyncImage
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.vibelock.app.R
import com.vibelock.app.engine.AuraTier
import com.vibelock.app.ui.theme.AuraColors
import com.vibelock.app.ui.theme.AuraShape
import com.vibelock.app.ui.theme.AuraSpacing
import com.vibelock.app.ui.theme.AuraTypography

@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    glowColor: Color = AuraColors.NeonPurple,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(AuraShape.Large)
            .background(AuraColors.BackgroundGlass)
            .border(
                width = 1.dp,
                color = glowColor.copy(alpha = 0.3f),
                shape = AuraShape.Large
            )
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = androidx.compose.ui.graphics.Paint().apply {
                        color = glowColor.copy(alpha = 0.15f)
                        asFrameworkPaint().maskFilter = BlurMaskFilter(40f, BlurMaskFilter.Blur.NORMAL)
                    }
                    canvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        AuraShape.Large.topStart.toPx(size, this@drawBehind),
                        AuraShape.Large.topStart.toPx(size, this@drawBehind),
                        paint
                    )
                }
            }
            .padding(AuraSpacing.m),
        content = content
    )
}

@Composable
fun NeonText(
    text: String,
    color: Color = AuraColors.NeonPurple,
    style: TextStyle = AuraTypography.HeadingL,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = color,
        style = style,
        modifier = modifier.drawBehind {
            drawIntoCanvas { canvas ->
                val paint = androidx.compose.ui.graphics.Paint().apply {
                    this.color = color.copy(alpha = 0.6f)
                    asFrameworkPaint().maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL)
                }
                // Native canvas shadow text drawing is complex in compose 
                // We'll apply it simply via standard Compose shadow or let the Text element's shadow parameter handle it
                // For a robust implementation, TextStyle(shadow) is better:
            }
        }
    )
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    color: Color = AuraColors.NeonPurple,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f)
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(AuraShape.Pill)
            .background(Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.7f))))
            .border(1.dp, color.copy(alpha = 0.5f), AuraShape.Pill)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = AuraSpacing.xl, vertical = AuraSpacing.m),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = AuraColors.TextOnNeon,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun AuraXPProgressBar(
    currentXP: Int,
    maxXP: Int,
    color: Color = AuraColors.NeonPurple,
    modifier: Modifier = Modifier
) {
    val progress = if (maxXP > 0) (currentXP.toFloat() / maxXP.toFloat()).coerceIn(0f, 1f) else 1f

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "$currentXP / $maxXP XP",
            style = AuraTypography.Label,
            color = AuraColors.TextSecondary,
            modifier = Modifier.align(Alignment.End)
        )
        Spacer(modifier = Modifier.height(AuraSpacing.s))
        com.vibelock.app.ui.components.LiquidProgressBar(
            progress = progress,
            fillColor = color
        )
    }
}

@Composable
fun AuraAvatar(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Avatar"
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageLoader = remember(context) {
        coil.ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(coil.decode.ImageDecoderDecoder.Factory())
                } else {
                    add(coil.decode.GifDecoder.Factory())
                }
            }
            .crossfade(true)
            .build()
    }

    coil.compose.SubcomposeAsyncImage(
        model = url,
        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
        loading = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = AuraColors.NeonPurple,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        },
        error = {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Text("?", color = Color.White)
            }
        }
    )
}

@Composable
fun AuraBotCompanion(
    vibe: String,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.aura_bot))
    
    val speed = when {
        vibe.contains("Grind", ignoreCase = true) -> 2.0f
        vibe.contains("Chaos", ignoreCase = true) -> 3.0f
        vibe.contains("Cozy", ignoreCase = true) -> 0.5f
        else -> 1.0f
    }
    
    val vibeColor = when {
        vibe.contains("Grind", ignoreCase = true) -> Color(0xFFFF5722)
        vibe.contains("Chill", ignoreCase = true) -> Color(0xFF3B82F6)
        vibe.contains("Chaos", ignoreCase = true) -> Color(0xFF8B5CF6)
        vibe.contains("Cozy", ignoreCase = true) -> Color(0xFFFFD700)
        vibe.contains("Dark", ignoreCase = true) -> Color(0xFF1E3A8A)
        else -> AuraColors.NeonCyan
    }

    val infiniteTransition = rememberInfiniteTransition(label = "aura_bot_idle")

    val levitation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = androidx.compose.animation.core.EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bot_levitation"
    )

    val breathing by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = androidx.compose.animation.core.EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bot_breathing"
    )

    val auraPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = androidx.compose.animation.core.FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bot_aura_pulse"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = levitation
                scaleX = breathing
                scaleY = breathing
            }, 
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(auraPulse)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(vibeColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )
        )
        
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = speed,
            modifier = Modifier.size(64.dp)
        )
        if (composition == null) {
            androidx.compose.material3.Text("🤖", fontSize = 32.sp)
        }
    }
}

@Composable
fun VibeButtonContent(vibe: String, isSelected: Boolean) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val textColor = if (isSelected) AuraColors.TextPrimary else AuraColors.NeonPurple
    
    // Chaos glitch offset
    val chaosOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Cozy pulse opacity
    val cozyPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Grind scale
    val grindScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var modifier = Modifier.padding(horizontal = 4.dp)
    
    if (isSelected) {
        when {
            vibe.contains("Chaos") -> modifier = modifier.offset(x = chaosOffset.dp)
            vibe.contains("Cozy") -> modifier = modifier.graphicsLayer { alpha = cozyPulse }
            vibe.contains("Grind") -> modifier = modifier.scale(grindScale)
        }
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.material3.Text(
            text = vibe, 
            color = textColor, 
            fontWeight = FontWeight.Bold
        )
    }
}

