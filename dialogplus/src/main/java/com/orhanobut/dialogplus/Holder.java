package com.orhanobut.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author Orhan Obut
 */
public interface Holder {

    void addHeader(View view);

    void addFooter(View view);

    void setAdapter(BaseAdapter adapter);

    View getView(LayoutInflater inflater, ViewGroup parent);

    void setOnItemClickListener(OnItemClickListener listener);

    void setOnKeyListener(View.OnKeyListener keyListener);

}
