package com.orhanobut.dialogplus;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * @author Orhan Obut
 */
public class ExpandTouchListener implements View.OnTouchListener {

  private final AbsListView absListView;
  private final View contentContainer;
  private final int displayHeight;
  private final int defaultContentHeight;
  private final GestureDetector gestureDetector;
  private final int gravity;

  private float y;
  private boolean fullScreen;
  private boolean touchUp;
  private boolean scrollUp;
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

    this.params = (FrameLayout.LayoutParams) contentContainer.getLayoutParams();

    gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        return true;
      }

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        scrollUp = distanceY > 0;
        return false;
      }
    });
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    if (gestureDetector.onTouchEvent(event)) {
      return false;
    }

    if (!(!scrollUp && Utils.listIsAtTop(absListView)) && fullScreen) {
      return false;
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        y = event.getRawY();
        return true;
      case MotionEvent.ACTION_MOVE:
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
    }
    return true;
  }

  private void onTouchMove(View view, MotionEvent event) {
    if (y == -1) {
      y = event.getRawY();
    }
    float delta = (y - event.getRawY());
    touchUp = delta > 0;
    if (gravity == Gravity.TOP) {
      delta = -delta;
    }
    y = event.getRawY();

    int newHeight = params.height + (int) delta;
    if (newHeight > displayHeight) {
      newHeight = displayHeight;
    }
    if (newHeight < defaultContentHeight) {
      newHeight = defaultContentHeight;
    }
    params.height = newHeight;
    contentContainer.setLayoutParams(params);
    fullScreen = params.height == displayHeight;
  }

  private void onTouchUp(View view, MotionEvent event) {
    y = -1;
    if (!touchUp && params.height < displayHeight && params.height > (displayHeight * 4) / 5) {
      Utils.animateContent(contentContainer, displayHeight, new SimpleAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
          fullScreen = true;
        }
      });
      return;
    }
    if (touchUp && params.height > defaultContentHeight + 50) {
      Utils.animateContent(contentContainer, displayHeight, new SimpleAnimationListener() {
        @Override
        public void onAnimationEnd(Animation animation) {
          fullScreen = true;
        }
      });
      return;
    }
    if (touchUp && params.height <= defaultContentHeight + 50) {
      Utils.animateContent(contentContainer, defaultContentHeight, new SimpleAnimationListener());
      return;
    }
    if (!touchUp && params.height > defaultContentHeight) {
      Utils.animateContent(contentContainer, defaultContentHeight, new SimpleAnimationListener());
    }
  }
}
