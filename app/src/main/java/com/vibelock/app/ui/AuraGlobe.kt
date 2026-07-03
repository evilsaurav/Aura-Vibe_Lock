package com.vibelock.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Brush

// 3D Point Data Class
data class Point3D(var x: Float, var y: Float, var z: Float)

@Composable
fun AuraGlobe(
    checkIns: List<com.vibelock.app.data.GlobalCheckIn> = emptyList(),
    modifier: Modifier = Modifier,
    globeColor: Color = Color(0xFF8B5CF6), // Default Cyberpunk Purple
    onGlobeDrag: (Float) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    
    // Rotation States
    var dragRotationX by remember { mutableStateOf(0f) }
    var dragRotationY by remember { mutableStateOf(0f) }
    
    val transition = rememberInfiniteTransition()
    val autoRotationY by transition.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(40000, easing = LinearEasing))
    )
    
    val rotationX = dragRotationX
    val rotationY = autoRotationY + dragRotationY

    // Generate Sphere Nodes (Latitude & Longitude grid)
    val nodes = remember {
        val points = mutableListOf<Point3D>()
        val lats = 12
        val lons = 24
        for (i in 0..lats) {
            val lat = Math.PI * i / lats - Math.PI / 2
            for (j in 0 until lons) {
                val lon = 2 * Math.PI * j / lons
                val x = (cos(lat) * cos(lon)).toFloat()
                val y = sin(lat).toFloat()
                val z = (cos(lat) * sin(lon)).toFloat()
                points.add(Point3D(x, y, z))
            }
        }
        points
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Drag karne par globe rotate hoga
                        dragRotationY += dragAmount.x * 0.01f
                        dragRotationX += dragAmount.y * 0.01f
                        
                        // Fire external drag callback (for custom haptic engine)
                        val dragVelocity = kotlin.math.sqrt(dragAmount.x * dragAmount.x + dragAmount.y * dragAmount.y)
                        onGlobeDrag(dragVelocity)
                        
                        // Tez ghoomane par micro-vibration feel hoga
                        if (kotlin.math.abs(dragAmount.x) > 10f || kotlin.math.abs(dragAmount.y) > 10f) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    }
                )
            }
    ) {
        val radius = size.minDimension / 2.5f
        val center = Offset(size.width / 2, size.height / 2)
        val projectedPoints = mutableListOf<Offset>()

        // 3D to 2D Projection Math
        nodes.forEach { node ->
            // Rotate around X axis
            val y1 = node.y * cos(rotationX) - node.z * sin(rotationX)
            val z1 = node.y * sin(rotationX) + node.z * cos(rotationX)

            // Rotate around Y axis
            val x2 = node.x * cos(rotationY) + z1 * sin(rotationY)
            val z2 = -node.x * sin(rotationY) + z1 * cos(rotationY)
            val y2 = y1

            // Simple Perspective Projection
            val distance = 2.5f
            val zScale = 1 / (distance - z2)
            
            val projectedX = center.x + (x2 * zScale * radius)
            val projectedY = center.y + (y2 * zScale * radius)
            
            projectedPoints.add(Offset(projectedX, projectedY))

            // Draw glowing dots (Particles)
            val alpha = ((z2 + 1f) / 2f).coerceIn(0.1f, 1f) // Peeche wale dots fade dikhenge
            drawCircle(
                color = globeColor.copy(alpha = alpha),
                radius = 4f * alpha,
                center = Offset(projectedX, projectedY)
            )
        }

        // Draw connections (Wireframe Matrix Look)
        val lons = 24
        for (i in 0 until projectedPoints.size) {
            // Horizontal lines
            if ((i + 1) % lons != 0) {
                drawLineSafe(projectedPoints[i], projectedPoints[i + 1], globeColor)
            }
            // Vertical lines
            if (i + lons < projectedPoints.size) {
                drawLineSafe(projectedPoints[i], projectedPoints[i + lons], globeColor)
            }
        }
        
        // Draw Global Check-Ins
        checkIns.forEach { checkIn ->
            val latRad = Math.toRadians(checkIn.lat)
            val lonRad = Math.toRadians(checkIn.lon)
            
            // Map to sphere coordinates
            val cx = (cos(latRad) * cos(lonRad)).toFloat()
            val cy = sin(latRad).toFloat()
            val cz = (cos(latRad) * sin(lonRad)).toFloat()
            
            // Rotate around X axis
            val cy1 = cy * cos(rotationX) - cz * sin(rotationX)
            val cz1 = cy * sin(rotationX) + cz * cos(rotationX)

            // Rotate around Y axis
            val cx2 = cx * cos(rotationY) + cz1 * sin(rotationY)
            val cz2 = -cx * sin(rotationY) + cz1 * cos(rotationY)
            val cy2 = cy1

            // Simple Perspective Projection
            val distance = 2.5f
            val zScale = 1 / (distance - cz2)
            
            val projectedX = center.x + (cx2 * zScale * radius)
            val projectedY = center.y + (cy2 * zScale * radius)
            
            // Front-facing elements are brighter
            val alpha = ((cz2 + 1f) / 2f).coerceIn(0.1f, 1f)
            
            val checkInColor = Color(checkIn.colorHex)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(checkInColor.copy(alpha = alpha), Color.Transparent),
                    center = Offset(projectedX, projectedY),
                    radius = 20f * alpha
                ),
                radius = 20f * alpha,
                center = Offset(projectedX, projectedY)
            )
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = 3f * alpha,
                center = Offset(projectedX, projectedY)
            )
        }
    }
}

// Safely draw lines mapping with alpha fading for 3D depth illusion
fun DrawScope.drawLineSafe(start: Offset, end: Offset, color: Color) {
    drawLine(
        color = color.copy(alpha = 0.2f),
        start = start,
        end = end,
        strokeWidth = 1.5f
    )
}
