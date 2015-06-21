package com.orhanobut.dialogplus;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;

/**
 * @author Orhan Obut
 */
final class Utils {

    static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    static void animateContent(final View view, int to, Animation.AnimationListener listener) {
        HeightAnimation animation = new HeightAnimation(view, view.getHeight(), to);
        animation.setAnimationListener(listener);
        animation.setDuration(200);
        view.startAnimation(animation);
    }

    static boolean listIsAtTop(AbsListView listView) {
        return listView.getChildCount() == 0 || listView.getChildAt(0).getTop() == 0;
    }
}
