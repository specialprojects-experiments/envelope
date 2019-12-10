package com.specialprojects.experiments.envelopecall

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.telecom.Call
import android.telecom.InCallService
import androidx.core.app.NotificationCompat
import timber.log.Timber


class CallService: InCallService() {
    override fun onCallAdded(call: Call) {
        call.registerCallback(callback)
        Timber.d("onCallAdded()")
        super.onCallAdded(call)

        if (call.state == Call.STATE_RINGING) {
            (applicationContext as EnvelopeCallApp).callState.postValue(CallState.Ringing(call))
            postNotification()
        }
    }

    private val YOUR_CHANNEL_ID: String = "calls"

    lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun removeNotification() {
        notificationManager.cancel("calling", 2)
    }

    fun postNotification() {
        val channel = NotificationChannel(YOUR_CHANNEL_ID, "Incoming Calls", NotificationManager.IMPORTANCE_MAX)

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        channel.setSound(ringtoneUri, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )

        notificationManager.createNotificationChannel(channel)

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.flags = Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.setClass(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 1, intent, 0)

        val builder = NotificationCompat.Builder(this, YOUR_CHANNEL_ID).apply {
            setOngoing(true)
            setContentIntent(pendingIntent)
            setFullScreenIntent(pendingIntent, true)
        }

        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        builder.setContentTitle("New call")
        builder.setContentText("New call")

        notificationManager.notify("calling", 2, builder.build())
    }

    private val callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, newState: Int) {
            Timber.d("Call ${call.details}")
            Timber.d(newState.asString())

            (applicationContext as EnvelopeCallApp).callState.postValue(
                when(newState) {
                    Call.STATE_ACTIVE -> CallState.Active(call)
                    Call.STATE_RINGING -> CallState.Ringing(call)
                    Call.STATE_DIALING -> CallState.Dialing(call)
                    Call.STATE_DISCONNECTED -> CallState.Default
                    else -> CallState.Default
                })
        }
    }

    override fun onCallRemoved(call: Call) {
        Timber.d("onCallRemoved()")
        super.onCallRemoved(call)
        call.unregisterCallback(callback)

        removeNotification()
    }
}