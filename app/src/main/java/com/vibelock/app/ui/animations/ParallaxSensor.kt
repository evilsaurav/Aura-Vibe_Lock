package com.vibelock.app.ui.animations

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberParallaxOffset(): Offset {
    val context = LocalContext.current
    var offset by remember { mutableStateOf(Offset.Zero) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            private var lastX = 0f
            private var lastY = 0f
            private val alpha = 0.1f // Low-pass filter smoothing factor

            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    
                    // orientation[1] is pitch (x-axis), orientation[2] is roll (y-axis)
                    val pitch = orientation[1]
                    val roll = orientation[2]

                    // Apply Low-pass filter to smooth the values
                    lastX = lastX + alpha * (roll - lastX)
                    lastY = lastY + alpha * (pitch - lastY)

                    // Convert to generic Offset multiplier (-1f to 1f approx)
                    // Clamp at ±0.5 radians to prevent nauseating huge shifts
                    val clampedX = lastX.coerceIn(-0.5f, 0.5f) * 2f
                    val clampedY = lastY.coerceIn(-0.5f, 0.5f) * 2f

                    offset = Offset(clampedX, clampedY)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    return offset
}
