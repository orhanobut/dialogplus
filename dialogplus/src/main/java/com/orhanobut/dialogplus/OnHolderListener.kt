package com.orhanobut.dialogplus
import android.view.View

/** Receives an adapter item click. Header and footer items can be null. */
fun interface OnHolderListener {
    fun onItemClick(item: Any?, view: View?, position: Int)
}
