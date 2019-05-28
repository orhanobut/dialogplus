package com.orhanobut.dialogplus;

import androidx.annotation.NonNull;

/**
 * DialogPlus tries to listen back press actions.
 */
public interface OnBackPressListener {

  /**
   * Invoked when DialogPlus receives any back press button event.
   */
  void onBackPressed(@NonNull DialogPlus dialogPlus);

}
