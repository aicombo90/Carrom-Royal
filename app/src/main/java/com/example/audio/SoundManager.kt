package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class SoundManager private constructor(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val sampleRate = 22050

    // Pre-generated PCM audio buffers for instantaneous playback
    private val woodHitLow = generateWoodHit(420f, 0.05f)
    private val woodHitMid = generateWoodHit(680f, 0.04f)
    private val woodHitHigh = generateWoodHit(1100f, 0.035f)
    private val cushionThud = generateThud(180f, 0.07f)
    private val pocketChime = generateChime(listOf(880f, 1320f, 1760f), 0.16f)
    private val foulTone = generateFoul(160f, 0.2f)
    private val victoryJingle = generateVictory()
    private val buttonClick = generateWoodHit(950f, 0.02f)

    private fun generateWoodHit(freq: Float, durationSec: Float): ShortArray {
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 90.0).toFloat()
            val noise = (Random.nextFloat() * 2f - 1f) * 0.25f
            val wave = sin(2.0 * PI * freq * t).toFloat() * 0.75f + noise
            val sample = (wave * decay * Short.MAX_VALUE * 0.85f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateThud(freq: Float, durationSec: Float): ShortArray {
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 45.0).toFloat()
            val wave = sin(2.0 * PI * freq * t).toFloat()
            val sample = (wave * decay * Short.MAX_VALUE * 0.9f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateChime(freqs: List<Float>, durationSec: Float): ShortArray {
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 18.0).toFloat()
            var wave = 0f
            for (f in freqs) {
                wave += sin(2.0 * PI * f * t).toFloat()
            }
            wave /= freqs.size
            val sample = (wave * decay * Short.MAX_VALUE * 0.8f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateFoul(freq: Float, durationSec: Float): ShortArray {
        val numSamples = (sampleRate * durationSec).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val decay = exp(-t * 12.0).toFloat()
            val wave = (sin(2.0 * PI * freq * t) + sin(2.0 * PI * (freq * 1.414f) * t)) * 0.5f
            val sample = (wave * decay * Short.MAX_VALUE * 0.8f).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    private fun generateVictory(): ShortArray {
        val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
        val noteDur = 0.08f
        val totalSamples = (sampleRate * noteDur * notes.size).toInt()
        val buffer = ShortArray(totalSamples)
        for (n in notes.indices) {
            val freq = notes[n]
            val offset = (n * sampleRate * noteDur).toInt()
            val durSamples = (sampleRate * noteDur).toInt()
            for (i in 0 until durSamples) {
                val t = i.toFloat() / sampleRate
                val decay = exp(-t * 14.0).toFloat()
                val wave = sin(2.0 * PI * freq * t).toFloat()
                val sample = (wave * decay * Short.MAX_VALUE * 0.85f).toInt()
                val idx = offset + i
                if (idx < totalSamples) {
                    buffer[idx] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }
        }
        return buffer
    }

    private fun playPcm(buffer: ShortArray, volumeMultiplier: Float = 1.0f) {
        if (!soundEnabled) return
        scope.launch {
            try {
                val scaledBuffer = if (volumeMultiplier in 0.01f..0.99f) {
                    ShortArray(buffer.size) { i ->
                        (buffer[i] * volumeMultiplier).toInt().toShort()
                    }
                } else buffer

                val track = AudioTrack.Builder()
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
                    .setBufferSizeInBytes(scaledBuffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(scaledBuffer, 0, scaledBuffer.size)
                track.play()
                track.setNotificationMarkerPosition(scaledBuffer.size)
                track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onMarkerReached(t: AudioTrack?) {
                        try {
                            t?.stop()
                            t?.release()
                        } catch (_: Exception) {}
                    }
                    override fun onPeriodicNotification(t: AudioTrack?) {}
                })
            } catch (_: Exception) {}
        }
    }

    fun playDiscHit(intensity: Float = 1.0f) {
        val volume = intensity.coerceIn(0.15f, 1.0f)
        when {
            intensity > 0.7f -> playPcm(woodHitHigh, volume)
            intensity > 0.35f -> playPcm(woodHitMid, volume)
            else -> playPcm(woodHitLow, volume)
        }
        vibrateLight()
    }

    fun playStrikerHit(power: Float) {
        val volume = (power / 100f).coerceIn(0.3f, 1.0f)
        playPcm(woodHitHigh, volume)
        vibrateMedium()
    }

    fun playCushionBounce(intensity: Float = 0.8f) {
        playPcm(cushionThud, intensity.coerceIn(0.2f, 1.0f))
        vibrateLight()
    }

    fun playPocketSink() {
        playPcm(pocketChime, 1.0f)
        vibrateHeavy()
    }

    fun playFoul() {
        playPcm(foulTone, 1.0f)
        vibrateHeavy()
    }

    fun playVictory() {
        playPcm(victoryJingle, 1.0f)
        vibrateSuccess()
    }

    fun playClick() {
        playPcm(buttonClick, 0.4f)
        vibrateLight()
    }

    private fun vibrateLight() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateMedium() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(28)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateHeavy() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(55, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(55)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateSuccess() {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 30, 60, 45)
                val amps = intArrayOf(0, 180, 0, 240)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amps, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(70)
            }
        } catch (_: Exception) {}
    }

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context).also { INSTANCE = it }
            }
        }
    }
}
