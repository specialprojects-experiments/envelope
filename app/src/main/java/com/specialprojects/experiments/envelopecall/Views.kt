package com.specialprojects.experiments.envelopecall

import android.app.Activity
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment

fun <T : View> View.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) ?: viewNotFound(id) }

fun <T : View> Activity.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) ?: viewNotFound(id) }

fun <T : View> Fragment.bindView(id: Int): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) { view?.findViewById<T>(id) ?: viewNotFound(id) }

private fun viewNotFound(id: Int): Nothing =
    throw IllegalStateException("View ID $id not found.")

inline fun View.waitForLayout(crossinline f: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            removeOnGlobalLayoutListener(this)
            f()
        }
    })
}