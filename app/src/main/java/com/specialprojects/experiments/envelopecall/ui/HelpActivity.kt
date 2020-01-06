package com.specialprojects.experiments.envelopecall.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.FileDownloader
import com.specialprojects.experiments.envelopecall.PDF_URL
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.onboarding.OnboardingActivity
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class HelpActivity: AppCompatActivity() {
    private val setupView by bindView<TextView>(R.id.setup_screens)
    private val privacyView by bindView<TextView>(R.id.privacy)
    private val envelopeView by bindView<TextView>(R.id.making_envelope)
    private val permissionsView by bindView<TextView>(R.id.permissions)
    private val linkView by bindView<TextView>(R.id.link)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        findViewById<Button>(R.id.close).setOnClickListener {
            finish()
        }


        permissionsView.text = SpannableString("Envelope needs to become the default dialer on your phone. We recommend you do this only for the times you use the app, and reset the default dialer to the system one. You can do this now by clicking here and selecting Advanced > Phone App > Set Phone (system default).").apply {
            setSpan(
                object: ClickableSpan() {
                    override fun onClick(view: View) {
                        view.context.startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", view.context.packageName, null)
                        })
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                        ds.bgColor = Color.WHITE
                        ds.color = Color.BLACK
                    }
                },
                200,
                204,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        permissionsView.movementMethod = LinkMovementMethod.getInstance()

        linkView.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("http://www.specialprojects.studio")
                }
            )
        }

        setupView.apply {
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

    private val downloadReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
            }
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
                        FileDownloader.maybeStartDownload(this,
                            PDF_URL
                        )
                        Toast.makeText(this, "Starting download", Toast.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.color = Color.BLACK
            ds.bgColor = Color.WHITE
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        registerReceiver(downloadReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(downloadReceiver)
    }
}