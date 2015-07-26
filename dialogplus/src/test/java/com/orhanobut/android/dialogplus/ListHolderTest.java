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

import junit.framework.TestCase;

import org.junit.Before;
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
public class ListHolderTest extends TestCase {

  private Context context;

  @Before
  public void setup() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

  private ListHolder getListHolder() {
    ListHolder holder = new ListHolder();
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    holder.getView(layoutInflater, new LinearLayout(context));
    return holder;
  }

  @Test
  public void init() {
    assertTrue(getListHolder() instanceof HolderAdapter);
    assertNotNull(getListHolder());
  }

  @Test
  public void testViewInflation() {
    ListHolder holder = new ListHolder();
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = holder.getView(layoutInflater, new LinearLayout(context));

    assertNotNull(view);
    assertEquals(R.id.list, holder.getInflatedView().getId());

    ListView listView = (ListView) holder.getInflatedView();
    assertTrue(listView.getOnItemClickListener() instanceof ListHolder);
  }

  @Test
  public void testFooter() {
    ListHolder holder = getListHolder();

    assertNull(holder.getFooter());

    View footer = new LinearLayout(context);
    holder.addFooter(footer);

    assertEquals(footer, holder.getFooter());
  }

  @Test
  public void testHeader() {
    ListHolder holder = getListHolder();

    assertNull(holder.getHeader());

    View header = new LinearLayout(context);
    holder.addHeader(header);

    assertEquals(header, holder.getHeader());
  }

  @Test
  public void testOnItemClick() {
    ListHolder holder = getListHolder();
    ListView listView = (ListView) holder.getInflatedView();

    //there is no listener, it shouldn't crash
    listView.performItemClick(null, 0, 0);

    //with adapter set
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1,
        new String[]{"test"}
    );
    holder.setAdapter(adapter);
    listView.performItemClick(null, 0, 0);

    //set listener
    holder.setOnItemClickListener(new OnHolderListener() {
      @Override
      public void onItemClick(Object item, View view, int position) {
        assertEquals("test", String.valueOf(item));
        assertEquals(0, position);
        assertEquals(null, view);
      }
    });
    listView.performItemClick(null, 0, 0);
  }

}
