package com.orhanobut.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.Arrays;

/**
 * @author Orhan Obut
 */
public class DialogPlusBuilder {
  private static final int INVALID = -1;

  private final int[] margin = new int[4];
  private final int[] padding = new int[4];
  private final int[] outMostMargin = new int[4];
  private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
  );

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
  private int defaultContentHeight;

  private DialogPlusBuilder() {
  }

  /**
   * Initialize the builder with a valid context in order to inflate the dialog
   */
  DialogPlusBuilder(Context context) {
    if (context == null) {
      throw new NullPointerException("Context may not be null");
    }
    this.context = context;
    Arrays.fill(margin, INVALID);
  }

  /**
   * Set the adapter that will be used when ListHolder or GridHolder are passed
   */
  public DialogPlusBuilder setAdapter(BaseAdapter adapter) {
    if (adapter == null) {
      throw new NullPointerException("Adapter may not be null");
    }
    this.adapter = adapter;
    return this;
  }

  /**
   * Set the footer view using the id of the layout resource
   */
  public DialogPlusBuilder setFooter(int resourceId) {
    this.footerViewResourceId = resourceId;
    return this;
  }

  /**
   * Set the footer view using a view
   */
  public DialogPlusBuilder setFooter(View view) {
    this.footerView = view;
    return this;
  }

  /**
   * Set the header view using the id of the layout resource
   */
  public DialogPlusBuilder setHeader(int resourceId) {
    this.headerViewResourceId = resourceId;
    return this;
  }

  /**
   * Set the header view using a view
   */
  public DialogPlusBuilder setHeader(View view) {
    this.headerView = view;
    return this;
  }

  /**
   * Define if the dialog is cancelable and should be closed when back pressed or click outside is pressed
   */
  public DialogPlusBuilder setCancelable(boolean isCancelable) {
    this.isCancelable = isCancelable;
    return this;
  }

  /**
   * Set the content of the dialog by passing one of the provided Holders
   */
  public DialogPlusBuilder setContentHolder(Holder holder) {
    this.holder = holder;
    return this;
  }

  /**
   * Set background color for your dialog. If no resource is passed 'white' will be used
   */
  public DialogPlusBuilder setBackgroundColorResourceId(int resourceId) {
    this.backgroundColorResourceId = resourceId;
    return this;
  }

  /**
   * Set the gravity you want the dialog to have among the ones that are provided
   */
  public DialogPlusBuilder setGravity(int gravity) {
    this.gravity = gravity;
    params.gravity = gravity;
    return this;
  }

  /**
   * Customize the in animation by passing an animation resource
   */
  public DialogPlusBuilder setInAnimation(int inAnimResource) {
    this.inAnimation = inAnimResource;
    return this;
  }

  /**
   * Customize the out animation by passing an animation resource
   */
  public DialogPlusBuilder setOutAnimation(int outAnimResource) {
    this.outAnimation = outAnimResource;
    return this;
  }

  /**
   * Add margins to your outmost view which contains everything. As default they are 0
   * are applied
   */
  public DialogPlusBuilder setOutMostMargin(int left, int top, int right, int bottom) {
    this.outMostMargin[0] = left;
    this.outMostMargin[1] = top;
    this.outMostMargin[2] = right;
    this.outMostMargin[3] = bottom;
    return this;
  }

  /**
   * Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins
   * are applied
   * <p/>
   * Use {@code setMargin}
   */
  @Deprecated
  public DialogPlusBuilder setMargins(int left, int top, int right, int bottom) {
    this.margin[0] = left;
    this.margin[1] = top;
    this.margin[2] = right;
    this.margin[3] = bottom;
    return this;
  }

  /**
   * Add margins to your dialog. They are set to 0 except when gravity is center. In that case basic margins
   * are applied
   */
  public DialogPlusBuilder setMargin(int left, int top, int right, int bottom) {
    this.margin[0] = left;
    this.margin[1] = top;
    this.margin[2] = right;
    this.margin[3] = bottom;
    return this;
  }

  /**
   * Set paddings for the dialog content
   */
  public DialogPlusBuilder setPadding(int left, int top, int right, int bottom) {
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
  public DialogPlusBuilder setOnItemClickListener(OnItemClickListener listener) {
    this.onItemClickListener = listener;
    return this;
  }

  /**
   * Set a global click listener to you dialog in order to handle all the possible click events. You can then
   * identify the view by using its id and handle the correct behaviour
   */
  public DialogPlusBuilder setOnClickListener(OnClickListener listener) {
    this.onClickListener = listener;
    return this;
  }

  public DialogPlusBuilder setOnDismissListener(OnDismissListener listener) {
    this.onDismissListener = listener;
    return this;
  }

  public DialogPlusBuilder setOnCancelListener(OnCancelListener listener) {
    this.onCancelListener = listener;
    return this;
  }

  public DialogPlusBuilder setOnBackPressListener(OnBackPressListener listener) {
    this.onBackPressListener = listener;
    return this;
  }

  public DialogPlusBuilder setExpanded(boolean expanded) {
    this.expanded = expanded;
    return this;
  }

  public DialogPlusBuilder setExpanded(boolean expanded, int defaultContentHeight) {
    this.expanded = expanded;
    this.defaultContentHeight = defaultContentHeight;
    return this;
  }

  public DialogPlusBuilder setContentHeight(int height) {
    params.height = height;
    return this;
  }

  public DialogPlusBuilder setContentWidth(int width) {
    params.width = width;
    return this;
  }

  /**
   * Create the dialog using this builder
   */
  public DialogPlus create() {
    init();
    return new DialogPlus(this);
  }

  private void init() {
    if (backgroundColorResourceId == INVALID) {
      backgroundColorResourceId = android.R.color.white;
    }
    getHolder().setBackgroundColor(backgroundColorResourceId);
  }

  public View getFooterView() {
    return Utils.getView(context, footerViewResourceId, footerView);
  }

  public View getHeaderView() {
    return Utils.getView(context, headerViewResourceId, headerView);
  }

  public Holder getHolder() {
    if (holder == null) {
      holder = new ListHolder();
    }
    return holder;
  }

  public Context getContext() {
    return context;
  }

  public BaseAdapter getAdapter() {
    return adapter;
  }

  public Animation getInAnimation() {
    int res = (inAnimation == INVALID) ? Utils.getAnimationResource(this.gravity, true) : inAnimation;
    return AnimationUtils.loadAnimation(context, res);
  }

  public Animation getOutAnimation() {
    int res = (outAnimation == INVALID) ? Utils.getAnimationResource(this.gravity, false) : outAnimation;
    return AnimationUtils.loadAnimation(context, res);
  }

  public FrameLayout.LayoutParams getContentParams() {
    if (expanded) {
      params.height = getDefaultContentHeight();
    }
    return params;
  }

  public boolean isExpanded() {
    return expanded;
  }

  public FrameLayout.LayoutParams getOutmostLayoutParams() {
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    );
    params.setMargins(outMostMargin[0], outMostMargin[1], outMostMargin[2], outMostMargin[3]);
    return params;
  }

  public boolean isCancelable() {
    return isCancelable;
  }

  public OnItemClickListener getOnItemClickListener() {
    return onItemClickListener;
  }

  public OnClickListener getOnClickListener() {
    return onClickListener;
  }

  public OnDismissListener getOnDismissListener() {
    return onDismissListener;
  }

  public OnCancelListener getOnCancelListener() {
    return onCancelListener;
  }

  public OnBackPressListener getOnBackPressListener() {
    return onBackPressListener;
  }

  public int[] getContentMargin() {
    int minimumMargin = context.getResources().getDimensionPixelSize(R.dimen.default_center_margin);
    for (int i = 0; i < margin.length; i++) {
      margin[i] = getMargin(this.gravity, margin[i], minimumMargin);
    }
    return margin;
  }

  public int[] getContentPadding() {
    return padding;
  }

  public int getDefaultContentHeight() {
    Activity activity = (Activity) context;
    Display display = activity.getWindowManager().getDefaultDisplay();
    int displayHeight = display.getHeight() - Utils.getStatusBarHeight(activity);
    if (defaultContentHeight == 0) {
      defaultContentHeight = (displayHeight * 2) / 5;
    }
    return defaultContentHeight;
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

}
