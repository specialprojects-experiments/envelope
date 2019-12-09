package com.specialprojects.experiments.envelopecall

import android.telecom.Call
import android.telecom.InCallService
import timber.log.Timber

class CallService: InCallService() {
    override fun onCallAdded(call: Call) {
        call.registerCallback(callback)
        Timber.d("onCallAdded()")
        super.onCallAdded(call)

        if (call.state == Call.STATE_RINGING) {
            (applicationContext as EnvelopeCallApp).callState.postValue(CallState.Ringing(call))
        }
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
    }
}