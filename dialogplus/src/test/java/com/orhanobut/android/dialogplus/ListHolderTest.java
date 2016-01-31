package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.orhanobut.dialogplus.BuildConfig;
import com.orhanobut.dialogplus.HolderAdapter;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnHolderListener;
import com.orhanobut.dialogplus.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ListHolderTest {

  Context context;

  @Before public void setup() {
    context = Robolectric.setupActivity(Activity.class);
  }

  private ListHolder getListHolder() {
    ListHolder holder = new ListHolder();
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    holder.getView(layoutInflater, new LinearLayout(context));
    return holder;
  }

  @Test public void init() {
    assertThat(getListHolder()).isInstanceOf(HolderAdapter.class);
    assertThat(getListHolder()).isNotNull();
  }

  @Test public void testViewInflation() {
    ListHolder holder = new ListHolder();
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = holder.getView(layoutInflater, new LinearLayout(context));

    assertThat(view).isNotNull();
    assertThat(holder.getInflatedView().getId()).isEqualTo(R.id.dialogplus_list);

    ListView listView = (ListView) holder.getInflatedView();
    assertThat(listView.getOnItemClickListener()).isInstanceOf(ListHolder.class);
  }

  @Test public void testFooter() {
    ListHolder holder = getListHolder();

    assertThat(holder.getFooter()).isNull();

    View footer = new LinearLayout(context);
    holder.addFooter(footer);

    assertThat(holder.getFooter()).isEqualTo(footer);
  }

  @Test public void testHeader() {
    ListHolder holder = getListHolder();

    assertThat(holder.getHeader()).isNull();

    View header = new LinearLayout(context);
    holder.addHeader(header);

    assertThat(holder.getHeader()).isEqualTo(header);
  }

  @Test public void testOnItemClickWithoutItemListenerAndAdapter() {
    ListHolder holder = getListHolder();
    ListView listView = (ListView) holder.getInflatedView();

    try {
      listView.performItemClick(null, 0, 0);
    } catch (Exception e) {
      fail("it should not crash");
    }
  }


  @Test public void testOnItemClickWithoutItemListenerOnly() {
    ListHolder holder = getListHolder();
    ListView listView = (ListView) holder.getInflatedView();

    //with adapter set
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1,
        new String[]{"test"}
    );
    holder.setAdapter(adapter);
    try {
      listView.performItemClick(null, 0, 0);
    } catch (Exception e) {
      fail("it should not crash");
    }
  }

  @Test public void testOnItemClick() {
    ListHolder holder = getListHolder();
    ListView listView = (ListView) holder.getInflatedView();

    //with adapter set
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1,
        new String[]{"test"}
    );
    holder.setAdapter(adapter);

    //set listener
    holder.setOnItemClickListener(new OnHolderListener() {
      @Override
      public void onItemClick(Object item, View view, int position) {
        assertThat(String.valueOf(item)).isEqualTo("test");
        assertThat(position).isEqualTo(0);
        assertThat(view).isNull();
      }
    });
    listView.performItemClick(null, 0, 0);
  }

  @Test public void doNotCountHeaderForPositionCalculation() {
    ListHolder holder = getListHolder();
    holder.addHeader(new View(context));
    ListView listView = (ListView) holder.getInflatedView();

    //with adapter set
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1,
        new String[]{"test"}
    );
    holder.setAdapter(adapter);

    //set listener
    holder.setOnItemClickListener(new OnHolderListener() {
      @Override
      public void onItemClick(Object item, View view, int position) {
        assertThat(String.valueOf(item)).isEqualTo("test");
        assertThat(position).isEqualTo(0);
        assertThat(view).isNull();
      }
    });
    listView.performItemClick(null, 1, 0);
  }

}
