package com.orhanobut.android.dialogplus

import android.app.Activity
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.LinearLayout
import com.orhanobut.dialogplus.GridHolder
import com.orhanobut.dialogplus.HolderAdapter
import com.orhanobut.dialogplus.R
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GridHolderTest {

  private val context = Robolectric.setupActivity(Activity::class.java)
  private val holder: GridHolder
    get() {
      val holder = GridHolder(3)
      val layoutInflater = LayoutInflater.from(context)
      holder.getView(layoutInflater, LinearLayout(context))
      return holder
    }

  @Test fun init() {
    assertThat(holder).isInstanceOf(HolderAdapter::class.java)
    assertThat(holder).isNotNull()
  }

  @Test fun testViewInflation() {
    val holder = GridHolder(3)
    val layoutInflater = LayoutInflater.from(context)
    val view = holder.getView(layoutInflater, LinearLayout(context))

    assertThat(view).isNotNull()
    assertThat(holder.inflatedView.id).isEqualTo(R.id.dialogplus_list)

    val gridView = holder.inflatedView as GridView
    assertThat(gridView.onItemClickListener).isInstanceOf(GridHolder::class.java)
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

  @Test fun testOnItemClick() {
    val holder = holder
    val view = holder.inflatedView as GridView

    //there is no listener, it shouldn't crash
    view.performItemClick(null, 0, 0)

    //with adapter set
    val adapter = ArrayAdapter(
        context, android.R.layout.simple_list_item_1,
        arrayOf("test")
    )
    holder.setAdapter(adapter)
    view.performItemClick(null, 0, 0)

    //set listener
    holder.setOnItemClickListener { item, passedView, position ->
      assertThat(item.toString()).isEqualTo("test")
      assertThat(position).isEqualTo(0)
      assertThat(view).isEqualTo(passedView)
    }
    view.performItemClick(view, 0, 0)
  }

}
