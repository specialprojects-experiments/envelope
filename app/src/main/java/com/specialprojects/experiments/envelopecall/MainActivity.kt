package com.specialprojects.experiments.envelopecall

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private val REQUEST_CALL_PHONE: Int = 0x2

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

        return ObjectAnimator.ofFloat(button, "alpha", 0F).apply {
            duration = 250
            addListener(onEnd = {
                button.alpha = 1F
            })
        }
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

    private fun defaultCallHandle() {
        val callButton = findViewById<Button>(R.id.call)
        val numberView = findViewById<TextView>(R.id.number)

        callButton.setOnClickListener {
            val number = numberView.text.toString()
            Timber.d("Calling number: $number")
            val uri = "tel:$number".toUri()

            if (checkPermissions()) {
                startActivity(Intent(Intent.ACTION_CALL, uri))
            }
        }
    }

    private fun checkPermissions(): Boolean {
        Timber.d(" Checking permissions.")

        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED) {
            Timber.i("Call Phone permissions has NOT been granted. Requesting permissions.")
            requestPermissions()
            false
        } else {
            Timber.i("Contact permissions have already been granted")
            true
        }
    }

    private fun requestPermissions() { // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            Timber.i("Displaying call permission rationale to provide additional context.")
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
        }
    }

    private fun offerReplacingDefaultDialer() {
        val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        if (telecomManager.defaultDialerPackage !== packageName) {
            val changeDialer = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            changeDialer.putExtra(
                TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
                packageName
            )
            startActivity(changeDialer)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CALL_PHONE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private var currentAnimation: Animator? = null

    private fun createPulseAnimation() {
        val callButton = findViewById<Button>(R.id.call)

        currentAnimation = ObjectAnimator.ofFloat(callButton, "alpha", 0F).apply {
            duration = 300
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
        }

        currentAnimation?.addListener(onEnd = {
            callButton.alpha = 1F
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val callButton = findViewById<Button>(R.id.call)

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

        defaultCallHandle()

        (applicationContext as EnvelopeCallApp).callState.observe(this, Observer { callState ->
            when(callState) {
                CallState.Default -> {
                    (callButton.background as LevelListDrawable).level = 0
                    currentAnimation?.end()
                    callButton.alpha = 1F
                    defaultCallHandle()
                }
                is CallState.Ringing -> {
                    createPulseAnimation()

                    (callButton.background as LevelListDrawable).level = 1
                    callButton.setOnClickListener {
                        callState.call.answer(VideoProfile.STATE_AUDIO_ONLY)
                        currentAnimation?.end()
                    }
                    callButton.setOnLongClickListener {
                        callState.call.disconnect(); currentAnimation?.end(); true

                    }
                    currentAnimation?.start()
                }
                is CallState.Dialing -> {
                    createPulseAnimation()

                    (callButton.background as LevelListDrawable).level = 1
                    callButton.setOnClickListener {
                        callState.call.answer(VideoProfile.STATE_AUDIO_ONLY)
                        currentAnimation?.end()
                    }
                    callButton.setOnLongClickListener {
                        callState.call.disconnect(); currentAnimation?.end(); true

                    }
                    currentAnimation?.start()
                }
                is CallState.Active -> {
                    (callButton.background as LevelListDrawable).level = 2
                    callButton.setOnClickListener {
                        callState.call.disconnect()
                    }

                    currentAnimation?.end()
                }
            }
        })

        offerReplacingDefaultDialer()

        findViewById<Button>(R.id.clock).setOnClickListener {
            playAnimation()
        }
    }
}