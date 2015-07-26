package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.orhanobut.dialogplus.BuildConfig;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.DialogPlusBuilder;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.R;
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
public class DialogPlusBuilderTest extends TestCase {

  private final Context context;

  public DialogPlusBuilderTest() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

  @Test
  public void constructorShouldNotAcceptNull() {
    try {
      DialogPlus.newDialog(null);
      fail();
    } catch (Exception e) {
      if (e instanceof NullPointerException) {
        assertTrue(true);
      } else {
        fail();
      }
    }

    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNotNull(builder);
  }

  @Test
  public void getContext_shouldNotReturnNull() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNotNull(builder.getContext());
  }

  @Test
  public void setAdapter_shouldNotAcceptNull() {
    try {
      DialogPlus.newDialog(context).setAdapter(null);
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testAdapter() {
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1, new String[]{"234"}
    );
    DialogPlusBuilder builder = DialogPlus.newDialog(context).setAdapter(adapter);
    assertEquals(adapter, builder.getAdapter());
  }

  @Test
  public void testFooter() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setFooter(android.R.layout.simple_list_item_1);
    assertNotNull(builder.getFooterView());

    LinearLayout footerView = new LinearLayout(context);
    builder.setFooter(footerView);
    assertEquals(footerView, builder.getFooterView());
  }

  @Test
  public void testHeader() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setHeader(android.R.layout.simple_list_item_1);
    assertNotNull(builder.getHeaderView());

    LinearLayout headerView = new LinearLayout(context);
    builder.setHeader(headerView);
    assertEquals(headerView, builder.getHeaderView());
  }

  @Test
  public void getHolder_shouldBeListHolderAsDefault() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNotNull(builder.getHolder());
    assertTrue(builder.getHolder() instanceof ListHolder);
  }

  @Test
  public void testSetContentHolder() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //Test ListHolder
    ListHolder listHolder = new ListHolder();
    builder.setContentHolder(listHolder);
    assertEquals(listHolder, builder.getHolder());

    //test GridHolder
    GridHolder gridHolder = new GridHolder(3);
    builder.setContentHolder(gridHolder);
    assertEquals(gridHolder, builder.getHolder());

    //test ViewHolder
    ViewHolder viewHolder = new ViewHolder(new LinearLayout(context));
    builder.setContentHolder(viewHolder);
    assertEquals(viewHolder, builder.getHolder());

    //should accept null
    builder.setContentHolder(null);
  }

  @Test
  public void testSetCancelable() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //default should be true
    assertEquals(true, builder.isCancelable());

    builder.setCancelable(false);
    assertEquals(false, builder.isCancelable());
  }

  @Test
  public void testBackgroundColorResId() {
    assertTrue(true);
    //TODO fill it
  }

  @Test
  public void testGravity() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    //default should be bottom
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertEquals(Gravity.BOTTOM, params.gravity);

    // set different gravity
    builder.setGravity(Gravity.TOP);
    assertEquals(Gravity.TOP, params.gravity);

    //set combination
    builder.setGravity(Gravity.TOP | Gravity.CENTER);
    assertEquals(Gravity.TOP | Gravity.CENTER, params.gravity);
  }

  @Test
  public void testInAnimation() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNotNull(builder.getInAnimation());
    //TODO
  }

  @Test
  public void testOutAnimation() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNotNull(builder.getOutAnimation());
    //TODO
  }

  @Test
  public void testContentLayoutParams_whenExpandedFalseAndNotSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertTrue(builder.getContentParams() instanceof FrameLayout.LayoutParams);

    //when not expanded
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertEquals(false, builder.isExpanded());
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, params.width);
    assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, params.height);
    assertEquals(Gravity.BOTTOM, params.gravity);
  }

  @Test
  public void testContentLayoutParams_whenExpandedTrueAndNotSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setExpanded(true);
    assertTrue(builder.getContentParams() instanceof FrameLayout.LayoutParams);

    //when not expanded
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertEquals(true, builder.isExpanded());
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, params.width);
    assertEquals(builder.getDefaultContentHeight(), params.height);
    assertEquals(Gravity.BOTTOM, params.gravity);
  }

  @Test
  public void testContentLayoutParams_whenExpandedTrueWithHeightAndNotSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setExpanded(true, 100);
    assertTrue(builder.getContentParams() instanceof FrameLayout.LayoutParams);

    //when not expanded
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertEquals(true, builder.isExpanded());
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, params.width);
    assertEquals(100, params.height);
    assertEquals(Gravity.BOTTOM, params.gravity);
  }

  @Test
  public void testExpanded() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //default should be false
    assertFalse(builder.isExpanded());

    builder.setExpanded(true);
    assertTrue(builder.isExpanded());
  }

  @Test
  public void testOutMostParams() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    FrameLayout.LayoutParams params = builder.getOutmostLayoutParams();

    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, params.width);
    assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, params.height);

    // default should be 0 for all
    assertEquals(0, params.leftMargin);
    assertEquals(0, params.rightMargin);
    assertEquals(0, params.topMargin);
    assertEquals(0, params.bottomMargin);

    //set new margin
    builder.setOutMostMargin(1, 2, 3, 4);
    params = builder.getOutmostLayoutParams();
    assertEquals(1, params.leftMargin);
    assertEquals(2, params.topMargin);
    assertEquals(3, params.rightMargin);
    assertEquals(4, params.bottomMargin);
  }

  @Test
  public void testDefaultContentMarginWhenGravityCenter() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setGravity(Gravity.CENTER);

    int minimumMargin = context.getResources().getDimensionPixelSize(R.dimen.default_center_margin);
    int[] margin = builder.getContentMargin();
    assertEquals(minimumMargin, margin[0]);
    assertEquals(minimumMargin, margin[1]);
    assertEquals(minimumMargin, margin[2]);
    assertEquals(minimumMargin, margin[3]);
  }

  @Test
  public void testDefaultContentMarginWhenGravityIsNotCenter() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    int[] margin = builder.getContentMargin();
    assertEquals(0, margin[0]);
    assertEquals(0, margin[1]);
    assertEquals(0, margin[2]);
    assertEquals(0, margin[3]);
  }

  @Test
  public void testContentMarginWhenSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setMargin(1, 2, 3, 4);
    int[] margin = builder.getContentMargin();
    assertEquals(1, margin[0]);
    assertEquals(2, margin[1]);
    assertEquals(3, margin[2]);
    assertEquals(4, margin[3]);
  }

  @Test
  public void testPadding() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //default 0
    int[] padding = builder.getContentPadding();
    assertEquals(0, padding[0]);
    assertEquals(0, padding[1]);
    assertEquals(0, padding[2]);
    assertEquals(0, padding[3]);


    builder.setPadding(1, 2, 3, 4);
    padding = builder.getContentPadding();
    assertEquals(1, padding[0]);
    assertEquals(2, padding[1]);
    assertEquals(3, padding[2]);
    assertEquals(4, padding[3]);
  }

  @Test
  public void testSetContentWidth() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setContentWidth(100);

    FrameLayout.LayoutParams params = builder.getContentParams();
    assertEquals(100, params.width);
  }

  @Test
  public void testSetContentHeight() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setContentHeight(100);

    FrameLayout.LayoutParams params = builder.getContentParams();
    assertEquals(100, params.height);
  }

  @Test
  public void testSetOnClickListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNull(builder.getOnClickListener());

    OnClickListener clickListener = new OnClickListener() {
      @Override
      public void onClick(DialogPlus dialog, View view) {

      }
    };
    builder.setOnClickListener(clickListener);
    assertNotNull(builder.getOnClickListener());
    assertEquals(clickListener, builder.getOnClickListener());
  }

  @Test
  public void testSetOnItemClickListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNull(builder.getOnItemClickListener());

    OnItemClickListener listener = new OnItemClickListener() {
      @Override
      public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

      }
    };
    builder.setOnItemClickListener(listener);
    assertNotNull(builder.getOnItemClickListener());
    assertEquals(listener, builder.getOnItemClickListener());
  }

  @Test
  public void testSetOnDismissListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNull(builder.getOnDismissListener());

    OnDismissListener listener = new OnDismissListener() {
      @Override
      public void onDismiss(DialogPlus dialog) {

      }
    };
    builder.setOnDismissListener(listener);
    assertNotNull(builder.getOnDismissListener());
    assertEquals(listener, builder.getOnDismissListener());
  }

  @Test
  public void testSetOnCancelListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNull(builder.getOnCancelListener());

    OnCancelListener listener = new OnCancelListener() {
      @Override
      public void onCancel(DialogPlus dialog) {

      }
    };
    builder.setOnCancelListener(listener);
    assertNotNull(builder.getOnCancelListener());
    assertEquals(listener, builder.getOnCancelListener());
  }

  @Test
  public void testSetOnBackPressListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNull(builder.getOnBackPressListener());

    OnBackPressListener listener = new OnBackPressListener() {
      @Override
      public void onBackPressed(DialogPlus dialogPlus) {

      }
    };
    builder.setOnBackPressListener(listener);
    assertNotNull(builder.getOnBackPressListener());
    assertEquals(listener, builder.getOnBackPressListener());
  }

  @Test
  public void create_shouldNotReturnNull() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertNotNull(builder.create());
  }

}
