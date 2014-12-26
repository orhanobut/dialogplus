package com.orhanobut.dialogplus;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.orhanobut.android.dialogplus.R;

/**
 * @author Orhan Obut
 */
public class BasicHolder implements Holder, SimpleListView.OnItemClickListener {

    private static final String TAG = BasicHolder.class.getSimpleName();
    private SimpleListView simpleListView;
    private OnItemClickListener listener;

    @Override
    public void addHeader(View view) {
        Log.d(TAG, "addHeader called");
        simpleListView.setHeaderView(view);
    }

    @Override
    public void addFooter(View view) {
        Log.d(TAG, "addFooter called");
        simpleListView.setFooterView(view);
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        simpleListView.setAdapter(adapter);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_basic, parent, false);
        simpleListView = (SimpleListView) view.findViewById(R.id.list);
        simpleListView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(Object item, View view, int position) {
        listener.onItemClick(item, view, position);
    }
}
