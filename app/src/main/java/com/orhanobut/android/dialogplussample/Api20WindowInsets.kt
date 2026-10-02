package com.orhanobut.android.dialogplussample

import android.annotation.TargetApi
import android.view.View

// Keep WindowInsets types out of the Activity loaded on API 15 devices.
@TargetApi(20)
internal object Api20WindowInsets {
    fun apply(decorView: View) {
        decorView.setOnApplyWindowInsetsListener { view, insets ->
            @Suppress("DEPRECATION")
            view.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop,
                insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets
        }
    }
}
