package com.specialprojects.experiments.envelopecall

import android.telecom.Call
import android.telecom.InCallService
import timber.log.Timber

class CallService: InCallService() {
    override fun onCallAdded(call: Call) {
        Timber.d("onCallAdded()")
        super.onCallAdded(call)
        call.registerCallback(callback)
        (applicationContext as EnvelopeCallApp).newCall.postValue(call)
    }

    private val callback = object : Call.Callback() {
        override fun onStateChanged(call: Call, newState: Int) {
            Timber.d(call.toString())

            (applicationContext as EnvelopeCallApp).callState.postValue(newState)
        }
    }

    override fun onCallRemoved(call: Call) {
        Timber.d("onCallRemoved()")
        super.onCallRemoved(call)
        call.unregisterCallback(callback)
    }
}