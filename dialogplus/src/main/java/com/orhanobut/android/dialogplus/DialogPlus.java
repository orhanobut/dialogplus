package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * @author Orhan Obut
 */
public class DialogPlus {

    private static final int INVALID = -1;

    private Context context;
    private ViewGroup rootView;
    private ViewGroup contentContainer;
    private int gravity = Gravity.BOTTOM;
    private ScreenType screenType = ScreenType.HALF;
    private boolean isCancelable = true;
    private View topView;
    private View bottomView;
    private View footerView;
    private View headerView;
    private int footerViewResourceId = INVALID;
    private int headerViewResourceId = INVALID;
    private BaseAdapter adapter;
    private AdapterView.OnItemClickListener onItemClickListener;
    private Holder holder;
    private ViewGroup decorView;
    private LayoutInflater inflater;

    public enum ScreenType {
        HALF, FULL
    }

    private DialogPlus() {
    }

    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }

    public boolean isShowing() {
        View view = decorView.findViewById(R.id.outmost_container);
        return view != null;
    }

    public void dismiss() {
        decorView.removeView(rootView);
    }

    private void createDialog() {
        init();
        initViews();
        initContentView();
        initContentView();
        initPosition();
        initCancellable();
    }

    private void init() {
        inflater = LayoutInflater.from(context);
        Activity activity = (Activity) context;
        decorView = (ViewGroup) activity.getWindow().getDecorView();
    }

    private void initViews() {
        rootView = (ViewGroup) inflater.inflate(R.layout.base_container, null);
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
        topView = rootView.findViewById(R.id.top_view);
        bottomView = rootView.findViewById(R.id.bottom_view);
    }

    private void initContentView() {
        View contentView = createView(inflater);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, gravity
        );
        contentView.setLayoutParams(params);
        contentContainer.addView(contentView);
    }

    private void initCancellable() {
        if (!isCancelable) {
            return;
        }
        topView.setOnTouchListener(onCancelableTouchListener);
        bottomView.setOnTouchListener(onCancelableTouchListener);
    }

    private void initPosition() {
        if (screenType == ScreenType.FULL) {
            topView.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
            return;
        }
        switch (gravity) {
            case Gravity.TOP:
                bottomView.setVisibility(View.VISIBLE);
                topView.setVisibility(View.GONE);
                break;
            default:
                bottomView.setVisibility(View.GONE);
                topView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private View createView(LayoutInflater inflater) {
        View view = holder.getView(inflater, rootView);
        holder.addFooter(getFooterView());
        holder.addHeader(getHeaderView());
        if (adapter != null) {
            holder.setAdapter(adapter);
        }
        if (onItemClickListener != null) {
            holder.setOnItemClickListener(onItemClickListener);
        }
        return view;
    }

    private View getFooterView() {
        if (footerView != null) {
            return footerView;
        }
        if (footerViewResourceId != INVALID) {
            footerView = inflater.inflate(footerViewResourceId, rootView, false);
        }
        return footerView;
    }

    private View getHeaderView() {
        if (headerView != null) {
            return headerView;
        }
        if (headerViewResourceId != INVALID) {
            headerView = inflater.inflate(headerViewResourceId, rootView, false);
        }
        return headerView;
    }

    private void onAttached(View view) {
        decorView.addView(view);
    }

    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
            }
            return false;
        }
    };

    /**
     * Use this builder to create a dialog
     */
    public static class Builder {
        DialogPlus dialog = new DialogPlus();

        public Builder from(Context context) {
            dialog.context = context;
            return this;
        }

        public Builder setAdapter(BaseAdapter adapter) {
            dialog.adapter = adapter;
            return this;
        }

        public Builder setFooter(int resourceId) {
            dialog.footerViewResourceId = resourceId;
            return this;
        }

        public Builder setFooter(View view) {
            dialog.footerView = view;
            return this;
        }

        public Builder setHeader(int resourceId) {
            dialog.headerViewResourceId = resourceId;
            return this;
        }

        public Builder setHeader(View view) {
            dialog.headerView = view;
            return this;
        }

        public Builder setCancelable(boolean isCancelable) {
            dialog.isCancelable = isCancelable;
            return this;
        }

        public Builder setHolder(Holder holder) {
            dialog.holder = holder;
            return this;
        }

        public Builder setGravity(int gravity) {
            dialog.gravity = gravity;
            return this;
        }

        public Builder setScreenType(ScreenType screenType) {
            dialog.screenType = screenType;
            return this;
        }

        public Builder setOnItemClickListener(AdapterView.OnItemClickListener listener) {
            dialog.onItemClickListener = listener;
            return this;
        }

        public DialogPlus create() {
            dialog.createDialog();
            return dialog;
        }
    }
}
