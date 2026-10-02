package com.orhanobut.dialogplus

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.widget.AbsListView
import android.widget.FrameLayout

internal class ExpandTouchListener(
    private val listView: AbsListView,
    private val contentContainer: View,
    private val gravity: Int,
    private val displayHeight: Int,
    private val defaultContentHeight: Int,
) : View.OnTouchListener {
    private var y = -1f
    private var fullScreen = false
    private var touchUp = false
    private var scrollUp = false
    private val params = contentContainer.layoutParams as FrameLayout.LayoutParams
    private val gestureDetector = GestureDetector(listView.context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean = true
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            scrollUp = distanceY > 0
            return false
        }
    })
    // Single taps are returned to AbsListView, which owns item click accessibility.
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event)) return false
        if (fullScreen && (scrollUp || !Utils.listIsAtTop(listView))) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> y = event.rawY
            MotionEvent.ACTION_MOVE -> {
                if (params.height == displayHeight) {
                    params.height--
                    contentContainer.layoutParams = params
                    return false
                }
                if (y == -1f) y = event.rawY
                val delta = y - event.rawY
                touchUp = delta > 0
                y = event.rawY
                params.height = DialogLayoutRules.dragHeight(params.height, delta, gravity, defaultContentHeight, displayHeight)
                contentContainer.layoutParams = params
                fullScreen = params.height == displayHeight
            }
            MotionEvent.ACTION_UP -> {
                y = -1f
                val target = DialogLayoutRules.snapHeight(params.height, touchUp, defaultContentHeight, displayHeight)
                if (target != null) Utils.animateContent(contentContainer, target, object : SimpleAnimationListener() {
                    override fun onAnimationEnd(animation: Animation) { fullScreen = target == displayHeight }
                })
            }
            MotionEvent.ACTION_CANCEL -> y = -1f
        }
        return true
    }
}
