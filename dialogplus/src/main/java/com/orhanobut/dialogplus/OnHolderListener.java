package com.orhanobut.dialogplus;

import android.support.annotation.NonNull;
import android.view.View;

public interface OnHolderListener {

  void onItemClick(@NonNull Object item, @NonNull View view, int position);

}
