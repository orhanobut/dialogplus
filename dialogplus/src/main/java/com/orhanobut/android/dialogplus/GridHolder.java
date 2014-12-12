package com.orhanobut.android.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

/**
 * @author Orhan Obut
 */
public class GridHolder implements Holder {

    private final int columnNumber;

    private GridView gridView;

    public GridHolder(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    @Override
    public void addHeader(View view) {
        gridView.addView(view);
    }

    @Override
    public void addFooter(View view) {
        gridView.addView(view);
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        gridView.setAdapter(adapter);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_grid, parent, false);
        gridView = (GridView) view.findViewById(R.id.list);
        gridView.setNumColumns(columnNumber);
        return view;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        gridView.setOnItemClickListener(listener);
    }
}
