package com.orhanobut.android.dialogplus

import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DialogPlusTest {

  private val context = Robolectric.setupActivity(Activity::class.java)

  @Test fun testDialogCreate() {
    val dialog = DialogPlus.newDialog(context).create()
    dialog.show()
  }

  @Test fun testIsShowing() {
    val dialog = DialogPlus.newDialog(context).create()
    assertThat(dialog.isShowing).isFalse()
    dialog.show()
    assertThat(dialog.isShowing).isTrue()
    dialog.dismiss()
    assertThat(dialog.isShowing).isFalse()
  }

  @Test fun testDismiss() {
    val dialog = DialogPlus.newDialog(context).create()
    dialog.dismiss()
    assertThat(dialog.isShowing).isFalse()
    dialog.show()
    assertThat(dialog.isShowing).isTrue()
    dialog.dismiss()
    //TODO wait for dismiss
  }

  @Test fun testFindViewById() {
    val layout = LinearLayout(context)
    val textView = TextView(context)
    textView.id = android.R.id.text1
    layout.addView(textView)

    val dialog = DialogPlus.newDialog(context)
        .setContentHolder(ViewHolder(layout))
        .create()

    assertThat(dialog.findViewById(android.R.id.text1)).isNotNull()
  }

  @Test fun testGetHeaderView() {
    val layout = LinearLayout(context)
    val header = LinearLayout(context)

    val dialog = DialogPlus.newDialog(context)
        .setContentHolder(ViewHolder(layout))
        .setHeader(header)
        .create()

    assertThat(dialog.headerView).isEqualTo(header)
  }

  @Test fun testGetFooterView() {
    val layout = LinearLayout(context)
    val footer = LinearLayout(context)

    val dialog = DialogPlus.newDialog(context)
        .setContentHolder(ViewHolder(layout))
        .setFooter(footer)
        .create()

    assertThat(dialog.footerView).isEqualTo(footer)
  }

  @Test fun testGetHolderView() {
    val layout = LinearLayout(context)

    val dialog = DialogPlus.newDialog(context)
        .setContentHolder(ViewHolder(layout))
        .create()

    assertThat(dialog.holderView).isEqualTo(layout)
  }
}
