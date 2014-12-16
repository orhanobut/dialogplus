package com.github.nr4bt.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.orhanobut.android.dialogplus.R;

/**
 * @author Orhan Obut
 */
public class ListHolder implements Holder, AdapterView.OnItemClickListener {

    private ListView listView;
    private OnItemClickListener listener;

    @Override
    public void addHeader(View view) {
        if (view == null) {
            return;
        }
        listView.addHeaderView(view);
    }

    @Override
    public void addFooter(View view) {
        if (view == null) {
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
        listView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onItemClick(parent.getItemAtPosition(position), view, position);
    }
}
