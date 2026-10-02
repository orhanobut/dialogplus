package com.orhanobut.dialogplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/** Content contract for custom, list and grid holders. Inflate before adding headers or footers. */
interface Holder {
    fun addHeader(view: View)
    fun addHeader(view: View, fixed: Boolean)
    fun addFooter(view: View)
    fun addFooter(view: View, fixed: Boolean)
    /** Accepts a drawable or color resource, or zero to remove the background. */
    fun setBackgroundResource(colorResource: Int)
    fun getView(inflater: LayoutInflater, parent: ViewGroup?): View
    fun setOnKeyListener(keyListener: View.OnKeyListener?)
    val inflatedView: View
    val header: View?
    val footer: View?
}
