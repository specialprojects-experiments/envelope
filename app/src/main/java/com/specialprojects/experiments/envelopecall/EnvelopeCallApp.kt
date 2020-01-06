package com.specialprojects.experiments.envelopecall

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.*
import com.specialprojects.experiments.envelopecall.audio.SoundPoolHolder
import com.specialprojects.experiments.envelopecall.prefs.BooleanPreference
import com.specialprojects.experiments.envelopecall.prefs.LongPreference
import com.specialprojects.experiments.envelopecall.telephony.CallState
import timber.log.Timber

class EnvelopeCallApp: Application(), LifecycleObserver {
    lateinit var onboardingPreference: BooleanPreference
    lateinit var usagePreference: LongPreference
    val callState = MutableLiveData<CallState>()

    var foregroundState = true

    override fun onCreate() {
        super.onCreate()

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        onboardingPreference =
            BooleanPreference(
                preferenceManager,
                "completedOnboarding"
            )

        usagePreference =
            LongPreference(
                preferenceManager,
                "usageAccess"
            )

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        SoundPoolHolder.init()
        SoundPoolHolder.loadSounds(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    fun appendUsage(usage: Long) {
        val current = usagePreference.get()
        usagePreference.set(current + usage)
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

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        foregroundState = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        foregroundState = true
    }
}