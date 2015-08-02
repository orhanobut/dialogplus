package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.dialogplus.BuildConfig;
import com.orhanobut.dialogplus.DialogPlus;
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
public class DialogPlusTest extends TestCase {

  private final Context context;

  public DialogPlusTest() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

  @Test
  public void testDialogCreate() {
    DialogPlus dialog = DialogPlus.newDialog(context).create();
    dialog.show();
    assertTrue(true);
  }

  @Test
  public void testIsShowing() {
    DialogPlus dialog = DialogPlus.newDialog(context).create();
    assertThat(dialog.isShowing()).isFalse();
    dialog.show();
    assertThat(dialog.isShowing()).isTrue();
    dialog.dismiss();
    assertThat(dialog.isShowing()).isFalse();
  }

  @Test
  public void testDismiss() {
    DialogPlus dialog = DialogPlus.newDialog(context).create();
    dialog.dismiss();
    assertThat(dialog.isShowing()).isFalse();
    dialog.show();
    assertThat(dialog.isShowing()).isTrue();
    dialog.dismiss();
    //TODO wait for dismiss
  }

  @Test
  public void testFindViewById() {
    LinearLayout layout = new LinearLayout(context);
    TextView textView = new TextView(context);
    textView.setId(android.R.id.text1);
    layout.addView(textView);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .create();

    assertThat(dialog.findViewById(android.R.id.text1)).isNotNull();
  }

  @Test
  public void testGetHeaderView() {
    LinearLayout layout = new LinearLayout(context);
    LinearLayout header = new LinearLayout(context);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .setHeader(header)
        .create();

    assertThat(dialog.getHeaderView()).isEqualTo(header);
  }

  @Test
  public void testGetFooterView() {
    LinearLayout layout = new LinearLayout(context);
    LinearLayout footer = new LinearLayout(context);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .setFooter(footer)
        .create();

    assertThat(dialog.getFooterView()).isEqualTo(footer);
  }

  @Test
  public void testGetHolderView() {
    LinearLayout layout = new LinearLayout(context);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .create();

    assertThat(dialog.getHolderView()).isEqualTo(layout);
  }
}
