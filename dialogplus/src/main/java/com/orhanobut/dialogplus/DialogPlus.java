package com.orhanobut.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * DialogPlus base layout root view
     */
    private final ViewGroup rootView;

    /**
     * DialogPlus content container which is a different layout rather than base layout
     */
    private final ViewGroup contentContainer;

    /**
     * Determines the position of the dialog, only BOTTOM and TOP should be used, as default
     * it is BOTTOM
     */
    private final int gravity;

    /**
     * Either the content should fill the all screen or only the half, when the content reaches
     * the limit, it will be scrollable, BasicHolder cannot be scrollable, use it only for
     * a few items
     */
    private final ScreenType screenType;

    /**
     * Determines whether dialog should be dismissed by back button or touch in the black overlay
     */
    private final boolean isCancelable;

    /**
     * topView and bottomView are used to set the position of the dialog
     * If the position is top, bottomView will fill the screen, otherwise
     * topView will the screen
     */
    private final View topView;
    private final View bottomView;

    /**
     * footerView, headerView are used
     * to set header/footer for the dialog content
     */
    private final View footerView;
    private final View headerView;

    /**
     * Content adapter
     */
    private final BaseAdapter adapter;

    /**
     * Listener for the user to take action by clicking any item
     */
    private final OnItemClickListener onItemClickListener;

    /**
     * Content
     */
    private final Holder holder;

    /**
     * basically activity root view
     */
    private final ViewGroup decorView;
    private final LayoutInflater inflater;

    /**
     * Determines the content background color, as default it is white
     */
    private final int backgroundColorResourceId;

    public enum ScreenType {
        HALF, FULL
    }

    private DialogPlus(Builder builder) {
        inflater = LayoutInflater.from(builder.context);
        Activity activity = (Activity) builder.context;

        this.holder = getHolder(builder.holder);
        this.backgroundColorResourceId = builder.backgroundColorResourceId;
        this.headerView = getView(builder.headerViewResourceId, builder.headerView);
        this.footerView = getView(builder.footerViewResourceId, builder.footerView);
        this.screenType = builder.screenType;
        this.adapter = builder.adapter;
        this.onItemClickListener = builder.onItemClickListener;
        this.isCancelable = builder.isCancelable;
        this.gravity = builder.gravity;

        decorView = (ViewGroup) activity.getWindow().getDecorView();
        rootView = (ViewGroup) inflater.inflate(R.layout.base_container, null);
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
        topView = rootView.findViewById(R.id.top_view);
        bottomView = rootView.findViewById(R.id.bottom_view);

        createDialog();
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
        initViews();
        initContentView();
        initPosition();
        initCancellable();
    }


    /**
     * Initialize the appropriate views and also set for the back press button.
     */
    private void initViews() {
        if (backgroundColorResourceId != INVALID) {
            contentContainer.setBackgroundResource(backgroundColorResourceId);
        }
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
        holder.addFooter(footerView);
        holder.addHeader(headerView);
        if (adapter != null) {
            holder.setAdapter(adapter);
        }
        if (onItemClickListener != null) {
            holder.setOnItemClickListener(onItemClickListener);
        }
        return view;
    }

    /**
     * It is used to create content
     *
     * @return BasicHolder it setHolder is not called
     */
    private Holder getHolder(Holder holder) {
        if (holder == null) {
            holder = new BasicHolder();
        }
        return holder;
    }

    /**
     * This will be called in order to create view, if the given view is not null,
     * it will be used directly, otherwise it will check the resourceId
     *
     * @return null if both resourceId and view is not set
     */
    private View getView(int resourceId, View view) {
        if (view != null) {
            return view;
        }
        if (resourceId != INVALID) {
            view = inflater.inflate(resourceId, null);
        }
        return view;
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
        private BaseAdapter adapter;
        private Context context;
        private int footerViewResourceId = INVALID;
        private View footerView;
        private int headerViewResourceId = INVALID;
        private View headerView;
        private boolean isCancelable = true;
        private Holder holder;
        private int backgroundColorResourceId = INVALID;
        private int gravity = Gravity.BOTTOM;
        private ScreenType screenType = ScreenType.HALF;
        private OnItemClickListener onItemClickListener;

        private Builder() {
        }

        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("Context may not be null");
            }
            this.context = context;
        }

        public Builder setAdapter(BaseAdapter adapter) {
            if (adapter == null) {
                throw new NullPointerException("Adapter may not be null");
            }
            this.adapter = adapter;
            return this;
        }

        public Builder setFooter(int resourceId) {
            this.footerViewResourceId = resourceId;
            return this;
        }

        public Builder setFooter(View view) {
            this.footerView = view;
            return this;
        }

        public Builder setHeader(int resourceId) {
            this.headerViewResourceId = resourceId;
            return this;
        }

        public Builder setHeader(View view) {
            this.headerView = view;
            return this;
        }

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setHolder(Holder holder) {
            this.holder = holder;
            return this;
        }

        public Builder setBackgroundColorResourceId(int resourceId) {
            this.backgroundColorResourceId = resourceId;
            return this;
        }

        public Builder setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public Builder setScreenType(ScreenType screenType) {
            this.screenType = screenType;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
            return this;
        }

        public DialogPlus create() {
            return new DialogPlus(this);
        }
    }
}
