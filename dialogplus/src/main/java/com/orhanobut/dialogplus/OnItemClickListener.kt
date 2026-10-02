package com.orhanobut.dialogplus
import android.view.View

/** Receives list or grid item clicks. List positions exclude scrolling headers. */
fun interface OnItemClickListener {
    fun onItemClick(dialog: DialogPlus, item: Any?, view: View?, position: Int)
}
