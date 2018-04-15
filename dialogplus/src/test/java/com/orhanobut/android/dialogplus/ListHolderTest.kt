package com.orhanobut.android.dialogplus

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import com.orhanobut.dialogplus.HolderAdapter
import com.orhanobut.dialogplus.ListHolder
import com.orhanobut.dialogplus.R
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ListHolderTest {

  private val context = Robolectric.setupActivity(Activity::class.java)

  private val listHolder: ListHolder
    get() {
      val holder = ListHolder()
      val layoutInflater = LayoutInflater.from(context)
      holder.getView(layoutInflater, LinearLayout(context))
      return holder
    }

  @Test fun init() {
    assertThat(listHolder).isInstanceOf(HolderAdapter::class.java)
    assertThat(listHolder).isNotNull()
  }

  @Test fun testViewInflation() {
    val holder = ListHolder()
    val layoutInflater = LayoutInflater.from(context)
    val view = holder.getView(layoutInflater, LinearLayout(context))

    assertThat(view).isNotNull()
    assertThat(holder.inflatedView.id).isEqualTo(R.id.dialogplus_list)

    val listView = holder.inflatedView as ListView
    assertThat(listView.onItemClickListener).isInstanceOf(ListHolder::class.java)
  }

  @Test fun testFooter() {
    val holder = listHolder

    assertThat(holder.footer).isNull()

    val footer = LinearLayout(context)
    holder.addFooter(footer)

    assertThat(holder.footer).isEqualTo(footer)
  }

  @Test fun testHeader() {
    val holder = listHolder

    assertThat(holder.header).isNull()

    val header = LinearLayout(context)
    holder.addHeader(header)

    assertThat(holder.header).isEqualTo(header)
  }

  @Test fun testOnItemClickWithoutItemListenerAndAdapter() {
    val holder = listHolder
    val listView = holder.inflatedView as ListView

    try {
      listView.performItemClick(null, 0, 0)
    } catch (e: Exception) {
      fail("it should not crash")
    }

  }


  @Test fun testOnItemClickWithoutItemListenerOnly() {
    val holder = listHolder
    val listView = holder.inflatedView as ListView

    //with adapter set
    val adapter = ArrayAdapter(
        context, android.R.layout.simple_list_item_1,
        arrayOf("test")
    )
    holder.setAdapter(adapter)
    try {
      listView.performItemClick(null, 0, 0)
    } catch (e: Exception) {
      fail("it should not crash")
    }

  }

  @Test fun testOnItemClick() {
    val holder = listHolder
    val listView = holder.inflatedView as ListView

    //with adapter set
    val adapter = ArrayAdapter(
        context, android.R.layout.simple_list_item_1,
        arrayOf("test")
    )
    holder.setAdapter(adapter)

    //set listener
    holder.setOnItemClickListener { item, view, position ->
      assertThat(item.toString()).isEqualTo("test")
      assertThat(position).isEqualTo(0)
      assertThat(view).isEqualTo(listView)
    }
    listView.performItemClick(listView, 0, 0)
  }

  @Test fun doNotCountHeaderForPositionCalculation() {
    val holder = listHolder
    holder.addHeader(View(context))
    val listView = holder.inflatedView as ListView

    //with adapter set
    val adapter = ArrayAdapter(
        context, android.R.layout.simple_list_item_1,
        arrayOf("test")
    )
    holder.setAdapter(adapter)

    //set listener
    holder.setOnItemClickListener { item, view, position ->
      assertThat(item.toString()).isEqualTo("test")
      assertThat(position).isEqualTo(0)
      assertThat(view).isEqualTo(listView)
    }
    listView.performItemClick(listView, 1, 0)
  }

}
