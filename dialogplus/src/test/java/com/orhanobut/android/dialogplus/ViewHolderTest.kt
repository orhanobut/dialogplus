package com.orhanobut.android.dialogplus

import android.app.Activity
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.orhanobut.dialogplus.Holder
import com.orhanobut.dialogplus.ViewHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ViewHolderTest {

  private val context = Robolectric.setupActivity(Activity::class.java)

  private val holder: ViewHolder
    get() {
      val holder = ViewHolder(LinearLayout(context))
      val layoutInflater = LayoutInflater.from(context)
      holder.getView(layoutInflater, LinearLayout(context))
      return holder
    }

  @Test fun init() {
    assertThat(holder).isInstanceOf(Holder::class.java)
    assertThat(holder).isNotNull()
  }

  @Test fun testViewInflation() {
    val contentView = LinearLayout(context)
    val holder = ViewHolder(contentView)
    val layoutInflater = LayoutInflater.from(context)
    val view = holder.getView(layoutInflater, LinearLayout(context))

    assertThat(view).isNotNull()
    assertThat(holder.inflatedView).isEqualTo(contentView)
  }

  @Test fun testFooter() {
    val holder = holder

    assertThat(holder.footer).isNull()

    val footer = LinearLayout(context)
    holder.addFooter(footer)

    assertThat(holder.footer).isEqualTo(footer)
  }

  @Test fun testHeader() {
    val holder = holder

    assertThat(holder.header).isNull()

    val header = LinearLayout(context)
    holder.addHeader(header)

    assertThat(holder.header).isEqualTo(header)
  }

}
