package com.orhanobut.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

/**
 * @author Orhan Obut
 */
public class DialogPlus {

    /**
     * Custom values for DialogPlus gravity
     */
    public enum Gravity {
        TOP, BOTTOM, CENTER
    }

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
    private final Gravity gravity;

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
     * Determines whether dialog is showing dismissing animation and avoid to repeat it
     */
    private boolean isDismissing;

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
     * Listener for the user to take action by clicking views in header or footer
     */
    private final OnClickListener onClickListener;

    /**
     * Listener to notify the user that dialog has been dismissed
     * */
    private final OnDismissListener onDismissListener;

    /**
     * Listener to notify the user that dialog has been canceled
     * */
    private final OnCancelListener onCancelListener;

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

    /**
     * Determines the in and out animation of the dialog. Default animation are bottom sliding animations
     */
    private final int inAnimationResource;
    private final int outAnimationResource;

    /**
     * Current status that PlusDialog being canceled by user or not
     * the value goes true when {@link #onCancelableTouchListener} or {@link #holder#setOnKeyListener} is invoked.
     *
     * note that {@link #onDismissListener} won't be invoked when the value is true, even though {@link OnDismissListener} is not null.
     * */
    private boolean isCanceled = false;

    /**
     * Determine the margin that the dialog should have
     */
    private final int marginLeft;
    private final int marginTop;
    private final int marginRight;
    private final int marginBottom;

    public enum ScreenType {
        HALF, FULL
    }

    private DialogPlus(Builder builder) {
        inflater = LayoutInflater.from(builder.context);
        Activity activity = (Activity) builder.context;

        holder = getHolder(builder.holder);

        int backgroundColor = builder.backgroundColorResourceId;
        backgroundColorResourceId = (backgroundColor == INVALID) ? android.R.color.white : backgroundColor;
        headerView = getView(builder.headerViewResourceId, builder.headerView);
        footerView = getView(builder.footerViewResourceId, builder.footerView);
        screenType = builder.screenType;
        adapter = builder.adapter;
        onItemClickListener = builder.onItemClickListener;
        onClickListener = builder.onClickListener;
        onDismissListener = builder.onDismissListener;
        onCancelListener = builder.onCancelListener;
        isCancelable = builder.isCancelable;
        gravity = builder.gravity;

        int inAnimation = builder.inAnimation;
        int outAnimation = builder.outAnimation;
        inAnimationResource = (inAnimation == INVALID) ? getAnimationResource(this.gravity, true) : inAnimation;
        outAnimationResource = (outAnimation == INVALID) ? getAnimationResource(this.gravity, false) : outAnimation;

        int minimumMargin = activity.getResources().getDimensionPixelSize(R.dimen.default_center_margin);
        marginLeft = getMargin(this.gravity, builder.marginLeft, minimumMargin);
        marginTop = getMargin(this.gravity, builder.marginTop, minimumMargin);
        marginRight = getMargin(this.gravity, builder.marginRight, minimumMargin);
        marginBottom = getMargin(this.gravity, builder.marginBottom, minimumMargin);

        /**
         * Avoid getting directly from the decor view because by doing that we are overlapping the black soft key on
         * nexus device. I think it should be tested on different devices but in my opinion is the way to go.
         * @link http://stackoverflow.com/questions/4486034/get-root-view-from-current-activity
         */
        decorView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) inflater.inflate(R.layout.base_container, null);
        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
        topView = rootView.findViewById(R.id.top_view);
        bottomView = rootView.findViewById(R.id.bottom_view);

