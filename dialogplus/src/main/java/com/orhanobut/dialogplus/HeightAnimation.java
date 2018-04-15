package com.orhanobut.dialogplus;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class HeightAnimation extends Animation {

  private final int originalHeight;
  private float perValue;
  private final View view;

  HeightAnimation(View view, int fromHeight, int toHeight) {
    this.view = view;
    this.originalHeight = fromHeight;
    this.perValue = (toHeight - fromHeight);
  }

  @Override protected void applyTransformation(float interpolatedTime, Transformation t) {
    view.getLayoutParams().height = (int) (originalHeight + perValue * interpolatedTime);
    view.requestLayout();
  }

  @Override public boolean willChangeBounds() {
    return true;
  }
}