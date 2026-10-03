package com.orhanobut.dialogplus

/** Receives cancellation from the back button or overlay. */
fun interface OnCancelListener {
    fun onCancel(dialog: DialogPlus)
}
