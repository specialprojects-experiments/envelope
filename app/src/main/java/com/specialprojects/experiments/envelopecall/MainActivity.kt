package com.specialprojects.experiments.envelopecall

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.telecom.VideoProfile
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.net.toUri
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val idActionMap = mapOf(
        R.id.one to "1",
        R.id.two to "2",
        R.id.three to "3",
        R.id.four to "4",
        R.id.five to "5",
        R.id.six to "6",
        R.id.seven to "7",
        R.id.eight to "8",
        R.id.nine to "9",
        R.id.zero to "0",
        R.id.star to "*",
        R.id.hash to "#"
    )

    fun dialUpAnimation(id: Int): Animator {
        val button = findViewById<Button>(id)

        //button.isPressed = true

        val animator = ObjectAnimator.ofFloat(button, "alpha", 0F).apply {
            duration = 250
            addListener( object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animator: Animator) {

                }

                override fun onAnimationEnd(animator: Animator) {
                    //button.isPressed = false
                    button.alpha = 1F
                }

                override fun onAnimationCancel(animator: Animator) {

                }

                override fun onAnimationStart(animator: Animator) {
                    //button.isPressed = true
                }

            })
        }

        return animator
    }

    fun playAnimation() {
        AnimatorSet().apply {
            playSequentially(
                dialUpAnimation(R.id.one),
                dialUpAnimation(R.id.two),
                dialUpAnimation(R.id.three),
                dialUpAnimation(R.id.four)
            )
        }.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)


        val numberView = findViewById<TextView>(R.id.number)

        idActionMap.keys.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                numberView.append(idActionMap[id])
                Timber.d("Current number: ${numberView.text}")
            }
        }

        findViewById<Button>(R.id.dial_accept).setOnClickListener {
            val number = numberView.text.toString()
            Timber.d("Calling number: $number")
            val uri = "tel:$number".toUri()
            startActivity(Intent(Intent.ACTION_CALL, uri))
        }

        findViewById<Button>(R.id.hangup_refuse).setOnClickListener {
            playAnimation()
            //(applicationContext as EnvelopeCallApp).newCall.value?.disconnect()
        }

        /*
        findViewById<Button>(R.id.accept).setOnClickListener {
            (applicationContext as EnvelopeCallApp).newCall.value?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        (applicationContext as EnvelopeCallApp).newCall.observe(this, Observer {
            status.text = it.state.asString()
        })

        (applicationContext as EnvelopeCallApp).callState.observe(this, Observer {
            status.text = it.asString()
        })*/
    }
}