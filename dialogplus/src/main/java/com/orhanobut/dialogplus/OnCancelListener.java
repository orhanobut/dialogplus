package com.orhanobut.dialogplus;

import android.support.annotation.NonNull;

/**
 * DialogPlus will use this listener to propagate cancel events when back button is pressed.
 */
public interface OnCancelListener {

  void onCancel(@NonNull DialogPlus dialog);
}
