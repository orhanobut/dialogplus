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

import static org.assertj.core.api.Assertions.assertThat;

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
      assertThat(e).hasMessage("Context may not be null");
    }

    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    assertThat(builder).isNotNull();
  }

  @Test
  public void getContext_shouldNotReturnNull() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    assertThat(builder.getContext()).isNotNull();
  }

  @Test
  public void setAdapter_shouldNotAcceptNull() {
    try {
      DialogPlus.newDialog(context).setAdapter(null);
      fail();
    } catch (Exception e) {
      assertThat(e).hasMessage("Adapter may not be null");
    }
  }

  @Test
  public void testAdapter() {
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1, new String[]{"234"}
    );
    DialogPlusBuilder builder = DialogPlus.newDialog(context).setAdapter(adapter);

    assertThat(builder.getAdapter()).isEqualTo(adapter);
  }

  @Test
  public void testFooter() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setFooter(android.R.layout.simple_list_item_1);

    assertThat(builder.getFooterView()).isNotNull();

    LinearLayout footerView = new LinearLayout(context);
    builder.setFooter(footerView);

    assertThat(builder.getFooterView()).isEqualTo(footerView);
  }

  @Test
  public void testHeader() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setHeader(android.R.layout.simple_list_item_1);
    assertThat(builder.getHeaderView()).isNotNull();

    LinearLayout headerView = new LinearLayout(context);
    builder.setHeader(headerView);
    assertThat(builder.getHeaderView()).isEqualTo(headerView);
  }

  @Test
  public void getHolder_shouldBeListHolderAsDefault() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getHolder()).isNotNull();
    assertThat(builder.getHolder()).isInstanceOf(ListHolder.class);
  }

  @Test
  public void testSetContentHolder() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //Test ListHolder
    ListHolder listHolder = new ListHolder();
    builder.setContentHolder(listHolder);
    assertThat(builder.getHolder()).isEqualTo(listHolder);

    //test GridHolder
    GridHolder gridHolder = new GridHolder(3);
    builder.setContentHolder(gridHolder);
    assertThat(builder.getHolder()).isEqualTo(gridHolder);

    //test ViewHolder
    ViewHolder viewHolder = new ViewHolder(new LinearLayout(context));
    builder.setContentHolder(viewHolder);
    assertThat(builder.getHolder()).isEqualTo(viewHolder);

    //should accept null
    builder.setContentHolder(null);
  }

  @Test
  public void testSetCancelable() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //default should be true
    assertThat(builder.isCancelable()).isTrue();

    builder.setCancelable(false);
    assertThat(builder.isCancelable()).isFalse();
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
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM);

    // set different gravity
    builder.setGravity(Gravity.TOP);
    assertThat(params.gravity).isEqualTo(Gravity.TOP);

    //set combination
    builder.setGravity(Gravity.TOP | Gravity.CENTER);
    assertThat(params.gravity).isEqualTo(Gravity.TOP | Gravity.CENTER);
  }

  @Test
  public void testInAnimation() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getInAnimation()).isNotNull();
    //TODO
  }

  @Test
  public void testOutAnimation() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getOutAnimation()).isNotNull();
    //TODO
  }

  @Test
  public void testContentLayoutParams_whenExpandedFalseAndNotSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getContentParams()).isInstanceOf(FrameLayout.LayoutParams.class);

    //when not expanded
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertThat(builder.isExpanded()).isFalse();
    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT);
    assertThat(params.height).isEqualTo(ViewGroup.LayoutParams.WRAP_CONTENT);
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM);
  }

  @Test
  public void testContentLayoutParams_whenExpandedTrueAndNotSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setExpanded(true);
    assertThat(builder.getContentParams()).isInstanceOf(FrameLayout.LayoutParams.class);

    //when not expanded
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertThat(builder.isExpanded()).isTrue();
    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT);
    assertThat(params.height).isEqualTo(builder.getDefaultContentHeight());
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM);
  }

  @Test
  public void testContentLayoutParams_whenExpandedTrueWithHeightAndNotSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setExpanded(true, 100);
    assertThat(builder.getContentParams()).isInstanceOf(FrameLayout.LayoutParams.class);

    //when not expanded
    FrameLayout.LayoutParams params = builder.getContentParams();
    assertThat(builder.isExpanded()).isTrue();
    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT);
    assertThat(params.height).isEqualTo(100);
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM);
  }

  @Test
  public void testExpanded() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //default should be false
    assertThat(builder.isExpanded()).isFalse();

    builder.setExpanded(true);
    assertThat(builder.isExpanded()).isTrue();
  }

  @Test
  public void testOutMostParams() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    FrameLayout.LayoutParams params = builder.getOutmostLayoutParams();

    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT);
    assertThat(params.height).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT);

    // default should be 0 for all
    assertThat(params.leftMargin).isEqualTo(0);
    assertThat(params.rightMargin).isEqualTo(0);
    assertThat(params.topMargin).isEqualTo(0);
    assertThat(params.bottomMargin).isEqualTo(0);

    //set new margin
    builder.setOutMostMargin(1, 2, 3, 4);
    params = builder.getOutmostLayoutParams();
    assertThat(params.leftMargin).isEqualTo(1);
    assertThat(params.topMargin).isEqualTo(2);
    assertThat(params.rightMargin).isEqualTo(3);
    assertThat(params.bottomMargin).isEqualTo(4);
  }

  @Test
  public void testDefaultContentMarginWhenGravityCenter() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setGravity(Gravity.CENTER);

    int minimumMargin = context.getResources().getDimensionPixelSize(R.dimen.default_center_margin);
    int[] margin = builder.getContentMargin();
    assertThat(margin[0]).isEqualTo(minimumMargin);
    assertThat(margin[1]).isEqualTo(minimumMargin);
    assertThat(margin[2]).isEqualTo(minimumMargin);
    assertThat(margin[3]).isEqualTo(minimumMargin);
  }

  @Test
  public void testDefaultContentMarginWhenGravityIsNotCenter() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    int[] margin = builder.getContentMargin();
    assertThat(margin[0]).isEqualTo(0);
    assertThat(margin[1]).isEqualTo(0);
    assertThat(margin[2]).isEqualTo(0);
    assertThat(margin[3]).isEqualTo(0);
  }

  @Test
  public void testContentMarginWhenSet() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setMargin(1, 2, 3, 4);
    int[] margin = builder.getContentMargin();
    assertThat(margin[0]).isEqualTo(1);
    assertThat(margin[1]).isEqualTo(2);
    assertThat(margin[2]).isEqualTo(3);
    assertThat(margin[3]).isEqualTo(4);
  }

  @Test
  public void testPadding() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);

    //default 0
    int[] padding = builder.getContentPadding();
    assertThat(padding[0]).isEqualTo(0);
    assertThat(padding[1]).isEqualTo(0);
    assertThat(padding[2]).isEqualTo(0);
    assertThat(padding[3]).isEqualTo(0);


    builder.setPadding(1, 2, 3, 4);
    padding = builder.getContentPadding();
    assertThat(padding[0]).isEqualTo(1);
    assertThat(padding[1]).isEqualTo(2);
    assertThat(padding[2]).isEqualTo(3);
    assertThat(padding[3]).isEqualTo(4);
  }

  @Test
  public void testSetContentWidth() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setContentWidth(100);

    FrameLayout.LayoutParams params = builder.getContentParams();
    assertThat(params.width).isEqualTo(100);
  }

  @Test
  public void testSetContentHeight() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    builder.setContentHeight(100);

    FrameLayout.LayoutParams params = builder.getContentParams();
    assertThat(params.height).isEqualTo(100);
  }

  @Test
  public void testSetOnClickListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getOnClickListener()).isNull();

    OnClickListener clickListener = new OnClickListener() {
      @Override
      public void onClick(DialogPlus dialog, View view) {

      }
    };
    builder.setOnClickListener(clickListener);
    assertThat(builder.getOnClickListener()).isNotNull();
    assertThat(builder.getOnClickListener()).isEqualTo(clickListener);
  }

  @Test
  public void testSetOnItemClickListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getOnItemClickListener()).isNull();

    OnItemClickListener listener = new OnItemClickListener() {
      @Override
      public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

      }
    };
    builder.setOnItemClickListener(listener);
    assertThat(builder.getOnItemClickListener()).isNotNull();
    assertThat(builder.getOnItemClickListener()).isEqualTo(listener);
  }

  @Test
  public void testSetOnDismissListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getOnDismissListener()).isNull();

    OnDismissListener listener = new OnDismissListener() {
      @Override
      public void onDismiss(DialogPlus dialog) {

      }
    };
    builder.setOnDismissListener(listener);
    assertThat(builder.getOnDismissListener()).isNotNull();
    assertThat(builder.getOnDismissListener()).isEqualTo(listener);
  }

  @Test
  public void testSetOnCancelListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getOnCancelListener()).isNull();

    OnCancelListener listener = new OnCancelListener() {
      @Override
      public void onCancel(DialogPlus dialog) {

      }
    };
    builder.setOnCancelListener(listener);
    assertThat(builder.getOnCancelListener()).isNotNull();
    assertThat(builder.getOnCancelListener()).isEqualTo(listener);
  }

  @Test
  public void testSetOnBackPressListener() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.getOnBackPressListener()).isNull();

    OnBackPressListener listener = new OnBackPressListener() {
      @Override
      public void onBackPressed(DialogPlus dialogPlus) {

      }
    };
    builder.setOnBackPressListener(listener);
    assertThat(builder.getOnBackPressListener()).isNotNull();
    assertThat(builder.getOnBackPressListener()).isEqualTo(listener);
  }

  @Test
  public void create_shouldNotReturnNull() {
    DialogPlusBuilder builder = DialogPlus.newDialog(context);
    assertThat(builder.create()).isNotNull();
  }

}
