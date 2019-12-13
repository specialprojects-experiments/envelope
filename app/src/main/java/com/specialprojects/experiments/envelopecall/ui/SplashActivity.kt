package com.specialprojects.experiments.envelopecall.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.EnvelopeCallApp
import com.specialprojects.experiments.envelopecall.sensor.BooleanPreference
import com.specialprojects.experiments.envelopecall.ui.call.CallActivity
import com.specialprojects.experiments.envelopecall.ui.onboarding.OnboardingActivity

class SplashActivity: AppCompatActivity() {
    lateinit var onboardingPreference: BooleanPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onboardingPreference = EnvelopeCallApp.obtain(this).onboardingPreference

        if (!onboardingPreference.get()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        finish()
    }
}