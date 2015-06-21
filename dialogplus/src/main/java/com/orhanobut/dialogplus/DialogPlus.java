package com.orhanobut.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.Arrays;

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
     * Determines whether dialog should be dismissed by back button or touch in the black overlay
     */
    private final boolean isCancelable;

    /**
     * Determines whether dialog is showing dismissing animation and avoid to repeat it
     */
    private boolean isDismissing;

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
     */
    private final OnDismissListener onDismissListener;

    /**
     * Listener to notify the user that dialog has been canceled
     */
    private final OnCancelListener onCancelListener;

    /**
     * Listener to notify back press
     */
    private final OnBackPressListener onBackPressListener;

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
     * Determine the margin that the dialog should have
     */
    private final int[] margin = new int[4];

    /**
     * Determine the padding that the dialog should have
     */
    private final int[] padding = new int[4];

    private final int displayHeight;
    private final int defaultContentHeight;

    /**
     * Determines to use scroll animation
     */
    private final boolean expanded;

    private Context context;

    private DialogPlus(Builder builder) {
        inflater = LayoutInflater.from(builder.context);
        this.context = builder.context;

        Activity activity = (Activity) builder.context;

        Display display = activity.getWindowManager().getDefaultDisplay();
        displayHeight = display.getHeight() - Utils.getStatusBarHeight(activity);
        defaultContentHeight = (displayHeight * 2) / 5;

        holder = getHolder(builder.holder);

        int backgroundColor = builder.backgroundColorResourceId;
        backgroundColorResourceId = (backgroundColor == INVALID) ? android.R.color.white : backgroundColor;
        headerView = getView(builder.headerViewResourceId, builder.headerView);
        footerView = getView(builder.footerViewResourceId, builder.footerView);
        adapter = builder.adapter;
        onItemClickListener = builder.onItemClickListener;
        onClickListener = builder.onClickListener;
        onDismissListener = builder.onDismissListener;
        onCancelListener = builder.onCancelListener;
        onBackPressListener = builder.onBackPressListener;
        isCancelable = builder.isCancelable;
        gravity = builder.gravity;
        expanded = builder.expanded;

        int inAnimation = builder.inAnimation;
        int outAnimation = builder.outAnimation;
        inAnimationResource = (inAnimation == INVALID) ? getAnimationResource(this.gravity, true) : inAnimation;
        outAnimationResource = (outAnimation == INVALID) ? getAnimationResource(this.gravity, false) : outAnimation;

        int minimumMargin = activity.getResources().getDimensionPixelSize(R.dimen.default_center_margin);
        for (int i = 0; i < margin.length; i++) {
            margin[i] = getMargin(this.gravity, builder.margin[i], minimumMargin);
        }

        System.arraycopy(builder.padding, 0, padding, 0, padding.length);

        int[] outMostMargin = new int[4];
        System.arraycopy(builder.outMostMargin, 0, outMostMargin, 0, outMostMargin.length);

        /**
         * Avoid getting directly from the decor view because by doing that we are overlapping the black soft key on
         * nexus device. I think it should be tested on different devices but in my opinion is the way to go.
         * @link http://stackoverflow.com/questions/4486034/get-root-view-from-current-activity
         */
        decorView = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) inflater.inflate(R.layout.base_container, null);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.setMargins(outMostMargin[0], outMostMargin[1], outMostMargin[2], outMostMargin[3]);
        rootView.setLayoutParams(params);

        contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);

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
                        if (onDismissListener != null) {
                            onDismissListener.onDismiss(DialogPlus.this);
                        }
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

    @SuppressWarnings("unused")
    public View findViewById(int resourceId) {
        return contentContainer.findViewById(resourceId);
    }

    public View getHeaderView() {
        return headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    public View getHolderView() {
        return holder.getInflatedView();
    }

    /**
     * Get default animation resource when not defined by the user
     *
     * @param gravity       the gravity of the dialog
     * @param isInAnimation determine if is in or out animation. true when is is
     * @return the id of the animation resource
     */
    private int getAnimationResource(int gravity, boolean isInAnimation) {
        switch (gravity) {
            case Gravity.TOP:
                return isInAnimation ? R.anim.slide_in_top : R.anim.slide_out_top;
            case Gravity.BOTTOM:
                return isInAnimation ? R.anim.slide_in_bottom : R.anim.slide_out_bottom;
            case Gravity.CENTER:
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
     * @return the value of the margin
     */
    private int getMargin(int gravity, int margin, int minimumMargin) {
        switch (gravity) {
            case Gravity.CENTER:
                return (margin == INVALID) ? minimumMargin : margin;
            default:
                return (margin == INVALID) ? 0 : margin;
        }
    }

    /**
     * It creates the dialog
     */
    private void createDialog() {
        initContentView();
        initCancelable();
        if (expanded) {
            initExpandAnimator();
        }
    }

    private void initExpandAnimator() {
        final View view = holder.getInflatedView();
        if (!(view instanceof AbsListView)) {
            return;
        }
        final AbsListView absListView = (AbsListView) view;

        Activity activity = (Activity) context;
        view.setOnTouchListener(ExpandTouchListener.newListener(
                activity, absListView, contentContainer, gravity, displayHeight, defaultContentHeight
        ));
    }

    /**
     * It is called in order to create content
     */
    private void initContentView() {
        int height = expanded ? defaultContentHeight : ViewGroup.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height, gravity
        );
        contentContainer.setLayoutParams(params1);
        View contentView = createView(inflater);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        params.setMargins(margin[0], margin[1], margin[2], margin[3]);
        contentView.setLayoutParams(params);
        getHolderView().setPadding(padding[0], padding[1], padding[2], padding[3]);
        contentContainer.addView(contentView);
    }

    /**
     * It is called to set whether the dialog is cancellable by pressing back button or
     * touching the black overlay
     */
    private void initCancelable() {
        if (!isCancelable) {
            return;
        }
        View view = rootView.findViewById(R.id.outmost_container);
        view.setOnTouchListener(onCancelableTouchListener);
    }

    /**
     * it is called when the content view is created
     *
     * @param inflater used to inflate the content of the dialog
     * @return any view which is passed
     */
    private View createView(LayoutInflater inflater) {
        holder.setBackgroundColor(backgroundColorResourceId);
        View view = holder.getView(inflater, rootView);

        if (holder instanceof ViewHolder) {
            assignClickListenerRecursively(view);
        }

        assignClickListenerRecursively(headerView);
        holder.addHeader(headerView);

        assignClickListenerRecursively(footerView);
        holder.addFooter(footerView);

        if (adapter != null && holder instanceof HolderAdapter) {
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

    /**
     * Loop among the views in the hierarchy and assign listener to them
     */
    private void assignClickListenerRecursively(View parent) {
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
        //adapterview does not support click listener
        if (view instanceof AdapterView) {
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
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (onBackPressListener != null) {
                                onBackPressListener.onBackPressed(DialogPlus.this);
                            }
                            if (isCancelable) {
                                onBackPressed(DialogPlus.this);
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
     * Dismiss the dialog when the user press the back button
     *
     * @param dialogPlus is the current dialog
     */
    public void onBackPressed(DialogPlus dialogPlus) {
        if (onCancelListener != null) {
            onCancelListener.onCancel(DialogPlus.this);
        }
        dismiss();
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (onCancelListener != null) {
                    onCancelListener.onCancel(DialogPlus.this);
                }
                dismiss();
            }
            return false;
        }
    };

    /**
     * Use this builder to create a dialog
     */
    public static class Builder {
        private final int[] margin = new int[4];
        private final int[] padding = new int[4];
        private final int[] outMostMargin = new int[4];

        private BaseAdapter adapter;
        private Context context;
        private View footerView;
        private View headerView;
        private Holder holder;
        private int gravity = Gravity.BOTTOM;
        private OnItemClickListener onItemClickListener;
        private OnClickListener onClickListener;
        private OnDismissListener onDismissListener;
        private OnCancelListener onCancelListener;
        private OnBackPressListener onBackPressListener;

        private boolean isCancelable = true;
        private int backgroundColorResourceId = INVALID;
        private int headerViewResourceId = INVALID;
        private int footerViewResourceId = INVALID;
        private int inAnimation = INVALID;
        private int outAnimation = INVALID;
        private boolean expanded;

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
            Arrays.fill(margin, INVALID);
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
        public Builder setGravity(int gravity) {
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
         * Add margins to your outmost view which contains everything. As default they are 0
         * are applied
         */
        public Builder setOutMostMargin(int left, int top, int right, int bottom) {
            this.outMostMargin[0] = left;
            this.outMostMargin[1] = top;
            this.outMostMargin[2] = right;
            this.outMostMargin[3] = bottom;
            return this;
        }

        /**
         * Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins
         * are applied
         */
        public Builder setMargins(int left, int top, int right, int bottom) {
            this.margin[0] = left;
            this.margin[1] = top;
            this.margin[2] = right;
            this.margin[3] = bottom;
            return this;
        }

        /**
         * Set paddings for the dialog content
         */
        public Builder setPadding(int left, int top, int right, int bottom) {
            this.padding[0] = left;
            this.padding[1] = top;
            this.padding[2] = right;
            this.padding[3] = bottom;
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

        public Builder setOnBackPressListener(OnBackPressListener listener) {
            this.onBackPressListener = listener;
            return this;
        }

        public Builder setExpanded(boolean expanded) {
            this.expanded = expanded;
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
