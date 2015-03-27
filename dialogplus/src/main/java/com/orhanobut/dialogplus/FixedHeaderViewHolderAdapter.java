package com.orhanobut.dialogplus;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * @author yonghoon.do
 */
public class FixedHeaderViewHolderAdapter implements HolderAdapter, AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private static final String TAG = FixedHeaderViewHolderAdapter.class.getSimpleName();

    private int backgroundColor;

    private FrameLayout rootView;
    private View fixedHeaderView;
    private ListView listView;
    private OnHolderListener listener;
    private View.OnKeyListener keyListener;

    private int initialFixedViewPositionY = 0;
    private int totalHeaderViewHeight = 0;

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = null;

    public void addFixedHeader(View view, int defaultMargin) {
        fixedHeaderView = view;
        rootView.addView(fixedHeaderView);
        initialFixedViewPositionY = defaultMargin;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fixedHeaderView.getLayoutParams();
        lp.setMargins(0, initialFixedViewPositionY, 0, 0);
        fixedHeaderView.setLayoutParams(lp);
    }

    @Override
    public void addHeader(final View view) {
        if (view == null) {
            return;
        }
        listView.addHeaderView(view);
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                totalHeaderViewHeight += (view.getHeight() - view.getPaddingBottom() - view.getPaddingTop());
                initialFixedViewPositionY -= (view.getHeight() - view.getPaddingBottom() - view.getPaddingTop());

                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fixedHeaderView.getLayoutParams();
                lp.setMargins(0, initialFixedViewPositionY, 0, 0);
                fixedHeaderView.setLayoutParams(lp);
            }
        };
        if (view.getTag() != null && view.getTag() instanceof String && view.getTag().equals("TransparencyView")) {
            return;
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
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
        rootView = (FrameLayout) inflater.inflate(R.layout.dialog_list, parent, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        listView.setOnScrollListener(this);
        listView.setBackgroundColor(Color.TRANSPARENT);
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

        return rootView;
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
        if (fixedHeaderView == null) {
            return;
        }
        if (view.getChildCount() > 0) {
            View child = view.getChildAt(0);
            int y = -child.getTop() + view.getFirstVisiblePosition() * child.getHeight();
            trackHeaderViewPosition(y, firstVisibleItem);
        }
    }

    /**
     * fixedHeaderView is following the current scroll position along with items.
     * */
    private void trackHeaderViewPosition(int offsetY, int firstVisibleItem) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fixedHeaderView.getLayoutParams();
        int marginOffset = initialFixedViewPositionY - (offsetY + totalHeaderViewHeight);
        if (marginOffset < 0 || firstVisibleItem > 0 || offsetY >= initialFixedViewPositionY) {
            marginOffset = 0;
        }

        Log.i("Holder", "y: " + offsetY + ", margin: " + marginOffset + ", initialMarginY: " + initialFixedViewPositionY + ", visibleItem: " + firstVisibleItem + ", headerViewCount: " + listView.getHeaderViewsCount());
        lp.setMargins(0, marginOffset, 0, 0);
        fixedHeaderView.setLayoutParams(lp);
    }
}
