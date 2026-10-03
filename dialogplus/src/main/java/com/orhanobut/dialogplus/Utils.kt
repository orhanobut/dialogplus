package com.orhanobut.dialogplus

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.AbsListView

internal object Utils {
    fun getDisplayHeight(activity: Activity): Int {
        val content = activity.findViewById<View>(android.R.id.content)
        if (content.height > 0) return content.height
        if (Build.VERSION.SDK_INT >= 30) {
            return Api30WindowMetrics.availableHeight(activity)
        }
        // Required for API 15–29. getSize excludes the navigation bar.
        val size = Point()
        @Suppress("DEPRECATION")
        activity.windowManager.defaultDisplay.getSize(size)
        val visibleFrame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(visibleFrame)
        return size.y - visibleFrame.top
    }
    fun animateContent(view: View, to: Int, listener: Animation.AnimationListener) {
        val animation = HeightAnimation(view, view.height, to)
        animation.setAnimationListener(listener)
        animation.duration = 200
        view.startAnimation(animation)
    }
    fun listIsAtTop(listView: AbsListView): Boolean = listView.childCount == 0 ||
        (listView.firstVisiblePosition == 0 && listView.getChildAt(0).top == listView.paddingTop)
    fun getView(context: Context, resourceId: Int, view: View?): View? =
        view ?: if (resourceId != -1) LayoutInflater.from(context).inflate(resourceId, null) else null
    fun getAnimationResource(gravity: Int, isInAnimation: Boolean): Int = when {
        gravity and Gravity.TOP == Gravity.TOP -> if (isInAnimation) R.anim.slide_in_top else R.anim.slide_out_top
        gravity and Gravity.BOTTOM == Gravity.BOTTOM -> if (isInAnimation) R.anim.slide_in_bottom else R.anim.slide_out_bottom
        gravity and Gravity.CENTER == Gravity.CENTER -> if (isInAnimation) R.anim.fade_in_center else R.anim.fade_out_center
        else -> -1
    }
}
