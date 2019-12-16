package com.specialprojects.experiments.envelopecall

import android.app.Application
import android.content.Context
import android.media.SoundPool
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import com.specialprojects.experiments.envelopecall.audio.SoundPoolHolder
import com.specialprojects.experiments.envelopecall.prefs.BooleanPreference
import com.specialprojects.experiments.envelopecall.telephony.CallState
import timber.log.Timber

class EnvelopeCallApp: Application() {
    lateinit var onboardingPreference: BooleanPreference
    val callState = MutableLiveData<CallState>()

    override fun onCreate() {
        super.onCreate()

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        onboardingPreference =
            BooleanPreference(
                preferenceManager,
                "completedOnboarding"
            )

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        SoundPoolHolder.init()
        SoundPoolHolder.loadSounds(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()

        SoundPoolHolder.release()
    }

    companion object {
        @JvmStatic
        fun obtain(context: Context): EnvelopeCallApp {
            return context.applicationContext as EnvelopeCallApp
        }
    }

}