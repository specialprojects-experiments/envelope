package com.specialprojects.experiments.envelopecall.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.specialprojects.experiments.envelopecall.R
import timber.log.Timber

object SoundPoolHolder {
    private var soundPool: SoundPool? = null

    private val tonePool = mutableMapOf<Int, Int>()

    private val idSoundMap = mapOf(
        R.id.one to R.raw.dtmf_1,
        R.id.two to R.raw.dtmf_2,
        R.id.three to R.raw.dtmf_3,
        R.id.four to R.raw.dtmf_4,
        R.id.five to R.raw.dtmf_5,
        R.id.six to R.raw.dtmf_6,
        R.id.seven to R.raw.dtmf_7,
        R.id.eight to R.raw.dtmf_8,
        R.id.nine to R.raw.dtmf_9,
        R.id.zero to R.raw.dtmf_0,
        R.id.star to R.raw.dtmf_star,
        R.id.hash to R.raw.dtmf_hash
    )

    private val clockSoundMap = mapOf(
        4 to R.raw.hour_1,
        5 to R.raw.hour_2,
        7 to R.raw.minutes_1,
        8 to R.raw.minutes_2
    )

    fun init() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()
    }

    fun loadSounds(context: Context) {
        tonePool.clear()
        tonePool.apply {
            soundPool?.let {
                val id = it.load(context, R.raw.unlock, 1)
                put(6, id)

                for(i in idSoundMap) {
                    val soundId = it.load(context, i.value, 1)
                    put(i.key, soundId)
                }

                for(i in clockSoundMap) {
                    val soundId = it.load(context, i.value, 1)
                    put(i.key, soundId)
                }
            }
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }

    fun playSound(id: Int){
        tonePool[id]?.let {
            Timber.d("Playing sound with id: $id")
            soundPool?.play(it, 1f, 1f, 1, 0, 1f)
        }
    }
}