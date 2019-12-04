package com.specialprojects.experiments.envelopecall

import android.app.Application
import android.telecom.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

class EnvelopeCallApp: Application() {
    val newCall = MutableLiveData<Call>()
    val callState = MutableLiveData<Int>()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}