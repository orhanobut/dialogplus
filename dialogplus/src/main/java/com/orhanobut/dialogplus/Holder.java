package com.orhanobut.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface Holder {

  void addHeader(View view);

  void addFooter(View view);

  void setBackgroundResource(int colorResource);

  View getView(LayoutInflater inflater, ViewGroup parent);

  void setOnKeyListener(View.OnKeyListener keyListener);

  View getInflatedView();

  View getHeader();

  View getFooter();

}
