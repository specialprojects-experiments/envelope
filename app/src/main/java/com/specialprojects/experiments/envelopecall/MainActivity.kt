package com.specialprojects.experiments.envelopecall

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.LevelListDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.Handler
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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private val REQUEST_CALL_PHONE: Int = 0x2

    private val callBtnView by bindView<Button>(R.id.call)
    private val numberView by bindView<TextView>(R.id.number)
    private val clockBtnView by bindView<Button>(R.id.clock)

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

    private fun dialUpAnimation(id: Int) {
        val button = findViewById<Button>(id)
        button.isPressed = true
        button.isPressed = false
    }

    private val handler = Handler()

    private fun playAnimation() {
        dialUpAnimation(R.id.one)

        handler.postDelayed({
            dialUpAnimation(R.id.two)
        }, 300)

        handler.postDelayed({
            dialUpAnimation(R.id.three)
        }, 800)

        handler.postDelayed({
            dialUpAnimation(R.id.four)
            clockBtnView.isPressed = false
        }, 1100)
    }

    @SuppressLint("MissingPermission")
    private fun defaultCallHandle() {
        callBtnView.alpha = 0F

        numberView.text = ""

        callBtnView.apply {
            alpha = 0F
            setOnClickListener {
                val number = numberView.text.toString()
                Timber.d("Calling number: $number")
                val uri = "tel:$number".toUri()

                if (checkPermissions()) {
                    startActivity(Intent(Intent.ACTION_CALL, uri))
                }
            }
            setOnLongClickListener {
                numberView.text = ""
                true
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

        callBtnView.alpha = 1F

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

        numberView.addTextChangedListener(afterTextChanged = { result ->
            result?.let {
                callBtnView.alpha = if (it.isNotEmpty()) 1F else 0F
            }
        })

        defaultCallHandle()

        (applicationContext as EnvelopeCallApp).callState.observe(this, Observer { callState ->
            when(callState) {
                CallState.Default -> {
                    callBtnView.alpha = 0F
                    (callBtnView.background as TransitionDrawable).resetTransition()
                    currentAnimation?.end()
                    defaultCallHandle()
                }

                is CallState.Ringing -> {
                    createPulseAnimation()

                    callBtnView.apply {
                        setOnClickListener {
                            callState.call.answer(VideoProfile.STATE_AUDIO_ONLY)
                            currentAnimation?.end()
                        }
                        setOnLongClickListener {
                            callState.call.disconnect()
                            currentAnimation?.end()
                            true
                        }
                    }

                    currentAnimation?.start()
                }
                is CallState.Dialing -> {
                    callBtnView.apply {
                        setOnClickListener {
                            callState.call.disconnect()
                        }
                    }
                }
                is CallState.Active -> {
                    callBtnView.apply {
                        callBtnView.setBackgroundResource(R.drawable.call_button_state)
                        (background as TransitionDrawable).startTransition(300)
                        setOnClickListener {
                            callState.call.disconnect()
                        }
                    }

                    currentAnimation?.end()
                }
            }
        })

        offerReplacingDefaultDialer()

        clockBtnView.setOnClickListener {
            clockBtnView.isPressed = true
            //playAnimation()
        }
    }
}