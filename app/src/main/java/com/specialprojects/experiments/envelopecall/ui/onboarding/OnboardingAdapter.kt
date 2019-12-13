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

    class ViewHolder(
        view: View
    ): BindableAdapter.ViewHolder<String>(view) {
        override fun bind(item: String, position: Int)  {

            (itemView as TextView).apply {
                if (position == 1 && item.length > 115) {
                    val string = SpannableString(item)
                    string.setSpan(CustomClickableSpan(), 111, 116, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    text = string
                    movementMethod = LinkMovementMethod.getInstance()
                } else {
                    text = item
                }
            }
        }
    }
}

class CustomClickableSpan: ClickableSpan() {
    override fun onClick(view: View) {
        (view.context as OnboardingActivity).onLinkClicked()
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = false
        ds.color = Color.BLACK
        ds.bgColor = Color.WHITE
    }
}