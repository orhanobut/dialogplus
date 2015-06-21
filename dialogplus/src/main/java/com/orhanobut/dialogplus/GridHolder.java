package com.orhanobut.dialogplus;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;


/**
 * @author Orhan Obut
 */
public class GridHolder implements HolderAdapter, AdapterView.OnItemClickListener {

    private static final String TAG = GridHolder.class.getSimpleName();

    private final int columnNumber;

    private int backgroundColor;

    private GridView gridView;
    private ViewGroup headerContainer;
    private ViewGroup footerContainer;
    private OnHolderListener listener;
    private View.OnKeyListener keyListener;

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
    public void setBackgroundColor(int colorResource) {
        this.backgroundColor = colorResource;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_grid, parent, false);
        gridView = (GridView) view.findViewById(R.id.list);
        gridView.setBackgroundColor(parent.getResources().getColor(backgroundColor));
        gridView.setNumColumns(columnNumber);
        gridView.setOnItemClickListener(this);
        gridView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyListener == null) {
                    throw new NullPointerException("keyListener should not be null");
                }
                return keyListener.onKey(v, keyCode, event);
            }
        });
        headerContainer = (ViewGroup) view.findViewById(R.id.header_container);
        footerContainer = (ViewGroup) view.findViewById(R.id.footer_container);
        return view;
    }

    @Override
    public void setOnItemClickListener(OnHolderListener listener) {
        this.listener = listener;
    }

    @Override
    public void setOnKeyListener(View.OnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    @Override
    public View getInflatedView() {
        return gridView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onItemClick(parent.getItemAtPosition(position), view, position);
    }
}
