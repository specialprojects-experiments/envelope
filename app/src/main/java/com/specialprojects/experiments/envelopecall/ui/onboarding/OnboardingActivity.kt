package com.specialprojects.experiments.envelopecall.ui.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.rd.PageIndicatorView
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class OnboardingActivity: AppCompatActivity() {
    private val viewPager by bindView<ViewPager2>(R.id.viewPager)
    private val pageIndicatorView by bindView<PageIndicatorView>(R.id.pageIndicatorView)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val entries = resources.getStringArray(R.array.onboarding_entries)

        viewPager.adapter = OnboardingAdapter().apply {
            changeData(entries.toList())
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pageIndicatorView.selection = position
            }
        })
    }
}