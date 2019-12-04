package com.specialprojects.experiments.envelopecall

import android.app.Application
import timber.log.Timber

class EnvelopeCallApp: Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}