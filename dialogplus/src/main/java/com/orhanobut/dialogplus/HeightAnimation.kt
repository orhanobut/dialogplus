package com.orhanobut.dialogplus

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

internal class HeightAnimation(private val view: View, private val fromHeight: Int, private val toHeight: Int) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        view.layoutParams.height = DialogLayoutRules.animatedHeight(fromHeight, toHeight, interpolatedTime)
        view.requestLayout()
    }
    override fun willChangeBounds(): Boolean = true
}
