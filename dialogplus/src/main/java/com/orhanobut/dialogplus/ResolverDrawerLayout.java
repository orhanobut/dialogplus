package com.orhanobut.dialogplus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.OverScroller;

class ResolverDrawerLayout extends ViewGroup {
    private static final String TAG = "ResolverDrawerLayout";

    /**
     * Max width of the whole drawer layout
     */
    private int maxWidth;

    /**
     * Max total visible height of views not marked always-show when in the closed/initial state
     */
    private int maxCollapsedHeight;

    /**
     * Max total visible height of views not marked always-show when in the closed/initial state
     * when a default option is present
     */
    private int maxCollapsedHeightSmall;

    private boolean smallCollapsed;

    /**
     * Move views down from the top by this much in px
     */
    private float collapseOffset;

    private int collapsibleHeight;
    private int uncollapsibleHeight;

    private int topOffset;

    private boolean isDragging;
    private boolean openOnClick;
    private boolean openOnLayout;
    private boolean dismissOnScrollerFinished;
    private final int touchSlop;
    private final float minFlingVelocity;
    private final OverScroller scroller;
    private final VelocityTracker velocityTracker;

    private OnDismissedListener onDismissedListener;
    private RunOnDismissedListener runOnDismissedListener;

    private float initialTouchX;
    private float initialTouchY;
    private float lastTouchY;
    private int activePointerId = MotionEvent.INVALID_POINTER_ID;

    private final Rect tempRect = new Rect();

    private final ViewTreeObserver.OnTouchModeChangeListener touchModeChangeListener =
            new ViewTreeObserver.OnTouchModeChangeListener() {
                @Override
                public void onTouchModeChanged(boolean isInTouchMode) {
                    if (!isInTouchMode && hasFocus() && isDescendantClipped(getFocusedChild())) {
                        smoothScrollTo(0, 0);
                    }
                }
            };

    public ResolverDrawerLayout(Context context) {
        this(context, null);
    }

