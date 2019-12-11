package com.specialprojects.experiments.envelopecall.ui.onboarding

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.adapters.BindableAdapter

class OnboardingAdapter: BindableAdapter<String, BindableAdapter.ViewHolder<String>>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)

        return ViewHolder(inflater.inflate(R.layout.list_item_onboarding, viewGroup, false))
    }

    class ViewHolder(view: View): BindableAdapter.ViewHolder<String>(view) {
        override fun bind(item: String)  {
            val string = SpannableString(item)
            string.setSpan(CustomClickableSpan(), 10, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            (itemView as TextView).apply {
                text = string
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}

class CustomClickableSpan: ClickableSpan() {
    override fun onClick(view: View) {
        (view.context as AppCompatActivity).startLockTask()
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
        ds.color = Color.BLACK
        ds.bgColor = Color.WHITE
    }
}