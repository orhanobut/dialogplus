package com.orhanobut.dialogplus;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * It is used to propagate click events for {@link ListHolder} and {@link GridHolder}
 *
 * <p>For each item click, {@link #onItemClick(DialogPlus, Object, View, int)} will be invoked</p>
 */
public interface OnItemClickListener {

  void onItemClick(@NonNull DialogPlus dialog, @NonNull Object item, @NonNull View view, int position);

}
