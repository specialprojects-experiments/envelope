package com.specialprojects.experiments.envelopecall.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import timber.log.Timber
import java.nio.ByteBuffer
import kotlin.experimental.and

class DTMFPlayer {

    private val audioTrack: AudioTrack? = null

    fun init() {
        val buffsize = AudioTrack.getMinBufferSize(7168,
            AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)

        val audioTrack = AudioTrack(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
            AudioFormat.Builder()
                .setSampleRate(44100)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .build(), buffsize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE)

        audioTrack.setPlaybackPositionUpdateListener(object: AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(audioTrack: AudioTrack) {
                Timber.d("onMarkerReached()")
            }

            override fun onPeriodicNotification(audioTrack: AudioTrack) {
                Timber.d("onPeriodicNotification()")
            }
        })
        audioTrack.play()
    }

    fun playChar(char: Char) {
        writeSound(DTMF.generateDTMFTone(char))
    }

    private fun writeSound(samples: DoubleArray) {
        val generatedSnd: ByteArray = get16BitPcm(samples)
        audioTrack?.write(generatedSnd, 0, generatedSnd.size)
    }

    private fun get16BitPcm(samples: DoubleArray): ByteArray {
        val buffer = ByteBuffer.allocate(8 * samples.size)

        var index = 0

        for (sample in samples) {
            val maxSample = ((sample * Short.MAX_VALUE)).toShort()

            buffer.putShort(index++, (maxSample and 0x00ff))
            buffer.putShort(index++, ((maxSample and 0x00ff).toInt() ushr 8).toShort())
        }

        Timber.d("Sample index: $index")

        return buffer.array()
    }

    fun destroy() {
        audioTrack?.apply {
            stop()
            release()
        }
    }
}