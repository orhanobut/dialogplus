package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;

import com.orhanobut.dialogplus.BuildConfig;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Orhan Obut
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class UtilsTest {

  private Context context;

  @Before
  public void setup() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

}
