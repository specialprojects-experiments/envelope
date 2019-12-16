package com.specialprojects.experiments.envelopecall.ui.onboarding

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.DownloadManager
import android.app.role.RoleManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.rd.PageIndicatorView
import com.specialprojects.experiments.envelopecall.EnvelopeCallApp
import com.specialprojects.experiments.envelopecall.FileDownloader
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.CountdownActivity
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class OnboardingActivity: AppCompatActivity() {
    private val REQUEST_CODE_SET_DEFAULT_DIALER: Int = 0x1

    private val viewPager by bindView<ViewPager2>(R.id.viewPager)
    private val pageIndicatorView by bindView<PageIndicatorView>(R.id.pageIndicatorView)

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
                    } else if (position == 4 && !isAppPinned()) {
                        startLockTask()
                    } else if (position == pageIndicatorView.count - 1) {
                        EnvelopeCallApp.obtain(this@OnboardingActivity).onboardingPreference.set(true)
                        Handler().postDelayed({
                            startActivity(Intent(this@OnboardingActivity, CountdownActivity::class.java))
                            finish()
                        }, 1500)
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

                    if (position == 1 && !isAppPinned()) {
                        startLockTask()
                    } else if (position == pageIndicatorView.count - 1) {
                        Handler().postDelayed({
                            startActivity(Intent(this@OnboardingActivity, CountdownActivity::class.java))
                            finish()
                        }, 2000)
                    }
                }
            })
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
        FileDownloader.maybeStartDownload(this,"https://s3-eu-west-1.amazonaws.com/media.designersfriend.co.uk/sps/media/uploads/misc/downloads/google-unplugged-envelope-instructions.pdf")
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