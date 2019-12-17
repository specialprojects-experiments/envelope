package com.specialprojects.experiments.envelopecall.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.EnvelopeCallApp
import com.specialprojects.experiments.envelopecall.R
import timber.log.Timber

class StatisticsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        findViewById<TextView>(R.id.description).apply {
            text = SpannableString("Before leaving please reset your phone as the default phone app by clicking here and selecting Advanced > Phone App > Set Phone (system default)").apply {
                setSpan(CustomClickableSpan(), 76, 80, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            movementMethod = LinkMovementMethod.getInstance()
        }

        findViewById<TextView>(R.id.usage).apply {
            val usageSeconds = EnvelopeCallApp.obtain(this@StatisticsActivity).usagePreference.get()

            Timber.d("Usage in seconds: $usageSeconds")

            val minutes = usageSeconds / 60

            val hours = minutes / 60

            val usageText = if (hours == 0L) "$minutes minute${if(minutes > 1) "s" else ""}" else "$hours hour${if(hours > 1) "s" else ""}"
            text = getString(R.string.usage, usageText)
        }
    }

    class CustomClickableSpan: ClickableSpan() {
        override fun onClick(view: View) {
            view.context.startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", view.context.packageName, null)
            })
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
            ds.bgColor = Color.BLACK
            ds.color = Color.WHITE
        }
    }
}