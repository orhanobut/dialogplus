package com.orhanobut.android.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @author Orhan Obut
 */
public class ListHolder implements Holder {

    private ListView listView;

    @Override
    public void addHeader(View view) {
        if (view == null){
            return;
        }
        listView.addHeaderView(view);
    }

    @Override
    public void addFooter(View view) {
        if (view == null){
            return;
        }
        listView.addFooterView(view);
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        listView.setAdapter(adapter);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_list, parent, false);
        listView = (ListView) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }
}
