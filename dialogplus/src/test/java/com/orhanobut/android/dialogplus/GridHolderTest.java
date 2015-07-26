package com.orhanobut.android.dialogplus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.orhanobut.dialogplus.BuildConfig;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.HolderAdapter;
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
public class GridHolderTest extends TestCase {

  private Context context;

  @Before
  public void setup() {
    context = Robolectric.buildActivity(Activity.class).create().get();
  }

  private GridHolder getHolder() {
    GridHolder holder = new GridHolder(3);
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    holder.getView(layoutInflater, new LinearLayout(context));
    return holder;
  }

  @Test
  public void init() {
    assertTrue(getHolder() instanceof HolderAdapter);
    assertNotNull(getHolder());
  }

  @Test
  public void testViewInflation() {
    GridHolder holder = new GridHolder(3);
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    View view = holder.getView(layoutInflater, new LinearLayout(context));

    assertNotNull(view);
    assertEquals(R.id.list, holder.getInflatedView().getId());

    GridView gridView = (GridView) holder.getInflatedView();
    assertTrue(gridView.getOnItemClickListener() instanceof GridHolder);
  }

  @Test
  public void testFooter() {
    GridHolder holder = getHolder();

    assertNull(holder.getFooter());

    View footer = new LinearLayout(context);
    holder.addFooter(footer);

    assertEquals(footer, holder.getFooter());
  }

  @Test
  public void testHeader() {
    GridHolder holder = getHolder();

    assertNull(holder.getHeader());

    View header = new LinearLayout(context);
    holder.addHeader(header);

    assertEquals(header, holder.getHeader());
  }

  @Test
  public void testOnItemClick() {
    GridHolder holder = getHolder();
    GridView view = (GridView) holder.getInflatedView();

    //there is no listener, it shouldn't crash
    view.performItemClick(null, 0, 0);

    //with adapter set
    ArrayAdapter<String> adapter = new ArrayAdapter<>(
        context, android.R.layout.simple_list_item_1,
        new String[]{"test"}
    );
    holder.setAdapter(adapter);
    view.performItemClick(null, 0, 0);

    //set listener
    holder.setOnItemClickListener(new OnHolderListener() {
      @Override
      public void onItemClick(Object item, View view, int position) {
        assertEquals("test", String.valueOf(item));
        assertEquals(0, position);
        assertEquals(null, view);
      }
    });
    view.performItemClick(null, 0, 0);
  }

}
