package com.orhanobut.android.dialogplus

import android.app.Activity
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.orhanobut.dialogplus.*
import junit.framework.Assert.fail
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DialogPlusBuilderTest {

  private val context = Robolectric.setupActivity(Activity::class.java)

  @Test fun testAdapter() {
    val adapter = ArrayAdapter(
        context, android.R.layout.simple_list_item_1, arrayOf("234")
    )
    val builder = DialogPlus.newDialog(context).setAdapter(adapter)

    assertThat(builder.adapter).isEqualTo(adapter)
  }

  @Test fun testFooter() {
    val builder = DialogPlus.newDialog(context)
    builder.setFooter(android.R.layout.simple_list_item_1)

    assertThat(builder.footerView).isNotNull()

    val footerView = LinearLayout(context)
    builder.setFooter(footerView)

    assertThat(builder.footerView).isEqualTo(footerView)
  }

  @Test fun testHeader() {
    val builder = DialogPlus.newDialog(context)
    builder.setHeader(android.R.layout.simple_list_item_1)
    assertThat(builder.headerView).isNotNull()

    val headerView = LinearLayout(context)
    builder.setHeader(headerView)
    assertThat(builder.headerView).isEqualTo(headerView)
  }

  @Test fun getHolder_shouldBeListHolderAsDefault() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.holder).isNotNull()
    assertThat(builder.holder).isInstanceOf(ListHolder::class.java)
  }

  @Test fun testSetContentHolder() {
    val builder = DialogPlus.newDialog(context)

    //Test ListHolder
    val listHolder = ListHolder()
    builder.setContentHolder(listHolder)
    assertThat(builder.holder).isEqualTo(listHolder)

    //test GridHolder
    val gridHolder = GridHolder(3)
    builder.setContentHolder(gridHolder)
    assertThat(builder.holder).isEqualTo(gridHolder)

    //test ViewHolder
    val viewHolder = ViewHolder(LinearLayout(context))
    builder.setContentHolder(viewHolder)
    assertThat(builder.holder).isEqualTo(viewHolder)
  }

  @Test fun testSetCancelable() {
    val builder = DialogPlus.newDialog(context)

    //default should be true
    assertThat(builder.isCancelable).isTrue()

    builder.isCancelable = false
    assertThat(builder.isCancelable).isFalse()
  }

  @Test fun testBackgroundColorResId() {
    //TODO fill it
  }

  @Test fun testGravity() {
    val builder = DialogPlus.newDialog(context)
    //default should be bottom
    val params = builder.contentParams
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM)

    // set different gravity
    builder.setGravity(Gravity.TOP)
    assertThat(params.gravity).isEqualTo(Gravity.TOP)

    //set combination
    builder.setGravity(Gravity.TOP or Gravity.CENTER)
    assertThat(params.gravity).isEqualTo(Gravity.TOP or Gravity.CENTER)
  }

  @Test fun testInAnimation() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.inAnimation).isNotNull()
    //TODO
  }

  @Test fun testOutAnimation() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.outAnimation).isNotNull()
    //TODO
  }

  @Test fun testContentLayoutParams_whenExpandedFalseAndNotSet() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.contentParams).isInstanceOf(FrameLayout.LayoutParams::class.java)

    //when not expanded
    val params = builder.contentParams
    assertThat(builder.isExpanded).isFalse()
    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
    assertThat(params.height).isEqualTo(ViewGroup.LayoutParams.WRAP_CONTENT)
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM)
  }

  @Test fun testContentLayoutParams_whenExpandedTrueAndNotSet() {
    val builder = DialogPlus.newDialog(context)
    builder.isExpanded = true
    assertThat(builder.contentParams).isInstanceOf(FrameLayout.LayoutParams::class.java)

    //when not expanded
    val params = builder.contentParams
    assertThat(builder.isExpanded).isTrue()
    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
    assertThat(params.height).isEqualTo(builder.defaultContentHeight)
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM)
  }

  @Test fun testContentLayoutParams_whenExpandedTrueWithHeightAndNotSet() {
    val builder = DialogPlus.newDialog(context)
    builder.setExpanded(true, 100)
    assertThat(builder.contentParams).isInstanceOf(FrameLayout.LayoutParams::class.java)

    //when not expanded
    val params = builder.contentParams
    assertThat(builder.isExpanded).isTrue()
    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
    assertThat(params.height).isEqualTo(100)
    assertThat(params.gravity).isEqualTo(Gravity.BOTTOM)
  }

  @Test fun testExpanded() {
    val builder = DialogPlus.newDialog(context)

    //default should be false
    assertThat(builder.isExpanded).isFalse()

    builder.isExpanded = true
    assertThat(builder.isExpanded).isTrue()
  }

  @Test fun testOutMostParams() {
    val builder = DialogPlus.newDialog(context)
    var params = builder.outmostLayoutParams

    assertThat(params.width).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)
    assertThat(params.height).isEqualTo(ViewGroup.LayoutParams.MATCH_PARENT)

    // default should be 0 for all
    assertThat(params.leftMargin).isEqualTo(0)
    assertThat(params.rightMargin).isEqualTo(0)
    assertThat(params.topMargin).isEqualTo(0)
    assertThat(params.bottomMargin).isEqualTo(0)

    //set new margin
    builder.setOutMostMargin(1, 2, 3, 4)
    params = builder.outmostLayoutParams
    assertThat(params.leftMargin).isEqualTo(1)
    assertThat(params.topMargin).isEqualTo(2)
    assertThat(params.rightMargin).isEqualTo(3)
    assertThat(params.bottomMargin).isEqualTo(4)
  }

  @Test fun testDefaultContentMarginWhenGravityCenter() {
    val builder = DialogPlus.newDialog(context)
    builder.setGravity(Gravity.CENTER)

    val minimumMargin = context.resources.getDimensionPixelSize(R.dimen.dialogplus_default_center_margin)
    val margin = builder.contentMargin
    assertThat(margin[0]).isEqualTo(minimumMargin)
    assertThat(margin[1]).isEqualTo(minimumMargin)
    assertThat(margin[2]).isEqualTo(minimumMargin)
    assertThat(margin[3]).isEqualTo(minimumMargin)
  }

  @Test fun testDefaultContentMarginWhenGravityIsNotCenter() {
    val builder = DialogPlus.newDialog(context)
    val margin = builder.contentMargin
    assertThat(margin[0]).isEqualTo(0)
    assertThat(margin[1]).isEqualTo(0)
    assertThat(margin[2]).isEqualTo(0)
    assertThat(margin[3]).isEqualTo(0)
  }

  @Test fun testContentMarginWhenSet() {
    val builder = DialogPlus.newDialog(context)
    builder.setMargin(1, 2, 3, 4)
    val margin = builder.contentMargin
    assertThat(margin[0]).isEqualTo(1)
    assertThat(margin[1]).isEqualTo(2)
    assertThat(margin[2]).isEqualTo(3)
    assertThat(margin[3]).isEqualTo(4)
  }

  @Test fun testPadding() {
    val builder = DialogPlus.newDialog(context)

    //default 0
    var padding = builder.contentPadding
    assertThat(padding[0]).isEqualTo(0)
    assertThat(padding[1]).isEqualTo(0)
    assertThat(padding[2]).isEqualTo(0)
    assertThat(padding[3]).isEqualTo(0)


    builder.setPadding(1, 2, 3, 4)
    padding = builder.contentPadding
    assertThat(padding[0]).isEqualTo(1)
    assertThat(padding[1]).isEqualTo(2)
    assertThat(padding[2]).isEqualTo(3)
    assertThat(padding[3]).isEqualTo(4)
  }

  @Test fun testSetContentWidth() {
    val builder = DialogPlus.newDialog(context)
    builder.setContentWidth(100)

    val params = builder.contentParams
    assertThat(params.width).isEqualTo(100)
  }

  @Test fun testSetContentHeight() {
    val builder = DialogPlus.newDialog(context)
    builder.setContentHeight(100)

    val params = builder.contentParams
    assertThat(params.height).isEqualTo(100)
  }

  @Test fun testSetOnClickListener() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.onClickListener).isNull()

    val clickListener = OnClickListener { dialog, view -> }
    builder.onClickListener = clickListener
    assertThat(builder.onClickListener).isNotNull()
    assertThat(builder.onClickListener).isEqualTo(clickListener)
  }

  @Test fun testSetOnItemClickListener() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.onItemClickListener).isNull()

    val listener = OnItemClickListener { dialog, item, view, position -> }
    builder.onItemClickListener = listener
    assertThat(builder.onItemClickListener).isNotNull()
    assertThat(builder.onItemClickListener).isEqualTo(listener)
  }

  @Test fun testSetOnDismissListener() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.onDismissListener).isNull()

    val listener = OnDismissListener { }
    builder.onDismissListener = listener
    assertThat(builder.onDismissListener).isNotNull()
    assertThat(builder.onDismissListener).isEqualTo(listener)
  }

  @Test fun testSetOnCancelListener() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.onCancelListener).isNull()

    val listener = OnCancelListener { }
    builder.setOnCancelListener(listener)
    assertThat(builder.onCancelListener).isNotNull()
    assertThat(builder.onCancelListener).isEqualTo(listener)
  }

  @Test fun testSetOnBackPressListener() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.onBackPressListener).isNull()

    val listener = OnBackPressListener { }
    builder.onBackPressListener = listener
    assertThat(builder.onBackPressListener).isNotNull()
    assertThat(builder.onBackPressListener).isEqualTo(listener)
  }

  @Test fun create_shouldNotReturnNull() {
    val builder = DialogPlus.newDialog(context)
    assertThat(builder.create()).isNotNull()
  }

}
