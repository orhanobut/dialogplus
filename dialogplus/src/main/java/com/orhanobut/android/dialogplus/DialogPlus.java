package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
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

    /**
     * Determine whether the resources are set or not
     */
    private static final int INVALID = -1;

    private Context context;

    /**
     * DialogPlus base layout root view
     */
    private ViewGroup rootView;

    /**
     * DialogPlus content container which is a different layout rather than base layout
     */
    private ViewGroup contentContainer;

    /**
     * Determines the position of the dialog, only BOTTOM and TOP should be used, as default
     * it is BOTTOM
     */
    private int gravity = Gravity.BOTTOM;

    /**
     * Either the content should fill the all screen or only the half, when the content reaches
     * the limit, it will be scrollable, BasicHolder cannot be scrollable, use it only for
     * a few items
     */
    private ScreenType screenType = ScreenType.HALF;

    /**
     * Determines whether dialog should be dismissed by back button or touch in the black overlay
     */
    private boolean isCancelable = true;

    /**
     * topView and bottomView are used to set the position of the dialog
     * If the position is top, bottomView will fill the screen, otherwise
     * topView will the screen
     */
    private View topView;
    private View bottomView;

    /**
     * footerView, headerView, footerViewResourceId, headerViewResourceId are used
     * to set header/footer for the dialog content
     */
    private View footerView;
    private View headerView;
    private int footerViewResourceId = INVALID;
    private int headerViewResourceId = INVALID;

    /**
     * Content adapter
     */
    private BaseAdapter adapter;

    /**
     * Listener for the user to take action by clicking any item
     */
    private AdapterView.OnItemClickListener onItemClickListener;

    /**
     * Content
     */
    private Holder holder;

    /**
     * basically activity root view
     */
    private ViewGroup decorView;
    private LayoutInflater inflater;

    public enum ScreenType {
        HALF, FULL
    }

    private DialogPlus() {
    }

    /**
     * It adds the dialog view into rootView which is decorView of activity
     */
    public void show() {
        if (isShowing()) {
            return;
        }
        onAttached(rootView);
    }

    /**
     * It basically check if the rootView contains the dialog plus view.
     *
     * @return true if it contains
     */
    public boolean isShowing() {
        View view = decorView.findViewById(R.id.outmost_container);
        return view != null;
    }

    /**
     * It is called when to dismiss the dialog, either by calling dismiss() method or with cancellable
     */
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

    /**
     * Initialize the appropriate views and also set for the back press button.
     */
    private void initViews() {
        rootView = (ViewGroup) inflater.inflate(R.layout.base_container, null);
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
        topView = rootView.findViewById(R.id.top_view);
        bottomView = rootView.findViewById(R.id.bottom_view);

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return onKeyUp(keyCode, event);
                }
                return false;
            }
        });
    }

    /**
     * It is called in order to create content
     */
    private void initContentView() {
        View contentView = createView(inflater);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, gravity
        );
        contentView.setLayoutParams(params);
        contentContainer.addView(contentView);
    }

    /**
     * It is called to set whether the dialog is cancellable by pressing back button or
     * touching the black overlay
     */
    private void initCancellable() {
        if (!isCancelable) {
            return;
        }
        topView.setOnTouchListener(onCancelableTouchListener);
        bottomView.setOnTouchListener(onCancelableTouchListener);
    }

    /**
     * It is called when the set the position of the dialog
     */
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

    /**
     * it is called when the content view is created
     *
     * @param inflater
     * @return any view which is passed
     */
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

    /**
     * This will be called in order to create footer view, if the view is given for footer,
     * it will be used directly, otherwise it will check the resourceId
     *
     * @return null if both footerViewResourceId and footerView is not set
     */
    private View getFooterView() {
        if (footerView != null) {
            return footerView;
        }
        if (footerViewResourceId != INVALID) {
            footerView = inflater.inflate(footerViewResourceId, rootView, false);
        }
        return footerView;
    }

    /**
     * This will be called in order to create header view, if the view is given for header,
     * it will be used directly, otherwise it will check the resourceId
     *
     * @return null if both headerViewResourceId and headerView is not set
     */
    private View getHeaderView() {
        if (headerView != null) {
            return headerView;
        }
        if (headerViewResourceId != INVALID) {
            headerView = inflater.inflate(headerViewResourceId, rootView, false);
        }
        return headerView;
    }

    /**
     * It is called when the show() method is called
     *
     * @param view is the dialog plus view
     */
    private void onAttached(View view) {
        decorView.addView(view);
    }

    /**
     * A key was released.
     * <p/>
     * <p>The default implementation handles KEYCODE_BACK to close the
     * dialog.
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
            onBackPressed();
            return true;
        }
        return false;
    }

    /**
     * Called when the dialog has detected the user's press of the back
     * key.  The default implementation simply cancels the dialog (only if
     * it is cancelable), but you can override this to do whatever you want.
     */
    protected void onBackPressed() {
        if (isCancelable) {
            dismiss();
        }
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
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
