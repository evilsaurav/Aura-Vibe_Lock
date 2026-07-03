package com.vibelock.app.haptics

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.sin

class VibeSynthEngine {

    private val sampleRate = 44100

    private fun playTone(generateWave: (Int) -> Double, durationMs: Int) {
        Thread {
            val numSamples = (durationMs * sampleRate) / 1000
            val generatedSnd = ByteArray(2 * numSamples)

            for (i in 0 until numSamples) {
                val sample = generateWave(i)
                val normalized = (sample * 32767).toInt()
                generatedSnd[2 * i] = (normalized and 0x00ff).toByte()
                generatedSnd[2 * i + 1] = ((normalized and 0xff00) ushr 8).toByte()
            }

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(generatedSnd.size)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
            
            Thread.sleep(durationMs.toLong())
            audioTrack.release()
        }.start()
    }

    fun playClack() {
        // High frequency click (sharp decay)
        playTone({ i ->
            val time = i / sampleRate.toDouble()
            val env = Math.exp(-time * 50.0) // Fast decay
            sin(2.0 * Math.PI * 800.0 * time) * env
        }, 50)
    }

    fun playPop() {
        // Medium frequency pop (slightly longer decay)
        playTone({ i ->
            val time = i / sampleRate.toDouble()
            val env = Math.exp(-time * 30.0)
            sin(2.0 * Math.PI * 400.0 * time) * env
        }, 100)
    }

    fun playBassDrop() {
        // Low frequency sweep drop
        playTone({ i ->
            val time = i / sampleRate.toDouble()
            val env = Math.exp(-time * 5.0)
            val freq = 150.0 * Math.exp(-time * 10.0) // Sweep from 150Hz down
            sin(2.0 * Math.PI * freq * time) * env
        }, 400)
    }
}
