package com.specialprojects.experiments.envelopecall.ui.onboarding

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.DownloadManager
import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.rd.PageIndicatorView
import com.specialprojects.experiments.envelopecall.EnvelopeCallApp
import com.specialprojects.experiments.envelopecall.FileDownloader
import com.specialprojects.experiments.envelopecall.PDF_URL
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.call.CallActivity
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class OnboardingActivity: AppCompatActivity() {
    private val REQUEST_CODE_SET_DEFAULT_DIALER: Int = 0x1

    private val viewPager by bindView<ViewPager2>(R.id.viewPager)
    private val pageIndicatorView by bindView<PageIndicatorView>(R.id.pageIndicatorView)
    private val countdownView by bindView<TextView>(R.id.countdown)

    private var countDown = 11000
    private val delayInterval = 1000

    private val handler = Handler()

    private var alpha = 1F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val secondPass = intent.getBooleanExtra("second_pass", false)

        if (!secondPass) {
            val entries = resources.getStringArray(R.array.onboarding_entries)

            pageIndicatorView.count = entries.size

            viewPager.adapter = OnboardingAdapter().apply {
                changeData(entries.toList())
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    pageIndicatorView.selection = position

                    if (position == 3) {
                        offerReplacingDefaultDialer()
                    } else if (position == 4) {
                        startLockTask()
                    } else if (position == pageIndicatorView.count - 1) {
                        startCountdown()
                    }
                }
            })
        } else {
            val entries = resources.getStringArray(R.array.onboarding_second_pass_entries)

            pageIndicatorView.count = entries.size

            viewPager.adapter = OnboardingAdapter().apply {
                changeData(entries.toList())
            }

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    pageIndicatorView.selection = position

                    if (position == 2 && !isAppPinned()) {
                        startLockTask()
                    } else if (position == pageIndicatorView.count - 1) {
                        startCountdown()
                    }
                }
            })
        }
    }

    fun startCountdown() {
        viewPager.isUserInputEnabled = false
        pageIndicatorView.visibility = View.INVISIBLE
        countdownView.visibility = View.VISIBLE

        handler.post(countDownProcess)

        EnvelopeCallApp.obtain(this).onboardingPreference.set(true)

        ObjectAnimator.ofFloat(viewPager, "alpha", alpha, 0F).apply {
            duration = 11000
        }.start()
    }

    private val countDownProcess by lazy {
        object : Runnable {
            override fun run() {
                countDown -= delayInterval
                val seconds = (countDown / 1000)

                alpha -= 0.1F

                countdownView.text = "$seconds"
                viewPager.alpha = alpha

                if (countDown > 0) {
                    handler.postDelayed(this, delayInterval.toLong())
                } else {
                    startActivity(Intent(this@OnboardingActivity, CallActivity::class.java))
                    finish()
                }
            }
        }
    }

    fun isAppPinned(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_PINNED
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val message = when (resultCode) {
            RESULT_OK -> "User accepted request to become default dialer"
            RESULT_CANCELED -> "User declined request to become default dialer"
            else -> "Unexpected result code $resultCode"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("WrongConstant")
    private fun offerReplacingDefaultDialer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
            startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER)
        } else {
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
    }

    fun onLinkClicked() {
        FileDownloader.maybeStartDownload(this, PDF_URL)
        Toast.makeText(this, "Starting download", Toast.LENGTH_LONG).show()
    }

    private val downloadReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS))
            }
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