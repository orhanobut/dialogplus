package com.orhanobut.dialogplus;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;

class ExpandTouchListener implements View.OnTouchListener {

  private final AbsListView absListView;
  private final View contentContainer;

  /**
   * This is used to determine top. status bar height is removed
   */
  private final int displayHeight;

  /**
   * Default height for the holder
   */
  private final int defaultContentHeight;
  private final GestureDetector gestureDetector;
  private final int gravity;

  /**
   * The last touch position in Y Axis
   */
  private float y;

  /**
   * This is used to determine whether dialog reached to top or not.
   */
  private boolean fullScreen;

  /**
   * This is used to determine whether the user swipes from down to top.
   * touchUp is calculated by touch events
   */
  private boolean touchUp;

  /**
   * This is used to determine whether the user swipes from down to top.
   * scrollUp is calculated from gesture detector scroll event.
   * This shouldn't be used for the touch events
   */
  private boolean scrollUp;

  /**
   * Content container params, not the holder itself.
   */
  private FrameLayout.LayoutParams params;

  public static ExpandTouchListener newListener(Context context, AbsListView listView, View container,
                                                int gravity, int displayHeight, int defaultContentHeight) {
    return new ExpandTouchListener(context, listView, container, gravity, displayHeight, defaultContentHeight);
  }

  private ExpandTouchListener(Context context, AbsListView absListView, View container, int gravity,
                              int displayHeight, int defaultContentHeight) {
    this.absListView = absListView;
    this.contentContainer = container;
    this.gravity = gravity;
    this.displayHeight = displayHeight;
    this.defaultContentHeight = defaultContentHeight;
    this.params = (FrameLayout.LayoutParams) container.getLayoutParams();

    gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
      @Override public boolean onSingleTapUp(MotionEvent e) {
        return true;
      }

      @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrollUp = distanceY > 0;
        return false;
      }
    });
  }

  @Override public boolean onTouch(View v, MotionEvent event) {
    //If single tapped, don't consume the event
    if (gestureDetector.onTouchEvent(event)) {
      return false;
    }

    // when the dialog is fullscreen and user scrolls the content
    // don't consume the event
    if (!(!scrollUp && Utils.listIsAtTop(absListView)) && fullScreen) {
      return false;
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        y = event.getRawY();
        return true;
      case MotionEvent.ACTION_MOVE:
        // This is a quick fix to not trigger click event
        if (params.height == displayHeight) {
          params.height--;
          contentContainer.setLayoutParams(params);
          return false;
        }
        onTouchMove(v, event);
        break;
      case MotionEvent.ACTION_UP:
        onTouchUp(v, event);
        break;
      default:
        break;
    }
    return true;
  }

  private void onTouchMove(View view, MotionEvent event) {
    // sometimes Action_DOWN is not called, we need to make sure that
    // we calculate correct y value
    if (y == -1) {
      y = event.getRawY();
    }
    float delta = (y - event.getRawY());
    // if delta > 0 , that means user swipes to top
    touchUp = delta > 0;
    if (gravity == Gravity.TOP) {
      delta = -delta;
    }
    //update the y value, otherwise delta will be incorrect
    y = event.getRawY();

    int newHeight = params.height + (int) delta;

    // This prevents dialog to move out of screen bounds
    if (newHeight > displayHeight) {
      newHeight = displayHeight;
    }

    // This prevents the dialog go below the default value while dragging
    if (newHeight < defaultContentHeight) {
      newHeight = defaultContentHeight;
    }
    params.height = newHeight;
    contentContainer.setLayoutParams(params);

    // we use fullscreen value to activate view content scroll
    fullScreen = params.height == displayHeight;
  }

  private void onTouchUp(View view, MotionEvent event) {
    // reset y value
    y = -1;

    // if the dragging direction is from top to down and dialog position still can't exceeds threshold
    // move the dialog automatically to top
    if (!touchUp && params.height < displayHeight && params.height > (displayHeight * 4) / 5) {
      Utils.animateContent(contentContainer, displayHeight, new SimpleAnimationListener() {
        @Override public void onAnimationEnd(Animation animation) {
          fullScreen = true;
        }
      });
      return;
    }

    // if the dragging direction is down to top and dialog is dragged more than 50 to up
    // move the dialog automatically to top
    if (touchUp && params.height > defaultContentHeight + 50) {
      Utils.animateContent(contentContainer, displayHeight, new SimpleAnimationListener() {
        @Override public void onAnimationEnd(Animation animation) {
          fullScreen = true;
        }
      });
      return;
    }

    // if the dragging direction is from down to top and dialog position still can't exceeds threshold
    // move the dialog automatically to down
    if (touchUp && params.height <= defaultContentHeight + 50) {
      Utils.animateContent(contentContainer, defaultContentHeight, new SimpleAnimationListener());
      return;
    }

    // if the dragging direction is from top to down and the position exceeded the threshold
    // move the dialog to down
    if (!touchUp && params.height > defaultContentHeight) {
      Utils.animateContent(contentContainer, defaultContentHeight, new SimpleAnimationListener());
    }
  }
}
