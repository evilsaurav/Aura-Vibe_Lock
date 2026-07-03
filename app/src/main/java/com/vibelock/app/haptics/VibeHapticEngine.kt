package com.vibelock.app.haptics

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import android.os.Vibrator

class VibeHapticEngine(private val context: Context) {

    private val vibratorManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    } else {
        null
    }
    
    @Suppress("DEPRECATION")
    private val legacyVibrator = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    } else {
        null
    }

    private val synthEngine = VibeSynthEngine()

    init {
        // Synthesizer is ready mathematically.
    }

    fun triggerTouchDown() {
        // Haptic micro-tick
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            val combined = CombinedVibration.createParallel(effect)
            vibratorManager?.vibrate(combined)
        } else {
            legacyVibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        // Play synthesized clack sound
        synthEngine.playClack()
    }

    fun triggerTouchReleaseSuccess() {
        // Heavy pop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)
            val combined = CombinedVibration.createParallel(effect)
            vibratorManager?.vibrate(combined)
        } else {
            legacyVibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        // Play synthesized pop sound
        synthEngine.playPop()
    }

    fun triggerLevelUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK, 0.5f, 0)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.8f, 50)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, 100)
                .compose()
            val combined = CombinedVibration.createParallel(effect)
            vibratorManager?.vibrate(combined)
        } else {
            val timings = longArrayOf(0, 50, 50, 100)
            val amplitudes = intArrayOf(0, 128, 0, 255)
            legacyVibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        }
        // Play synthesized bass drop
        synthEngine.playBassDrop()
    }
    
    fun triggerGlobeSpin(velocity: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Scale intensity between 0.1 and 1.0 based on drag velocity
            val intensity = (kotlin.math.abs(velocity) / 50f).coerceIn(0.1f, 1.0f)
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_LOW_TICK, intensity, 0)
                .compose()
            vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
        } else {
            if (kotlin.math.abs(velocity) > 10f) {
                legacyVibrator?.vibrate(VibrationEffect.createOneShot(10, 50))
            }
        }
    }
    
    fun triggerVibeSelect(vibe: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 1.0f, 0)
                .compose()
            vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
        } else {
            legacyVibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    fun triggerJournalSubmit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_THUD, 0.8f, 0)
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, 0.5f, 100)
                .compose()
            vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
        } else {
            val timings = longArrayOf(0, 40, 60, 20)
            val amplitudes = intArrayOf(0, 200, 0, 100)
            legacyVibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        }
    }

    fun triggerAILoading() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val effect = VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_LOW_TICK, 0.3f, 0)
                .compose()
            vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
        } else {
            legacyVibrator?.vibrate(VibrationEffect.createOneShot(10, 30))
        }
    }

    fun release() {
        // No soundpool to release for synth
    }
}
