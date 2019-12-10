package com.specialprojects.experiments.envelopecall

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.specialprojects.experiments.envelopecall.telephony.CallState
import timber.log.Timber

class EnvelopeCallApp: Application() {
    val callState = MutableLiveData<CallState>()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}