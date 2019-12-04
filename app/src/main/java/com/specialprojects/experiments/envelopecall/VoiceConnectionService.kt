package com.specialprojects.experiments.envelopecall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.telecom.*
import android.telecom.Call.Details.PROPERTY_SELF_MANAGED
import android.telecom.DisconnectCause
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import timber.log.Timber


class VoiceConnectionService: ConnectionService() {
    private var connection: Connection? = null

    fun getConnection(): Connection? {
        return connection
    }

    fun deinitConnection() {
        connection = null
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection? {
        Timber.d("onCreateIncomingConnection()")
        val incomingCallConnection = createConnection(request)
        incomingCallConnection.setRinging()
        return incomingCallConnection
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        Timber.d("onCreateOutgoingConnectionFailed()")
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection? {
        Timber.d("onCreateOutgoingConnection()")
        val outgoingCallConnection = createConnection(request)
        outgoingCallConnection.connectionProperties = PROPERTY_SELF_MANAGED
        outgoingCallConnection.setDialing()
        return outgoingCallConnection
    }

    private fun createConnection(request: ConnectionRequest): Connection {
        connection = object : Connection() {
            override fun onStateChanged(state: Int) {
                if (state == STATE_DIALING) {
                    val handler = Handler()
                    handler.post { sendCallRequestToActivity(ACTION_OUTGOING_CALL) }
                }
            }

            override fun onCallAudioStateChanged(state: CallAudioState) {
                Timber.d("onCallAudioStateChanged called, current state is $state")
            }

            override fun onPlayDtmfTone(c: Char) {
                Timber.d("onPlayDtmfTone called with DTMF $c")
                val extras = Bundle()
                extras.putString(DTMF, c.toString())
                connection!!.extras = extras
                val handler = Handler()
                handler.post { sendCallRequestToActivity(ACTION_DTMF_SEND) }
            }

            override fun onDisconnect() {
                super.onDisconnect()
                connection!!.setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
                connection!!.destroy()
                connection = null
                val handler = Handler()
                handler.post { sendCallRequestToActivity(ACTION_DISCONNECT_CALL) }
            }

            override fun onSeparate() {
                super.onSeparate()
            }

            override fun onAbort() {
                super.onAbort()
                connection!!.setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
                connection!!.destroy()
            }

            override fun onAnswer() {
                super.onAnswer()
            }

            override fun onReject() {
                super.onReject()
                connection!!.setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
                connection!!.destroy()
            }

            override fun onPostDialContinue(proceed: Boolean) {
                super.onPostDialContinue(true)
            }
        }

        connection?.let {
            it.connectionCapabilities = Connection.CAPABILITY_MUTE
            if (request.extras.getString(CALLEE) == null) {
                it.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
            } else {
                it.setAddress(
                    Uri.parse(request.extras.getString(CALLEE)),
                    TelecomManager.PRESENTATION_ALLOWED
                )
            }
            it.setDialing()
            it.extras = request.extras
        }

        return connection!!
    }

    /*
     * Send call request to the VoiceConnectionServiceActivity
     */
    private fun sendCallRequestToActivity(action: String) {
        val intent = Intent(action)
        val extras = Bundle()
        when (action) {
            ACTION_OUTGOING_CALL -> {
                val address: Uri = connection!!.address
                extras.putString(OUTGOING_CALL_ADDRESS, address.toString())
                intent.putExtras(extras)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
            ACTION_DISCONNECT_CALL -> {
                extras.putInt("Reason", DisconnectCause.LOCAL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtras(extras)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
            ACTION_DTMF_SEND -> {
                val d = connection!!.extras.getString(DTMF)
                extras.putString(DTMF, connection!!.extras.getString(DTMF))
                intent.putExtras(extras)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
            else -> {
            }
        }
    }
}