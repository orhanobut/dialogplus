package com.orhanobut.android.dialogplus;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;


/**
 * @author Orhan Obut.
 *         To be able to use a list with adapter in a scrollview container.
 *         This container has also dataset observer in order to be able use adapter notify changes.
 *         There is a horizontal divider as default which puts a line between rows.
 */
public class SimpleListView extends LinearLayout {

    private static final int INVALID = -1;

    private ListAdapter adapter;
    private DataSetObserver dataSetObserver;
    private final LayoutInflater layoutInflater;
    private int dividerResId = INVALID;
    private AdapterView.OnItemClickListener itemClickListener;

    public SimpleListView(Context context) {
        this(context, null);
    }

    public SimpleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = LayoutInflater.from(getContext());
        setOrientation(VERTICAL);
    }

    public void setDividerResId(int resId) {
        dividerResId = resId;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setAdapter(ListAdapter adapter) {
        if (this.adapter != null && this.dataSetObserver != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        dataSetObserver = new AdapterDataSetObserver();
        this.adapter.registerDataSetObserver(dataSetObserver);

        resetList();
        refreshList();
    }

    private void refreshList() {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            final View view = adapter.getView(i, null, this);
            final int position = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener == null) {
                        return;
                    }
                    //TODO add on item click listener
                    // itemClickListener.onItemClick(adapter.getItem(position), view, position);
                }
            });
            addView(view);

            // adds divider between rows, it must be set by the user
            if (dividerResId != INVALID && i != count - 1) {
                addView(layoutInflater.inflate(dividerResId, this, false));
            }
        }
    }

    /**
     * Clears everything
     */
    private void resetList() {
        this.removeAllViews();
        invalidate();
    }

    /**
     * observe dataset changes, when the adapter notifyDataSetChanged method called, onChanged
     * method will be called and view will be refreshed.
     */
    class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();

            resetList();
            refreshList();
        }
    }
}
