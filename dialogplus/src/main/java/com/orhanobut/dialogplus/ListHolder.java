package com.orhanobut.dialogplus;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;


/**
 * @author Orhan Obut
 */
public class ListHolder implements HolderAdapter, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = ListHolder.class.getSimpleName();

    private int backgroundColor;

    private ListView listView;
    private OnHolderListener listener;
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
    public void setBackgroundColor(int colorResource) {
        this.backgroundColor = colorResource;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.dialog_list, parent, false);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setOnScrollListener(this);
        listView.setBackgroundColor(parent.getResources().getColor(backgroundColor));
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
    public void setOnItemClickListener(OnHolderListener listener) {
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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getChildCount() > 0) {
            View child = view.getChildAt(0);
            int y = -child.getTop() + view.getFirstVisiblePosition() * child.getHeight();
//            trackActionBarPosition(y, firstVisibleItem);
            Log.i("Holder", "y: " + y);
        }
    }

//    private void trackActionBarPosition(int offsetY, int firstVisibleItem) {
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)getFakeActionBar().getLayoutParams();
//        int marginOffset = lp.topMargin;
//        if (marginOffset > 0) {
//            marginOffset = mDefaultMargin - offsetY;
//            if (offsetY > 800) marginOffset = 0;
//        } else {
//            marginOffset = 0;
//            if (offsetY < mDefaultMargin) marginOffset = mDefaultMargin - offsetY;
//        }
//        if (firstVisibleItem > 0) marginOffset = 0;
//        lp.setMargins(0, marginOffset, 0, 0);
//        getFakeActionBar().setLayoutParams(lp);
//    }
}
