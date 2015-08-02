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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Orhan Obut
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18)
public class ViewHolderTest extends TestCase {

  private final Context context;

  public ViewHolderTest() {
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
    assertThat(getHolder()).isInstanceOf(Holder.class);
    assertThat(getHolder()).isNotNull();
  }

  @Test
  public void testViewInflation() {
    View contentView = new LinearLayout(context);
    ViewHolder holder = new ViewHolder(contentView);
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = holder.getView(layoutInflater, new LinearLayout(context));

    assertThat(view).isNotNull();
    assertThat(holder.getInflatedView()).isEqualTo(contentView);
  }

  @Test
  public void testFooter() {
    ViewHolder holder = getHolder();

    assertThat(holder.getFooter()).isNull();

    View footer = new LinearLayout(context);
    holder.addFooter(footer);

    assertThat(holder.getFooter()).isEqualTo(footer);
  }

  @Test
  public void testHeader() {
    ViewHolder holder = getHolder();

    assertThat(holder.getHeader()).isNull();

    View header = new LinearLayout(context);
    holder.addHeader(header);

    assertThat(holder.getHeader()).isEqualTo(header);
  }

}
