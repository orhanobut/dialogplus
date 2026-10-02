package com.orhanobut.dialogplus

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

/** Runs real inflation, adapter, input and animation behavior on Android, without mocks. */
class DialogPlusDeviceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var activity: Activity

    @Before fun startActivity() {
        val intent = Intent().setClassName(instrumentation.targetContext, DialogTestActivity::class.java.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity = instrumentation.startActivitySync(intent)
        instrumentation.waitForIdleSync()
    }
    @After fun finishActivity() { onMain { activity.finish() } }
    private fun onMain(block: () -> Unit) {
        val task = FutureTask(block, Unit)
        instrumentation.runOnMainSync(task)
        task.get()
    }
    private fun adapter() = ArrayAdapter(activity, android.R.layout.simple_list_item_1, arrayOf("first", "second"))
    private fun inflate(holder: Holder): View = holder.getView(LayoutInflater.from(activity), FrameLayout(activity))

    @Test fun customContentMovesFromItsPreviousParentAndReceivesGlobalClicks() = onMain {
        val content = LinearLayout(activity)
        val child = TextView(activity).apply { id = android.R.id.text1 }
        content.addView(child)
        val originalParent = FrameLayout(activity).apply { addView(content) }
        var clicked: View? = null
        val dialog = DialogPlus.newDialog(activity).setContentHolder(ViewHolder(content))
            .setOnClickListener { _, view -> clicked = view }.create()
        assertEquals(0, originalParent.childCount)
        assertSame(content, dialog.holderView)
        assertSame(child, dialog.findViewById(android.R.id.text1))
        assertNull(dialog.findViewById(android.R.id.text2))
        child.performClick()
        assertSame(child, clicked)
    }
    @Test fun allHoldersInflateAndAttachHeadersAndFooters() = onMain {
        for (holder in listOf(ListHolder(), GridHolder(3), ViewHolder(android.R.layout.simple_list_item_1))) {
            inflate(holder)
            val header = View(activity)
            val footer = View(activity)
            holder.addHeader(header)
            holder.addFooter(footer)
            assertSame(header, holder.header)
            assertSame(footer, holder.footer)
            if (holder is ListHolder) {
                val list = holder.inflatedView as ListView
                assertEquals(1, list.headerViewsCount)
                assertEquals(1, list.footerViewsCount)
            } else {
                assertNotNull(header.parent)
                assertNotNull(footer.parent)
            }
            if (holder is GridHolder) {
                holder.setAdapter(adapter())
                val grid = holder.inflatedView as GridView
                grid.measure(View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY))
                grid.layout(0, 0, 300, 300)
                assertEquals(3, grid.numColumns)
            }
        }
    }
    @Test fun scrollingListHeaderOffsetsItemsAndAllowsNullHeaderItems() = onMain {
        val holder = ListHolder()
        inflate(holder)
        val header = View(activity)
        holder.addHeader(header)
        holder.setAdapter(adapter())
        val calls = mutableListOf<Pair<Any?, Int>>()
        holder.setOnItemClickListener { item, _, position -> calls += item to position }
        val list = holder.inflatedView as ListView
        list.performItemClick(header, 0, 0)
        list.performItemClick(list, 1, 0)
        assertEquals(listOf(null to -1, "first" to 0), calls)
    }
    @Test fun fixedListHeaderDoesNotOffsetAdapterPositions() = onMain {
        val holder = ListHolder()
        inflate(holder)
        val header = View(activity)
        val footer = View(activity)
        holder.addHeader(header, true)
        holder.addFooter(footer, true)
        holder.setAdapter(adapter())
        var received: Pair<Any?, Int>? = null
        holder.setOnItemClickListener { item, _, position -> received = item to position }
        val list = holder.inflatedView as ListView
        assertEquals(0, list.headerViewsCount)
        assertEquals(0, list.footerViewsCount)
        list.performItemClick(list, 0, 0)
        assertEquals("first" to 0, received)
    }
    @Test fun gridClicksPropagateTheAdapterItemViewAndPosition() = onMain {
        val holder = GridHolder(2)
        inflate(holder)
        holder.setAdapter(adapter())
        val grid = holder.inflatedView as GridView
        var calls = 0
        holder.setOnItemClickListener { item, view, position ->
            calls++
            assertEquals("second", item)
            assertSame(grid, view)
            assertEquals(1, position)
        }
        grid.performItemClick(grid, 1, 0)
        assertEquals(1, calls)
        holder.setOnItemClickListener(null)
        grid.performItemClick(null, 0, 0)
        assertEquals(1, calls)
    }
    @Test fun clicksWithoutAnAdapterOrListenerDoNotCrash() = onMain {
        for (holder in listOf(ListHolder(), GridHolder(2))) {
            inflate(holder)
            (holder.inflatedView as android.widget.AdapterView<*>).performItemClick(null, 0, 0)
        }
    }
    @Test fun builderDimensionsMarginsPaddingAndExpansionReachInflatedViews() = onMain {
        val content = TextView(activity)
        val builder = DialogPlus.newDialog(activity).setContentHolder(ViewHolder(content))
            .setGravity(Gravity.TOP).setContentWidth(240).setContentHeight(180)
            .setMargin(1, 2, 3, 4).setPadding(5, 6, 7, 8).setOutMostMargin(9, 10, 11, 12)
        val dialog = builder.create()
        dialog.show()
        val container = content.rootView.findViewById<ViewGroup>(R.id.dialogplus_content_container)
        val params = container.layoutParams as FrameLayout.LayoutParams
        assertEquals(240, params.width)
        assertEquals(180, params.height)
        assertEquals(Gravity.TOP, params.gravity)
        val inner = container.getChildAt(0).layoutParams as FrameLayout.LayoutParams
        assertEquals(listOf(1, 2, 3, 4), listOf(inner.leftMargin, inner.topMargin, inner.rightMargin, inner.bottomMargin))
        assertEquals(listOf(5, 6, 7, 8), listOf(content.paddingLeft, content.paddingTop, content.paddingRight, content.paddingBottom))
        val outer = container.parent as View
        val outParams = outer.layoutParams as FrameLayout.LayoutParams
        assertEquals(listOf(9, 10, 11, 12), listOf(outParams.leftMargin, outParams.topMargin, outParams.rightMargin, outParams.bottomMargin))
        assertEquals(125, builder.setExpanded(true, 125).contentParams.height)
    }
    @Test fun defaultBuilderAndCustomHeaderFooterResourcesRemainUsable() = onMain {
        val builder = DialogPlus.newDialog(activity)
        assertTrue(builder.holder is ListHolder)
        assertTrue(builder.isCancelable)
        assertFalse(builder.isExpanded)
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, builder.contentParams.width)
        assertEquals(ViewGroup.LayoutParams.WRAP_CONTENT, builder.contentParams.height)
        assertEquals(Gravity.BOTTOM, builder.contentParams.gravity)
        assertArrayEquals(intArrayOf(0, 0, 0, 0), builder.contentMargin)
        val center = DialogPlus.newDialog(activity).setGravity(Gravity.CENTER)
        val margin = activity.resources.getDimensionPixelSize(R.dimen.dialogplus_default_center_margin)
        assertArrayEquals(intArrayOf(margin, margin, margin, margin), center.contentMargin)
        val header = TextView(activity)
        val footer = TextView(activity)
        val dialog = builder.setHeader(android.R.layout.simple_list_item_1).setHeader(header)
            .setFooter(android.R.layout.simple_list_item_1).setFooter(footer).create()
        assertSame(header, dialog.headerView)
        assertSame(footer, dialog.footerView)
        assertNotNull(DialogPlus.newDialog(activity).setHeader(android.R.layout.simple_list_item_1).headerView)
        assertNotNull(DialogPlus.newDialog(activity).setFooter(android.R.layout.simple_list_item_1).footerView)
    }
    @Test fun showAndDismissAreIdempotentAndDismissalWaitsForAnimation() {
        lateinit var dialog: DialogPlus
        val dismissed = CountDownLatch(1)
        var calls = 0
        onMain {
            dialog = DialogPlus.newDialog(activity).setAdapter(adapter()).setOnDismissListener { calls++; dismissed.countDown() }.create()
            dialog.dismiss()
            assertEquals(0, calls)
            assertFalse(dialog.isShowing)
            dialog.show()
            dialog.show()
            assertTrue(dialog.isShowing)
            dialog.dismiss()
            dialog.dismiss()
            assertTrue(dialog.isShowing)
        }
        assertTrue("Dismiss animation did not finish", dismissed.await(5, TimeUnit.SECONDS))
        onMain {
            assertFalse(dialog.isShowing)
            assertEquals(1, calls)
            dialog.show()
            assertTrue(dialog.isShowing)
        }
    }
    @Test fun showingStateBelongsToEachDialogInstance() = onMain {
        val first = DialogPlus.newDialog(activity).create()
        val second = DialogPlus.newDialog(activity).create()
        first.show()
        assertTrue(first.isShowing)
        assertFalse(second.isShowing)
        second.show()
        assertTrue(second.isShowing)
    }
    @Test fun backPressNotifiesBeforeCancelAndDismiss() {
        val events = mutableListOf<String>()
        val dismissed = CountDownLatch(1)
        onMain {
            val dialog = DialogPlus.newDialog(activity).setAdapter(adapter())
                .setOnBackPressListener { events += "back" }
                .setOnCancelListener { events += "cancel" }
                .setOnDismissListener { events += "dismiss"; dismissed.countDown() }.create()
            dialog.show()
        }
        instrumentation.waitForIdleSync()
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        assertTrue("Dismiss callback missing", dismissed.await(5, TimeUnit.SECONDS))
        onMain { assertEquals(listOf("back", "cancel", "dismiss"), events) }
    }
    @Test fun nonCancelableBackPressOnlyNotifiesAndKeepsTheDialogAttached() {
        var backs = 0
        var cancellations = 0
        lateinit var dialog: DialogPlus
        onMain {
            dialog = DialogPlus.newDialog(activity).setCancelable(false)
                .setOnBackPressListener { backs++ }.setOnCancelListener { cancellations++ }.create()
            dialog.show()
        }
        instrumentation.waitForIdleSync()
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        onMain {
            assertEquals(1, backs)
            assertEquals(0, cancellations)
            assertTrue(dialog.isShowing)
        }
    }
    @Test fun overlayCancelsOnlyCancelableDialogs() = onMain {
        for (cancelable in listOf(true, false)) {
            var cancellations = 0
            val dialog = DialogPlus.newDialog(activity).setCancelable(cancelable)
                .setOnCancelListener { cancellations++ }.create()
            dialog.show()
            val overlay = activity.findViewById<View>(android.R.id.content) as ViewGroup
            val root = overlay.getChildAt(overlay.childCount - 1)
            val event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0f, 0f, 0)
            try { root.dispatchTouchEvent(event) } finally { event.recycle() }
            assertEquals(if (cancelable) 1 else 0, cancellations)
        }
    }
    @Test fun listTopDetectionChecksPositionAsWellAsTheChildOffset() {
        lateinit var list: ListView
        onMain {
            list = ListView(activity)
            list.adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, (0..100).map { "item $it" })
            activity.setContentView(list)
        }
        instrumentation.waitForIdleSync()
        onMain {
            assertTrue(Utils.listIsAtTop(list))
            list.setSelectionFromTop(50, 0)
        }
        instrumentation.waitForIdleSync()
        onMain {
            assertEquals(50, list.firstVisiblePosition)
            assertFalse(Utils.listIsAtTop(list))
        }
    }
}
