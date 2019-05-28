package com.orhanobut.dialogplus;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class ViewHolder implements Holder {

  private static final int INVALID = -1;

  private int backgroundResource;

  private ViewGroup headerContainer;
  private View headerView;

  private ViewGroup footerContainer;
  private View footerView;

  private View.OnKeyListener keyListener;

  private View contentView;
  private int viewResourceId = INVALID;

  public ViewHolder(int viewResourceId) {
    this.viewResourceId = viewResourceId;
  }

  public ViewHolder(View contentView) {
    this.contentView = contentView;
  }

  @Override public void addHeader(@NonNull View view) {
    addHeader(view, false);
  }

  @Override public void addHeader(@NonNull View view, boolean fixed) {
    headerContainer.addView(view);
    headerView = view;
  }

  @Override public void addFooter(@NonNull View view) {
    addFooter(view, false);
  }

  @Override public void addFooter(@NonNull View view, boolean fixed) {
    footerContainer.addView(view);
    footerView = view;
  }

  @Override public void setBackgroundResource(int colorResource) {
    this.backgroundResource = colorResource;
  }

  @Override @NonNull
  public View getView(@NonNull LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.dialog_view, parent, false);
    View outMostView = view.findViewById(R.id.dialogplus_outmost_container);
    outMostView.setBackgroundResource(backgroundResource);
    ViewGroup contentContainer = view.findViewById(R.id.dialogplus_view_container);
    contentContainer.setOnKeyListener(new View.OnKeyListener() {
      @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyListener == null) {
          throw new NullPointerException("keyListener should not be null");
        }
        return keyListener.onKey(v, keyCode, event);
      }
    });
    addContent(inflater, parent, contentContainer);
    headerContainer = view.findViewById(R.id.dialogplus_header_container);
    footerContainer = view.findViewById(R.id.dialogplus_footer_container);
    return view;
  }

  private void addContent(LayoutInflater inflater, ViewGroup parent, ViewGroup container) {
    if (viewResourceId != INVALID) {
      contentView = inflater.inflate(viewResourceId, parent, false);
    } else {
      ViewGroup parentView = (ViewGroup) contentView.getParent();
      if (parentView != null) {
        parentView.removeView(contentView);
      }
    }

    container.addView(contentView);
  }

  @Override public void setOnKeyListener(View.OnKeyListener keyListener) {
    this.keyListener = keyListener;
  }

  @Override @NonNull
  public View getInflatedView() {
    return contentView;
  }

  @Override public View getHeader() {
    return headerView;
  }

  @Override public View getFooter() {
    return footerView;
  }
}
