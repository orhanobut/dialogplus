package com.orhanobut.dialogplus
import android.view.View

/** Receives clicks on views with IDs in custom content, headers and footers. */
fun interface OnClickListener {
    fun onClick(dialog: DialogPlus, view: View)
}
