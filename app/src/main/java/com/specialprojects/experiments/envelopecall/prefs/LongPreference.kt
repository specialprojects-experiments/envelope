package com.specialprojects.experiments.envelopecall.prefs

import android.content.SharedPreferences

class LongPreference(private val preferences: SharedPreferences,
                     private val key: String, private val defaultValue: Long = 0L) {

    fun get(): Long {
        return preferences.getLong(key, defaultValue)
    }

    val isSet: Boolean
        get() = preferences.contains(key)

    fun set(value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun delete() {
        preferences.edit().remove(key).apply()
    }
}