package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.orhanobut.dialogplus.BuildConfig;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ViewHolder;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * @author Orhan Obut
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ViewHolderTest extends TestCase {

  private Context context;

  @Before
  public void setup() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

  private ViewHolder getHolder() {
    ViewHolder holder = new ViewHolder(new LinearLayout(context));
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    holder.getView(layoutInflater, new LinearLayout(context));
    return holder;
  }

  @Test
  public void init() {
    assertTrue(getHolder() instanceof Holder);
    assertNotNull(getHolder());
  }

  @Test
  public void testViewInflation() {
    View contentView = new LinearLayout(context);
    ViewHolder holder = new ViewHolder(contentView);
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = holder.getView(layoutInflater, new LinearLayout(context));

    assertNotNull(view);
    assertEquals(contentView, holder.getInflatedView());
  }

  @Test
  public void testFooter() {
    ViewHolder holder = getHolder();

    assertNull(holder.getFooter());

    View footer = new LinearLayout(context);
    holder.addFooter(footer);

    assertEquals(footer, holder.getFooter());
  }

  @Test
  public void testHeader() {
    ViewHolder holder = getHolder();

    assertNull(holder.getHeader());

    View header = new LinearLayout(context);
    holder.addHeader(header);

    assertEquals(header, holder.getHeader());
  }

}
