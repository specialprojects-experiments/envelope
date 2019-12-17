package com.specialprojects.experiments.envelopecall.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.FileDownloader
import com.specialprojects.experiments.envelopecall.PDF_URL
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.onboarding.OnboardingActivity
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class HomeActivity: AppCompatActivity() {
    private val helpView by bindView<TextView>(R.id.help)
    private val useView by bindView<TextView>(R.id.use_envelope)
    private val makeEnvelopeView by bindView<TextView>(R.id.making_envelope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        helpView.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        useView.text = SpannableString("Use Envelope\nnow").apply {
            setSpan(BackgroundColorSpan(Color.WHITE), 0, "Use Envelope\nnow".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        useView.setOnClickListener {
            startActivity(Intent(this, OnboardingActivity::class.java).apply {
                putExtra("second_pass", true)
            })
        }

        makeEnvelopeView.text = SpannableString("Print an\nEnvelope").apply {
            setSpan(BackgroundColorSpan(Color.WHITE), 0, "Print an\nEnvelope".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        makeEnvelopeView.setOnClickListener {
            FileDownloader.maybeStartDownload(this, PDF_URL)
            Toast.makeText(this, "Starting download", Toast.LENGTH_LONG).show()
        }
    }
}