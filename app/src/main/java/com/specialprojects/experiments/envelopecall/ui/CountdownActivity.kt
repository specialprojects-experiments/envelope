package com.specialprojects.experiments.envelopecall.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.call.CallActivity
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class CountdownActivity: AppCompatActivity() {
    private var countDown = 11000
    private val delayInterval = 1000

    private val handler = Handler()

    private var alpha = 1F

    private val instructionsView by bindView<TextView>(R.id.instructions)
    private val countdownView by bindView<TextView>(R.id.countdown)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown)

        handler.post(countDownProcess)

        ObjectAnimator.ofFloat(instructionsView, "alpha", alpha, 0F).apply {
            duration = 11000
        }.start()
    }

    override fun onStop() {
        super.onStop()

        handler.removeCallbacks(countDownProcess)
    }

    private val countDownProcess by lazy {
        object : Runnable {
            override fun run() {
                countDown -= delayInterval
                val seconds = (countDown / 1000)

                alpha -= 0.1F

                countdownView.text = "$seconds"
                instructionsView.alpha = alpha

                if (countDown > 0) {
                    handler.postDelayed(this, delayInterval.toLong())
                } else {
                    startActivity(Intent(this@CountdownActivity, CallActivity::class.java))
                    finish()
                }
            }
        }
    }
}