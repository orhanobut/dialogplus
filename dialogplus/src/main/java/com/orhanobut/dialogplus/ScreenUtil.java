package com.orhanobut.dialogplus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * @author yonghoon.do
 */
class ScreenUtil {
    static int getNavigationBarHeight(Context context, int orientation) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(
                orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape",
                "dimen", "android");
        if (id > 0) {
            return resources.getDimensionPixelSize(id);
        }
        return 0;
    }

    static int getStatusBarHeight(Context c) {
        int result = 0;
        int resourceId = c.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = c.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    static Point getDisplaySize(Context context) {
        if (Build.VERSION.SDK_INT >= 17) {
            return getDisplaySizeMinSdk17(context);
        } else if (Build.VERSION.SDK_INT >= 13) {
            return getDisplaySizeMinSdk13(context);
        } else {
            return getDisplaySizeMinSdk1(context);
        }
    }

    @TargetApi(17)
    static Point getDisplaySizeMinSdk17(Context context) {
        final WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);

        final Point size = new Point();
        size.x = metrics.widthPixels;
        size.y = metrics.heightPixels;

        return size;
    }

    @TargetApi(13)
    static Point getDisplaySizeMinSdk13(Context context) {
        final WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final Point size = new Point();
        display.getSize(size);

        return size;
    }

    @SuppressWarnings("deprecation")
    static Point getDisplaySizeMinSdk1(Context context) {
        final WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();

        final Point size = new Point();
        size.x = display.getWidth();
        size.y = display.getHeight();

        return size;
    }
}
