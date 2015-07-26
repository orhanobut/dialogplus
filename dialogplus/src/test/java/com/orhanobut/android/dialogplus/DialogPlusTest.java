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
  }

  @Test
  public void testIsShowing() {
    DialogPlus dialog = DialogPlus.newDialog(context).create();
    assertFalse(dialog.isShowing());
    dialog.show();
    assertTrue(dialog.isShowing());
    dialog.dismiss();
    assertFalse(dialog.isShowing());
  }

  @Test
  public void testDismiss() {
    DialogPlus dialog = DialogPlus.newDialog(context).create();
    dialog.dismiss();
    assertFalse(dialog.isShowing());
    dialog.show();
    assertTrue(dialog.isShowing());
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

    assertNotNull(dialog.findViewById(android.R.id.text1));
  }

  @Test
  public void testGetHeaderView() {
    LinearLayout layout = new LinearLayout(context);
    LinearLayout header = new LinearLayout(context);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .setHeader(header)
        .create();

    assertEquals(header, dialog.getHeaderView());
  }

  @Test
  public void testGetFooterView() {
    LinearLayout layout = new LinearLayout(context);
    LinearLayout footer = new LinearLayout(context);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .setFooter(footer)
        .create();

    assertEquals(footer, dialog.getFooterView());
  }

  @Test
  public void testGetHolderView() {
    LinearLayout layout = new LinearLayout(context);

    DialogPlus dialog = DialogPlus.newDialog(context)
        .setContentHolder(new ViewHolder(layout))
        .create();

    assertEquals(layout, dialog.getHolderView());
  }
}