        createDialog();
    }

    /**
     * Get default animation resource when not defined by the user
     *
     * @param gravity       the gravity of the dialog
     * @param isInAnimation determine if is in or out animation. true when is is
     *
     * @return the id of the animation resource
     */
    private int getAnimationResource(Gravity gravity, boolean isInAnimation) {
        switch (gravity) {
            case TOP:
                return isInAnimation ? R.anim.slide_in_top : R.anim.slide_out_top;
            case BOTTOM:
                return isInAnimation ? R.anim.slide_in_bottom : R.anim.slide_out_bottom;
            case CENTER:
                return isInAnimation ? R.anim.fade_in_center : R.anim.fade_out_center;
            default:
                // This case is not implemented because we don't expect any other gravity at the moment
        }
        return INVALID;
    }

    /**
     * Get margins if provided or assign default values based on gravity
     *
     * @param gravity       the gravity of the dialog
     * @param margin        the value defined in the builder
     * @param minimumMargin the minimum margin when gravity center is selected
     *
     * @return the value of the margin
     */
    private int getMargin(Gravity gravity, int margin, int minimumMargin) {
        switch (gravity) {
            case TOP:
                // Fall Through
            case BOTTOM:
                return (margin == INVALID) ? 0 : margin;
            case CENTER:
                return (margin == INVALID) ? minimumMargin : margin;
            default:
                // This case is not implemented because we don't expect any other gravity at the moment
        }
        return 0;
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
        if (isDismissing) {
            return;
        }

        Context context = decorView.getContext();
        Animation outAnim = AnimationUtils.loadAnimation(context, this.outAnimationResource);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(rootView);
                        isDismissing = false;
                        if (onDismissListener == null) {
                            return;
                        }

                        if (isCanceled) {
                            // won't callback to dismiss listener, maybe next time. reset the value.
                            isCanceled = false;
                            return;
                        }
                        onDismissListener.onDismiss(DialogPlus.this);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contentContainer.startAnimation(outAnim);
        isDismissing = true;
    }

    /**
     * It creates the dialog
     */
    private void createDialog() {
        initContentView();
        initPosition();
        initCancelable();
    }

    /**
     * It is called in order to create content
     */
    private void initContentView() {
        int convertedGravity = getGravity();
        View contentView = createView(inflater);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, convertedGravity
        );
        params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        contentView.setLayoutParams(params);
        contentContainer.addView(contentView);
    }

    /**
     * Convert DialogPlusGravity with content layout readable gravity
     *
     * @return the converted layout gravity that can be passed to the container
     */
    private int getGravity() {
        switch (gravity) {
            case TOP:
                return android.view.Gravity.TOP;
            case BOTTOM:
                return android.view.Gravity.BOTTOM;
            default:
                return android.view.Gravity.CENTER;
        }
    }

    /**
     * It is called to set whether the dialog is cancellable by pressing back button or
     * touching the black overlay
     */
    private void initCancelable() {
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
            case TOP:
                bottomView.setVisibility(View.VISIBLE);
                topView.setVisibility(View.GONE);
                break;
            case BOTTOM:
                bottomView.setVisibility(View.GONE);
                topView.setVisibility(View.VISIBLE);
                break;
            default:
                bottomView.setVisibility(View.VISIBLE);
                topView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * it is called when the content view is created
     *
     * @param inflater  used to inflate the content of the dialog
     *
     * @return any view which is passed
     */
    private View createView(LayoutInflater inflater) {
        holder.setBackgroundColor(backgroundColorResourceId);
        View view = holder.getView(inflater, rootView);

        if (holder instanceof ViewHolder) {
            assignClickListenerRecursively(view);
        }

        if (adapter != null && holder instanceof FixedHeaderViewHolderAdapter) {
            FixedHeaderViewHolderAdapter holderAdapter = (FixedHeaderViewHolderAdapter) holder;
            holderAdapter.setAdapter(adapter);
            int height = (ScreenUtil.getDisplaySize(inflater.getContext()).y - ScreenUtil.getNavigationBarHeight(inflater.getContext(), Configuration.ORIENTATION_PORTRAIT)) / 2;
            holderAdapter.addFixedHeader(inflater.inflate(R.layout.fixed_header, (ViewGroup) view.getParent(), true), height);
            holderAdapter.addHeader(getTransparencySpacerView(inflater, height));
            holderAdapter.setOnItemClickListener(new OnHolderListener() {
                @Override
                public void onItemClick(Object item, View view, int position) {
                    if (position == 0) {
                        isCanceled = true;
                        dismiss();
                        if (onCancelListener != null) {
                            onCancelListener.onCancel(DialogPlus.this);
                        }
                    } else {
                        if (onItemClickListener == null) {
                            return;
                        }
                        onItemClickListener.onItemClick(DialogPlus.this, item, view, position);
                    }
                }
            });
        }

        assignClickListenerRecursively(headerView);
        holder.addHeader(headerView);

        assignClickListenerRecursively(footerView);
        holder.addFooter(footerView);

        if (adapter != null && holder instanceof HolderAdapter && !(holder instanceof FixedHeaderViewHolderAdapter)) {
            HolderAdapter holderAdapter = (HolderAdapter) holder;
            holderAdapter.setAdapter(adapter);
            holderAdapter.setOnItemClickListener(new OnHolderListener() {
                @Override
                public void onItemClick(Object item, View view, int position) {
                    if (onItemClickListener == null) {
                        return;
                    }
                    onItemClickListener.onItemClick(DialogPlus.this, item, view, position);
                }
            });
        }

        return view;
    }

    private View getTransparencySpacerView(LayoutInflater inflater, int height) {
        View v = inflater.inflate(R.layout.transparency_header, null, false);
        v.setTag("TransparencyView");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, height);
        v.setLayoutParams(params);
        v.setBackgroundColor(Color.TRANSPARENT);
        return v;
    }

    /**
     * Loop among the views in the hierarchy and assign listener to them
     */
    public void assignClickListenerRecursively(View parent) {
        if (parent == null) {
            return;
        }

        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            int childCount = viewGroup.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                View child = viewGroup.getChildAt(i);
                assignClickListenerRecursively(child);
            }
        }
        setClickListener(parent);
    }

    /**
     * It is used to setListener on view that have a valid id associated
     */
    private void setClickListener(View view) {
        if (view.getId() == INVALID) {
            return;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) {
                    return;
                }
                onClickListener.onClick(DialogPlus.this, v);
            }
        });
    }

    /**
     * It is used to create content
     *
     * @param holder the holder from the builder
     *
     * @return ListHolder if setContentHolder is not called
     */
    private Holder getHolder(Holder holder) {
        if (holder == null) {
            holder = new ListHolder();
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
        Context context = decorView.getContext();
        Animation inAnim = AnimationUtils.loadAnimation(context, this.inAnimationResource);
        contentContainer.startAnimation(inAnim);

        contentContainer.requestFocus();
            holder.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getAction()) {
                    case KeyEvent.ACTION_UP:
                        if (keyCode == KeyEvent.KEYCODE_BACK && isCancelable) {
                            isCanceled = true;
                            dismiss();
                            if (onCancelListener != null) {
                                onCancelListener.onCancel(DialogPlus.this);
                            }
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isCanceled = true;
                dismiss();
                if (onCancelListener != null) {
                    onCancelListener.onCancel(DialogPlus.this);
                }
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
        private View footerView;
        private View headerView;
        private Holder holder;
        private Gravity gravity = Gravity.BOTTOM;
        private ScreenType screenType = ScreenType.HALF;
        private OnItemClickListener onItemClickListener;
        private OnClickListener onClickListener;
        private OnDismissListener onDismissListener;
        private OnCancelListener onCancelListener;

        private boolean isCancelable = true;
        private int backgroundColorResourceId = INVALID;
        private int headerViewResourceId = INVALID;
        private int footerViewResourceId = INVALID;
        private int inAnimation = INVALID;
        private int outAnimation = INVALID;
        private int marginLeft = INVALID;
        private int marginTop = INVALID;
        private int marginRight = INVALID;
        private int marginBottom = INVALID;

        private Builder() {
        }

        /**
         * Initialize the builder with a valid context in order to inflate the dialog
         */
        public Builder(Context context) {
            if (context == null) {
                throw new NullPointerException("Context may not be null");
            }
            this.context = context;
        }

        /**
         * Set the adapter that will be used when ListHolder or GridHolder are passed
         */
        public Builder setAdapter(BaseAdapter adapter) {
            if (adapter == null) {
                throw new NullPointerException("Adapter may not be null");
            }
            this.adapter = adapter;
            return this;
        }

        /**
         * Set the footer view using the id of the layout resource
         */
        public Builder setFooter(int resourceId) {
            this.footerViewResourceId = resourceId;
            return this;
        }

        /**
         * Set the footer view using a view
         */
        public Builder setFooter(View view) {
            this.footerView = view;
            return this;
        }

        /**
         * Set the header view using the id of the layout resource
         */
        public Builder setHeader(int resourceId) {
            this.headerViewResourceId = resourceId;
            return this;
        }

        /**
         * Set the header view using a view
         */
        public Builder setHeader(View view) {
            this.headerView = view;
            return this;
        }

        /**
         * Define if the dialog is cancelable and should be closed when back pressed or click outside is pressed
         */
        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        /**
         * Set the content of the dialog by passing one of the provided Holders
         */
        public Builder setContentHolder(Holder holder) {
            this.holder = holder;
            return this;
        }

        /**
         * Set background color for your dialog. If no resource is passed 'white' will be used
         */
        public Builder setBackgroundColorResourceId(int resourceId) {
            this.backgroundColorResourceId = resourceId;
            return this;
        }

        /**
         * Set the gravity you want the dialog to have among the ones that are provided
         */
        public Builder setGravity(Gravity gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Customize the in animation by passing an animation resource
         */
        public Builder setInAnimation(int inAnimResource) {
            this.inAnimation = inAnimResource;
            return this;
        }

        /**
         * Customize the out animation by passing an animation resource
         */
        public Builder setOutAnimation(int outAnimResource) {
            this.outAnimation = outAnimResource;
            return this;
        }

        /**
         * Set how much big you want the dialog to be (full screen or half screen)
         */
        public Builder setScreenType(ScreenType screenType) {
            this.screenType = screenType;
            return this;
        }

        /**
         * Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins
         * are applied
         */
        public Builder setMargins(int left, int top, int right, int bottom) {
            this.marginLeft = left;
            this.marginTop = top;
            this.marginRight = right;
            this.marginBottom = bottom;
            return this;
        }

        /**
         * Set an item click listener when list or grid holder is chosen. In that way you can have callbacks when one
         * of your items is clicked
         */
        public Builder setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = listener;
            return this;
        }

        /**
         * Set a global click listener to you dialog in order to handle all the possible click events. You can then
         * identify the view by using its id and handle the correct behaviour
         */
        public Builder setOnClickListener(OnClickListener listener) {
            this.onClickListener = listener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener listener) {
            this.onDismissListener = listener;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener listener) {
            this.onCancelListener = listener;
            return this;
        }

        /**
         * Create the dialog using this builder
         */
        public DialogPlus create() {
            return new DialogPlus(this);
        }
    }
}
