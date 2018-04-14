package com.orhanobut.dialogplus;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Holder {

  void addHeader(@NonNull View view);

  /**
   * Adds the given view as header to the top of holder
   *
   * @param view  will be shown as header.
   * @param fixed fixed on top if it is true, scrollable otherwise
   */
  void addHeader(@NonNull View view, boolean fixed);

  void addFooter(@NonNull View view);

  /**
   * Adds the given view as footer to the bottom of holder
   *
   * @param view  will be shown as footer.
   * @param fixed fixed at bottom if it is true, scrollable otherwise
   */
  void addFooter(@NonNull View view, boolean fixed);

  void setBackgroundResource(int colorResource);

  View getView(@NonNull LayoutInflater inflater, ViewGroup parent);

  void setOnKeyListener(View.OnKeyListener keyListener);

  View getInflatedView();

  View getHeader();

  View getFooter();

}
