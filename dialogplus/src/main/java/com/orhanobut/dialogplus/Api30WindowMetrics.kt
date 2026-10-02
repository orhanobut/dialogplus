package com.orhanobut.dialogplus

import android.annotation.TargetApi
import android.app.Activity
import android.view.WindowInsets

// Called only on API 30+, so older devices never load WindowMetrics/Insets types.
@TargetApi(30)
internal object Api30WindowMetrics {
    fun availableHeight(activity: Activity): Int {
        val metrics = activity.windowManager.currentWindowMetrics
        val insets = metrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        return metrics.bounds.height() - insets.top - insets.bottom
    }
}
