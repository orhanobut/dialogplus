package com.orhanobut.dialogplus

import android.widget.BaseAdapter

/** A holder whose items come from a platform adapter. */
interface HolderAdapter : Holder {
    fun setAdapter(adapter: BaseAdapter)
    fun setOnItemClickListener(listener: OnHolderListener?)
}
