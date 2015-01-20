package com.orhanobut.dialogplus;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;


/**
 * @author Orhan Obut
 */
public class ListHolder implements Holder, AdapterView.OnItemClickListener {

    private static final String TAG = ListHolder.class.getSimpleName();

    private ListView listView;
    private OnItemClickListener listener;
    private View.OnKeyListener keyListener;

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
        listView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyListener == null) {
                    throw new NullPointerException("keyListener should not be null");
                }
                return keyListener.onKey(v, keyCode, event);
            }
        });
        return view;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void setOnKeyListener(View.OnKeyListener keyListener) {
        this.keyListener = keyListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onItemClick(parent.getItemAtPosition(position), view, position);
    }
}
