package com.orhanobut.dialogplus

import android.view.Gravity

/** Pure sizing and snapping rules shared by the builder and drag listener. */
internal object DialogLayoutRules {
    fun margin(gravity: Int, value: Int, centerMargin: Int): Int =
        if (value != -1) value else if (gravity == Gravity.CENTER) centerMargin else 0

    fun defaultHeight(displayHeight: Int): Int = displayHeight * 2 / 5

    fun dragHeight(height: Int, delta: Float, gravity: Int, defaultHeight: Int, displayHeight: Int): Int {
        val movement = if (gravity == Gravity.TOP) -delta else delta
        return (height + movement.toInt()).coerceAtMost(displayHeight).coerceAtLeast(defaultHeight)
    }

    fun snapHeight(height: Int, touchUp: Boolean, defaultHeight: Int, displayHeight: Int): Int? = when {
        !touchUp && height < displayHeight && height > displayHeight * 4 / 5 -> displayHeight
        touchUp && height > defaultHeight + 50 -> displayHeight
        touchUp && height <= defaultHeight + 50 -> defaultHeight
        !touchUp && height > defaultHeight -> defaultHeight
        else -> null
    }

    fun animatedHeight(from: Int, to: Int, fraction: Float): Int = (from + (to - from) * fraction).toInt()
}