    public ResolverDrawerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ResolverDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ResolverDrawerLayout,
                defStyleAttr, 0);
        maxWidth = a.getDimensionPixelSize(R.styleable.ResolverDrawerLayout_maxWidth, -1);
        maxCollapsedHeight = a.getDimensionPixelSize(
                R.styleable.ResolverDrawerLayout_maxCollapsedHeight, 0);
        maxCollapsedHeightSmall = a.getDimensionPixelSize(
                R.styleable.ResolverDrawerLayout_maxCollapsedHeightSmall,
                maxCollapsedHeight);
        a.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            scroller = new OverScroller(context, AnimationUtils.loadInterpolator(context, android.R.interpolator.decelerate_quint));
        } else {
            Interpolator interpolator = new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    return 2.5f;
                }
            };
            scroller = new OverScroller(context, interpolator);
        }
        velocityTracker = VelocityTracker.obtain();

        final ViewConfiguration vc = ViewConfiguration.get(context);
        touchSlop = vc.getScaledTouchSlop();
        minFlingVelocity = vc.getScaledMinimumFlingVelocity();
    }

    public void setSmallCollapsed(boolean smallCollapsed) {
        this.smallCollapsed = smallCollapsed;
        requestLayout();
    }

    public boolean isSmallCollapsed() {
        return smallCollapsed;
    }

    public boolean isCollapsed() {
        return collapseOffset > 0;
    }

    private boolean isMoving() {
        return isDragging || !scroller.isFinished();
    }

    private int getMaxCollapsedHeight() {
        return isSmallCollapsed() ? maxCollapsedHeightSmall : maxCollapsedHeight;
    }

    public void setOnDismissedListener(OnDismissedListener listener) {
        onDismissedListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            velocityTracker.clear();
        }

        velocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                initialTouchX = x;
                initialTouchY = lastTouchY = y;
                openOnClick = isListChildUnderClipped(x, y) && collapsibleHeight > 0;
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();
                final float dy = y - initialTouchY;
                boolean isActiveMoving;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    isActiveMoving = Math.abs(dy) > touchSlop && findChildUnder(x, y) != null && (getNestedScrollAxes() & SCROLL_AXIS_VERTICAL) == 0;
                } else {
                    isActiveMoving = Math.abs(dy) > touchSlop && findChildUnder(x, y) != null;
                }

                if (isActiveMoving) {
                    activePointerId = ev.getPointerId(0);
                    isDragging = true;
                    lastTouchY = Math.max(lastTouchY - touchSlop,
                            Math.min(lastTouchY + dy, lastTouchY + touchSlop));
                }
            }
            break;

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
            }
            break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                resetTouch();
            }
            break;
        }

        if (isDragging) {
            abortAnimation();
        }
        return isDragging || openOnClick;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();

        velocityTracker.addMovement(ev);

        boolean handled = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                initialTouchX = x;
                initialTouchY = lastTouchY = y;
                activePointerId = ev.getPointerId(0);
                final boolean hitView = findChildUnder(initialTouchX, initialTouchY) != null;
                handled = (!hitView && onDismissedListener != null) || collapsibleHeight > 0;
                isDragging = hitView && handled;
                abortAnimation();
            }
            break;

            case MotionEvent.ACTION_MOVE: {
                int index = ev.findPointerIndex(activePointerId);
                if (index < 0) {
                    Log.e(TAG, "Bad pointer id " + activePointerId + ", resetting");
                    index = 0;
                    activePointerId = ev.getPointerId(0);
                    initialTouchX = ev.getX();
                    initialTouchY = lastTouchY = ev.getY();
                }
                final float x = ev.getX(index);
                final float y = ev.getY(index);
                if (!isDragging) {
                    final float dy = y - initialTouchY;
                    if (Math.abs(dy) > touchSlop && findChildUnder(x, y) != null) {
                        handled = isDragging = true;
                        lastTouchY = Math.max(lastTouchY - touchSlop,
                                Math.min(lastTouchY + dy, lastTouchY + touchSlop));
                    }
                }
                if (isDragging) {
                    final float dy = y - lastTouchY;
                    performDrag(dy);
                }
                lastTouchY = y;
            }
            break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                activePointerId = pointerId;
                initialTouchX = ev.getX(pointerIndex);
                initialTouchY = lastTouchY = ev.getY(pointerIndex);
            }
            break;

            case MotionEvent.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
            }
            break;

            case MotionEvent.ACTION_UP: {
                final boolean wasDragging = isDragging;
                isDragging = false;
                if (!wasDragging && findChildUnder(initialTouchX, initialTouchY) == null &&
                        findChildUnder(ev.getX(), ev.getY()) == null) {
                    if (onDismissedListener != null) {
                        dispatchOnDismissed();
                        resetTouch();
                        return true;
                    }
                }
                if (openOnClick && Math.abs(ev.getX() - initialTouchX) < touchSlop &&
                        Math.abs(ev.getY() - initialTouchY) < touchSlop) {
                    smoothScrollTo(0, 0);
                    return true;
                }
                velocityTracker.computeCurrentVelocity(1000);
                final float yvel = velocityTracker.getYVelocity(activePointerId);
                if (Math.abs(yvel) > minFlingVelocity) {
                    if (onDismissedListener != null
                            && yvel > 0 && collapseOffset > collapsibleHeight) {
                        smoothScrollTo(collapsibleHeight + uncollapsibleHeight, yvel);
                        dismissOnScrollerFinished = true;
                    } else {
                        smoothScrollTo(yvel < 0 ? 0 : collapsibleHeight, yvel);
                    }
                } else {
                    smoothScrollTo(
                            collapseOffset < collapsibleHeight / 2 ? 0 : collapsibleHeight, 0);
                }
                resetTouch();
            }
            break;

            case MotionEvent.ACTION_CANCEL: {
                if (isDragging) {
                    smoothScrollTo(
                            collapseOffset < collapsibleHeight / 2 ? 0 : collapsibleHeight, 0);
                }
                resetTouch();
                return true;
            }
        }

        return handled;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == activePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            initialTouchX = ev.getX(newPointerIndex);
            initialTouchY = lastTouchY = ev.getY(newPointerIndex);
            activePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private void resetTouch() {
        activePointerId = MotionEvent.INVALID_POINTER_ID;
        isDragging = false;
        openOnClick = false;
        initialTouchX = initialTouchY = lastTouchY = 0;
        velocityTracker.clear();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            final boolean keepGoing = !scroller.isFinished();
            performDrag(scroller.getCurrY() - collapseOffset);
            if (keepGoing) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    postInvalidateOnAnimation();
                } else {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            } else if (dismissOnScrollerFinished && onDismissedListener != null) {
                runOnDismissedListener = new RunOnDismissedListener();
                post(runOnDismissedListener);
            }
        }
    }

    private void abortAnimation() {
        scroller.abortAnimation();
        runOnDismissedListener = null;
        dismissOnScrollerFinished = false;
    }

    private float performDrag(float dy) {
        final float newPos = Math.max(0, Math.min(collapseOffset + dy,
                collapsibleHeight + uncollapsibleHeight));
        if (newPos != collapseOffset) {
            dy = newPos - collapseOffset;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (!lp.ignoreOffset) {
                    child.offsetTopAndBottom((int) dy);
                }
            }
            final boolean isCollapsedOld = collapseOffset != 0;
            collapseOffset = newPos;
            topOffset += dy;
            final boolean isCollapsedNew = newPos != 0;
            ViewCompat.postInvalidateOnAnimation(this);
            return dy;
        }
        return 0;
    }

    void dispatchOnDismissed() {
        if (onDismissedListener != null) {
            onDismissedListener.onDismissed();
        }
        if (runOnDismissedListener != null) {
            removeCallbacks(runOnDismissedListener);
            runOnDismissedListener = null;
        }
    }

    private void smoothScrollTo(int yOffset, float velocity) {
        abortAnimation();
        final int sy = (int) collapseOffset;
        int dy = yOffset - sy;
        if (dy == 0) {
            return;
        }

        final int height = getHeight();
        final int halfHeight = height / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dy) / height);
        final float distance = halfHeight + halfHeight *
                distanceInfluenceForSnapDuration(distanceRatio);

        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageDelta = (float) Math.abs(dy) / height;
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, 300);

        scroller.startScroll(0, sy, 0, dy, duration);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postInvalidateOnAnimation();
        } else {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    /**
     * Note: this method doesn't take Z into account for overlapping views
     * since it is only used in contexts where this doesn't affect the outcome.
     */
    private View findChildUnder(float x, float y) {
        return findChildUnder(this, x, y);
    }

    private static View findChildUnder(ViewGroup parent, float x, float y) {
        final int childCount = parent.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            final View child = parent.getChildAt(i);
            if (isChildUnder(child, x, y)) {
                return child;
            }
        }
        return null;
    }

    private View findListChildUnder(float x, float y) {
        View v = findChildUnder(x, y);
        while (v != null) {
            x -= getX(v);
            y -= getY(v);
            if (v instanceof AbsListView) {
                // One more after this.
                return findChildUnder((ViewGroup) v, x, y);
            }
            v = v instanceof ViewGroup ? findChildUnder((ViewGroup) v, x, y) : null;
        }
        return v;
    }

    /**
     * This only checks clipping along the bottom edge.
     */
    private boolean isListChildUnderClipped(float x, float y) {
        final View listChild = findListChildUnder(x, y);
        return listChild != null && isDescendantClipped(listChild);
    }

    private boolean isDescendantClipped(View child) {
        tempRect.set(0, 0, child.getWidth(), child.getHeight());
        offsetDescendantRectToMyCoords(child, tempRect);
        View directChild;
        if (child.getParent() == this) {
            directChild = child;
        } else {
            View v = child;
            ViewParent p = child.getParent();
            while (p != this) {
                v = (View) p;
                p = v.getParent();
            }
            directChild = v;
        }

        // ResolverDrawerLayout lays out vertically in child order;
        // the next view and forward is what to check against.
        int clipEdge = getHeight() - getPaddingBottom();
        final int childCount = getChildCount();
        for (int i = indexOfChild(directChild) + 1; i < childCount; i++) {
            final View nextChild = getChildAt(i);
            if (nextChild.getVisibility() == GONE) {
                continue;
            }
            clipEdge = Math.min(clipEdge, nextChild.getTop());
        }
        return tempRect.bottom > clipEdge;
    }

    private static float getX(View child) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? child.getX() : ViewCompat.getX(child);
    }

    private static float getY(View child) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? child.getY() : ViewCompat.getY(child);
    }

    private static boolean isChildUnder(View child, float x, float y) {
        final float left = getX(child);
        final float top = getY(child);
        final float right = left + child.getWidth();
        final float bottom = top + child.getHeight();
        return x >= left && y >= top && x < right && y < bottom;
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && isDescendantClipped(focused)) {
            smoothScrollTo(0, 0);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnTouchModeChangeListener(touchModeChangeListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnTouchModeChangeListener(touchModeChangeListener);
        abortAnimation();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & View.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        if (scroller.isFinished()) {
            smoothScrollTo(collapseOffset < collapsibleHeight / 2 ? 0 : collapsibleHeight, 0);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            performDrag(-dyUnconsumed);
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0) {
            consumed[1] = (int) -performDrag(-dy);
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (velocityY > minFlingVelocity && collapseOffset != 0) {
            smoothScrollTo(0, velocityY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (!consumed && Math.abs(velocityY) > minFlingVelocity) {
            smoothScrollTo(velocityY > 0 ? 0 : collapsibleHeight, velocityY);
            return true;
        }
        return false;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(ResolverDrawerLayout.class.getName());
    }

//    @Override
//    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
//        super.onInitializeAccessibilityNodeInfo(info);
//        info.setClassName(ResolverDrawerLayout.class.getName());
//        if (isEnabled()) {
//            if (collapseOffset != 0) {
//                info.addAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
//                info.setScrollable(true);
//            }
//        }
//    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        if (super.performAccessibilityAction(action, arguments)) {
            return true;
        }

        if (action == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD && collapseOffset != 0) {
            smoothScrollTo(0, 0);
            return true;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int sourceWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSize = sourceWidth;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // Single-use layout; just ignore the mode and use available space.
        // Clamp to maxWidth.
        if (maxWidth >= 0) {
            widthSize = Math.min(widthSize, maxWidth);
        }

        final int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        final int widthPadding = getPaddingLeft() + getPaddingRight();
        int heightUsed = getPaddingTop() + getPaddingBottom();

        // Measure always-show children first.
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.alwaysShow && child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthSpec, widthPadding, heightSpec, heightUsed);
                heightUsed += lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin;
            }
        }

        final int alwaysShowHeight = heightUsed;

        // And now the rest.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (!lp.alwaysShow && child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthSpec, widthPadding, heightSpec, heightUsed);
                heightUsed += lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin;
            }
        }

        collapsibleHeight = Math.max(0,
                heightUsed - alwaysShowHeight - getMaxCollapsedHeight());
        uncollapsibleHeight = heightUsed - collapsibleHeight;

        boolean isLaidOut = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isLaidOut = isLaidOut();
        }
        collapseOffset = getCollapsedOffset(isLaidOut);
        topOffset = Math.max(0, heightSize - heightUsed) + (int) collapseOffset;

        setMeasuredDimension(sourceWidth, heightSize);
    }

    private float getCollapsedOffset(boolean isLaidOut) {
        if (isLaidOut) {
            return Math.min(collapseOffset, collapsibleHeight);
        } else {
            // Start out collapsed at first unless we restored state for otherwise
            return openOnLayout ? 0 : collapsibleHeight;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getWidth();

        int ypos = topOffset;
        int leftEdge = getPaddingLeft();
        int rightEdge = width - getPaddingRight();

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (child.getVisibility() == GONE) {
                continue;
            }

            int top = ypos + lp.topMargin;
            if (lp.ignoreOffset) {
                top -= collapseOffset;
            }
            final int bottom = top + child.getMeasuredHeight();

            final int childWidth = child.getMeasuredWidth();
            final int widthAvailable = rightEdge - leftEdge;
            final int left = leftEdge + (widthAvailable - childWidth) / 2;
            final int right = left + childWidth;

            child.layout(left, top, right, bottom);

            ypos = bottom + lp.bottomMargin;
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.open = collapsibleHeight > 0 && collapseOffset == 0;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        openOnLayout = ss.open;
    }

    public static class LayoutParams extends MarginLayoutParams {
        public boolean alwaysShow;
        public boolean ignoreOffset;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.ResolverDrawerLayout_LayoutParams);
            alwaysShow = a.getBoolean(
                    R.styleable.ResolverDrawerLayout_LayoutParams_layout_alwaysShow,
                    false);
            ignoreOffset = a.getBoolean(
                    R.styleable.ResolverDrawerLayout_LayoutParams_layout_ignoreOffset,
                    false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.alwaysShow = source.alwaysShow;
            this.ignoreOffset = source.ignoreOffset;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    static class SavedState extends BaseSavedState {
        boolean open;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            open = in.readInt() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(open ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public interface OnDismissedListener {
        public void onDismissed();
    }

    private class RunOnDismissedListener implements Runnable {
        @Override
        public void run() {
            dispatchOnDismissed();
        }
    }
}