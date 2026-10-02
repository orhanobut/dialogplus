package com.orhanobut.dialogplus

import android.view.animation.Animation

/** Subclass to handle only the animation callbacks you need. */
open class SimpleAnimationListener : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) = Unit
    override fun onAnimationEnd(animation: Animation) = Unit
    override fun onAnimationRepeat(animation: Animation) = Unit
}
