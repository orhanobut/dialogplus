package com.orhanobut.dialogplus

import android.annotation.TargetApi
import android.app.Activity
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher

// Keep Android 13 types out of classes loaded by API 15 devices.
@TargetApi(33)
internal object Api33BackPress {
    fun register(activity: Activity, onBack: () -> Unit): () -> Unit {
        val dispatcher = activity.onBackInvokedDispatcher
        val callback = OnBackInvokedCallback { onBack() }
        dispatcher.registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_OVERLAY, callback)
        return { dispatcher.unregisterOnBackInvokedCallback(callback) }
    }
}
