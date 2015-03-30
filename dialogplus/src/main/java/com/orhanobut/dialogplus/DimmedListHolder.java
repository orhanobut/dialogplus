package com.orhanobut.dialogplus;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author yonghoon.do
 */
public class DimmedListHolder implements HolderAdapter, AdapterView.OnItemClickListener, ResolverDrawerLayout.OnDismissedListener {

    private static final String TAG = ListHolder.class.getSimpleName();

    private int backgroundColor;

    private ResolverDrawerLayout drawerLayout;
    private ListView listView;

    private String title;
    private View fixedView;

    private OnHolderListener listener;
    private View.OnKeyListener keyListener;
    private ResolverDrawerLayout.OnDismissedListener dismissedListener;

    public void setFixedTitle(String title) {
        this.title = title;
    }

    public void setFixedView(View view) {
        fixedView = view;
    }

    @Override
    public void setAdapter(BaseAdapter adapter) {
        listView.setAdapter(adapter);
    }

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
    public void setBackgroundColor(int colorResource) {
        this.backgroundColor = colorResource;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_resolver_list_default, parent, false);
        drawerLayout = (ResolverDrawerLayout) view.findViewById(R.id.contentPanel);
        drawerLayout.setOnDismissedListener(this);
        listView = (ListView) drawerLayout.findViewById(R.id.resolver_list);
        listView.setBackgroundColor(backgroundColor);
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
        if (fixedView != null) {
            LinearLayout fixedHeaderLayout = (LinearLayout) view.findViewById(R.id.fixed_header_layout);
            fixedHeaderLayout.removeAllViews();
            fixedHeaderLayout.addView(fixedView);
        } else {
            ((TextView) view.findViewById(R.id.title)).setText(title);
        }
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

    public void setOnDismissedListener(ResolverDrawerLayout.OnDismissedListener listener) {
        dismissedListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listener.onItemClick(parent.getItemAtPosition(position), view, position);
    }

    @Override
    public void onDismissed() {
        if (dismissedListener != null) {
            dismissedListener.onDismissed();
        }
    }
}
