package com.specialprojects.experiments.envelopecall.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.onboarding.OnboardingActivity
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class HelpActivity: AppCompatActivity() {
    private val setuoView by bindView<TextView>(R.id.setup_screens)
    private val privacyView by bindView<TextView>(R.id.privacy)
    private val envelopeView by bindView<TextView>(R.id.making_envelope)
    private val linkView by bindView<TextView>(R.id.link)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        findViewById<Button>(R.id.close).setOnClickListener {
            finish()
        }

        linkView.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("http://www.specialprojects.studio")
                }
            )
        }

        setuoView.apply {
            val string = SpannableString(text)
            string.setSpan(CustomClickableSpan(), 45, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = string
            movementMethod = LinkMovementMethod.getInstance()
        }

        privacyView.apply {
            val string = SpannableString(text)
            string.setSpan(CustomClickableSpan(), 82, 86, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = string
            movementMethod = LinkMovementMethod.getInstance()
        }

        envelopeView.apply {
            val string = SpannableString(text)
            string.setSpan(CustomClickableSpan(), 117, 121, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = string
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    class CustomClickableSpan: ClickableSpan() {
        override fun onClick(view: View) {
            with(view.context as AppCompatActivity) {
                when (view.id) {
                    R.id.setup_screens -> {
                        startActivity(
                            Intent(view.context, OnboardingActivity::class.java)
                        )
                        finish()
                    }
                    R.id.privacy -> {
                        startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://www.iubenda.com/privacy-policy/97790877")
                            }
                        )
                    }
                    R.id.making_envelope -> {
                        startActivity(
                            Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://www.dropbox.com/s/x47ks1d41bcgbhd/Google_Envelope_wireframesv3.pdf?dl=1")
                            }
                        )
                    }
                }
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.color = Color.BLACK
            ds.bgColor = Color.WHITE
        }
    }
}