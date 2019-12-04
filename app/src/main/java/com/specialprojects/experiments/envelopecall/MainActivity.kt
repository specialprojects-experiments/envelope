package com.specialprojects.experiments.envelopecall

import android.content.Intent
import android.os.Bundle
import android.telecom.VideoProfile
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val status = findViewById<TextView>(R.id.textView)

        findViewById<Button>(R.id.button).setOnClickListener {
            val uri = "tel:9144323432".toUri()
            startActivity(Intent(Intent.ACTION_CALL, uri))
        }

        findViewById<Button>(R.id.hangup).setOnClickListener {
            (applicationContext as EnvelopeCallApp).newCall.value?.disconnect()
        }

        findViewById<Button>(R.id.accept).setOnClickListener {
            (applicationContext as EnvelopeCallApp).newCall.value?.answer(VideoProfile.STATE_AUDIO_ONLY)
        }

        (applicationContext as EnvelopeCallApp).newCall.observe(this, Observer {
            status.text = it.state.asString()
        })

        (applicationContext as EnvelopeCallApp).callState.observe(this, Observer {
            status.text = it.asString()
        })
    }
}
