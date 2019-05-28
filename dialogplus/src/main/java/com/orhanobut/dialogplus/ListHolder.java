package com.orhanobut.dialogplus;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

public class ListHolder implements HolderAdapter, AdapterView.OnItemClickListener {

  private int backgroundResource;

  private ListView listView;
  private OnHolderListener listener;
  private View.OnKeyListener keyListener;
  private View headerView;
  private View footerView;
  private ViewGroup footerContainer;
  private ViewGroup headerContainer;

  @Override public void addHeader(@NonNull View view) {
    addHeader(view, false);
  }

  @Override public void addHeader(@NonNull View view, boolean fixed) {
    if (fixed) {
      headerContainer.addView(view);
    } else {
      listView.addHeaderView(view);
    }
    headerView = view;
  }

  @Override public void addFooter(@NonNull View view) {
    addFooter(view, false);
  }

  @Override public void addFooter(@NonNull View view, boolean fixed) {
    if (fixed) {
      footerContainer.addView(view);
    } else {
      listView.addFooterView(view);
    }
    footerView = view;
  }

  @Override public void setAdapter(@NonNull BaseAdapter adapter) {
    listView.setAdapter(adapter);
  }

  @Override public void setBackgroundResource(int colorResource) {
    this.backgroundResource = colorResource;
  }

  @Override @NonNull
  public View getView(@NonNull LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.dialog_list, parent, false);
    View outMostView = view.findViewById(R.id.dialogplus_outmost_container);
    outMostView.setBackgroundResource(backgroundResource);
    listView = view.findViewById(R.id.dialogplus_list);
    listView.setOnItemClickListener(this);
    listView.setOnKeyListener(new View.OnKeyListener() {
      @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyListener == null) {
          throw new NullPointerException("keyListener should not be null");
        }
        return keyListener.onKey(v, keyCode, event);
      }
    });
    headerContainer = view.findViewById(R.id.dialogplus_header_container);
    footerContainer = view.findViewById(R.id.dialogplus_footer_container);
    return view;
  }

  @Override public void setOnItemClickListener(OnHolderListener listener) {
    this.listener = listener;
  }

  @Override public void setOnKeyListener(View.OnKeyListener keyListener) {
    this.keyListener = keyListener;
  }

  @Override @NonNull
  public View getInflatedView() {
    return listView;
  }

  @Override public View getHeader() {
    return headerView;
  }

  @Override public View getFooter() {
    return footerView;
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    if (listener == null) {
      return;
    }
    //ListView counts header as position as well. For consistency we don't
    listener.onItemClick(
        parent.getItemAtPosition(position),
        view,
        headerView != null ? --position : position
    );
  }
}
