package com.specialprojects.experiments.envelopecall

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.MutableLiveData
import com.specialprojects.experiments.envelopecall.sensor.BooleanPreference
import com.specialprojects.experiments.envelopecall.telephony.CallState
import timber.log.Timber

class EnvelopeCallApp: Application() {
    lateinit var onboardingPreference: BooleanPreference
    val callState = MutableLiveData<CallState>()

    override fun onCreate() {
        super.onCreate()

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        onboardingPreference = BooleanPreference(preferenceManager, "completedOnboarding")

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        @JvmStatic
        fun obtain(context: Context): EnvelopeCallApp {
            return context.applicationContext as EnvelopeCallApp
        }
    }

}