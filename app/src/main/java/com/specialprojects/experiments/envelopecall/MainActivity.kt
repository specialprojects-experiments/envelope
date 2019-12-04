package com.specialprojects.experiments.envelopecall

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    val PERMISSION_ALL = 1

    private val PERMISSIONS =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE)

    private var telecomManager: TelecomManager? = null
    private var handle: PhoneAccountHandle? = null
    private var phoneAccount: PhoneAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            makeCall("914432344")
        }

        setupPhoneAccount()

        if (!hasPermissions(this, PERMISSIONS.toList())) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }

        requestRole()
    }

    private val REQUEST_ID = 1

    fun requestRole() {
        val roleManager =
            getSystemService(Context.ROLE_SERVICE) as RoleManager
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        startActivityForResult(intent, REQUEST_ID)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ID) {
            if (resultCode == Activity.RESULT_OK) { // Your app is now the default dialer app
            } else { // Your app is not the default dialer app
            }
        }
    }

    private fun makeCall(to: String) {
        Timber.d("makeCall")
        val uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, to, null)

        val callInfoBundle = Bundle()
        callInfoBundle.putString(CALLEE, to)
        val callInfo = Bundle()
        callInfo.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, callInfoBundle)
        callInfo.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, handle)
        callInfo.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE, false)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        telecomManager!!.placeCall(uri, callInfo)
    }

    /*
    private fun endCall() {
        Timber.d( "endCall")
        val conn = VoiceConnectionService.getConnection()
        if (conn == null) {
            Toast.makeText(this, "No call exists for you to end", Toast.LENGTH_LONG)
                .show()
        } else {
            val cause = DisconnectCause(DisconnectCause.LOCAL)
            conn.setDisconnected(cause)
            conn.destroy()
            VoiceConnectionService.deinitConnection()
        }
    }*/

    fun hasPermissions(
        context: Context,
        permissions: List<String>
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    private fun setupPhoneAccount() {
        val appName = this.getString(R.string.app_name)
        handle = PhoneAccountHandle(
            ComponentName(
                this.applicationContext,
                VoiceConnectionService::class.java
            ), appName
        )
        telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        phoneAccount = PhoneAccount.Builder(handle, appName)
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
            .build()
        telecomManager?.registerPhoneAccount(phoneAccount)
    }
}
