package com.orhanobut.dialogplus

/** Receives a back press before optional cancellation. */
fun interface OnBackPressListener {
    fun onBackPressed(dialogPlus: DialogPlus)
}
