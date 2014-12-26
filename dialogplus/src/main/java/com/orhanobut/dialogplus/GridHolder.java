package com.orhanobut.dialogplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;


/**
 * @author Orhan Obut
 */
public class GridHolder implements Holder, AdapterView.OnItemClickListener {

    private final int columnNumber;

    private GridView gridView;
    private ViewGroup headerContainer;
    private ViewGroup footerContainer;
    private OnItemClickListener listener;

    public GridHolder(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    @Override
    public void addHeader(View view) {
        if (view == null) {
            return;
        }
        headerContainer.addView(view);
    }

    @Override
    public void addFooter(View view) {
        if (view == null) {
            return;
        }
        footerContainer.addView(view);
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
        gridView.setOnItemClickListener(this);

        headerContainer = (ViewGroup) view.findViewById(R.id.header_container);
        footerContainer = (ViewGroup) view.findViewById(R.id.footer_container);
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
