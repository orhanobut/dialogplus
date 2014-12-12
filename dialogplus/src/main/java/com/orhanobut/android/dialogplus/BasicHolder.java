package com.orhanobut.android.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

/**
 * @author Orhan Obut
 */
public class BasicHolder implements Holder {

    private SimpleListView simpleListView;
    private ViewGroup headerContainer;
    private ViewGroup footerContainer;

    @Override
    public void addHeader(View view) {
        headerContainer.addView(view);
    }

    @Override
    public void addFooter(View view) {
        footerContainer.addView(view);
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        simpleListView.setAdapter(adapter);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_basic, parent, false);
        simpleListView = (SimpleListView) view.findViewById(R.id.list);
        headerContainer = (ViewGroup) view.findViewById(R.id.header_container);
        footerContainer = (ViewGroup) view.findViewById(R.id.footer_container);
        return view;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        simpleListView.setOnItemClickListener(listener);
    }
}
