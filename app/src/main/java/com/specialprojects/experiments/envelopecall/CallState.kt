package com.specialprojects.experiments.envelopecall

import android.telecom.Call
import timber.log.Timber

fun Int.asString(): String = when (this) {
    Call.STATE_NEW -> "NEW"
    Call.STATE_RINGING -> "RINGING"
    Call.STATE_DIALING -> "DIALING"
    Call.STATE_ACTIVE -> "ACTIVE"
    Call.STATE_HOLDING -> "HOLDING"
    Call.STATE_DISCONNECTED -> "DISCONNECTED"
    Call.STATE_CONNECTING -> "CONNECTING"
    Call.STATE_DISCONNECTING -> "DISCONNECTING"
    Call.STATE_SELECT_PHONE_ACCOUNT -> "SELECT_PHONE_ACCOUNT"
    else -> {
        Timber.w("Unknown state $this")
        "UNKNOWN"
    }
}

sealed class CallState {
    class Ringing(val call: Call) : CallState()
    class Dialing(val call: Call) : CallState()
    class Active(val call: Call) : CallState()
    object Default : CallState()
}