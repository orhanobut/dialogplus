package com.orhanobut.dialogplus

import android.app.Activity
import android.content.Intent
import android.os.SystemClock
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewConfiguration
import android.widget.AbsListView
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
import java.util.concurrent.FutureTask

/** Uses rendered platform widgets, real adapters, input injection and actual animations. */
class DialogPlusDeviceTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private lateinit var activity: Activity

    @Before fun startActivity() {
        val intent = Intent().setClassName(instrumentation.targetContext, DialogTestActivity::class.java.name)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity = instrumentation.startActivitySync(intent)
        instrumentation.waitForIdleSync()
    }

    @After fun finishActivity() {
        ui { activity.finish() }
    }

    private fun <T> ui(block: () -> T): T {
        val task = FutureTask(block)
        instrumentation.runOnMainSync(task)
        val result = task.get()
        instrumentation.waitForIdleSync()
        return result
    }

    private fun await(description: String, condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 5_000
        while (SystemClock.uptimeMillis() < deadline) {
            if (ui(condition)) return
            SystemClock.sleep(20)
        }
        fail("Timed out waiting for $description")
    }

    private fun adapter(items: List<String> = listOf("first", "second")) =
        ArrayAdapter(activity, android.R.layout.simple_list_item_1, items)

    private fun show(configure: DialogPlusBuilder.() -> Unit = {}): DialogPlus {
        val dialog = ui { DialogPlus.newDialog(activity).setAdapter(adapter()).apply(configure).create().also { it.show() } }
        await("dialog layout and entrance animation") {
            container(dialog).height > 0 && container(dialog).animation?.hasEnded() != false
        }
        return dialog
    }

    private fun container(dialog: DialogPlus) =
        dialog.holderView.rootView.findViewById<ViewGroup>(R.id.dialogplus_content_container)

    private fun dismiss(dialog: DialogPlus) {
        ui { dialog.dismiss() }
        await("dialog removal") { !dialog.isShowing }
    }

    private fun center(view: View): Pair<Float, Float> = ui {
        assertTrue("Input target must be visible and laid out", view.isShown && view.width > 0 && view.height > 0)
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        location[0] + view.width / 2f to location[1] + view.height / 2f
    }

    private fun pointer(downTime: Long, action: Int, x: Float, y: Float) {
        val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), action, x, y, 0)
        try { instrumentation.sendPointerSync(event) } finally { event.recycle() }
    }

    private fun tap(point: Pair<Float, Float>) {
        val downTime = SystemClock.uptimeMillis()
        pointer(downTime, MotionEvent.ACTION_DOWN, point.first, point.second)
        pointer(downTime, MotionEvent.ACTION_UP, point.first, point.second)
        // AbsListView posts its item click after the pressed-state feedback delay.
        SystemClock.sleep(ViewConfiguration.getPressedStateDuration().toLong())
        instrumentation.waitForIdleSync()
    }

    private fun drag(view: View, distance: Float) {
        val (x, y) = center(view)
        val downTime = SystemClock.uptimeMillis()
        pointer(downTime, MotionEvent.ACTION_DOWN, x, y)
        for (step in 1..10) {
            SystemClock.sleep(16)
            pointer(downTime, MotionEvent.ACTION_MOVE, x, y + distance * step / 10)
        }
        pointer(downTime, MotionEvent.ACTION_UP, x, y + distance)
        instrumentation.waitForIdleSync()
    }

    @Test fun customContentIsReparentedAndItsRenderedChildReceivesClicks() {
        lateinit var content: LinearLayout
        lateinit var child: TextView
        lateinit var originalParent: FrameLayout
        var clicked: View? = null
        var clickedDialog: DialogPlus? = null
        val dialog = show {
            content = LinearLayout(activity)
            child = TextView(activity).apply { id = android.R.id.text1; text = "Tap me" }
            content.addView(child)
            originalParent = FrameLayout(activity).apply { addView(content) }
            setContentHolder(ViewHolder(content))
            setOnClickListener { source, view -> clickedDialog = source; clicked = view }
        }
        tap(center(child))
        ui {
            assertEquals(0, originalParent.childCount)
            assertSame(content, dialog.holderView)
            assertSame(child, dialog.findViewById(android.R.id.text1))
            assertNull(dialog.findViewById(android.R.id.text2))
            assertSame(child, clicked)
            assertSame(dialog, clickedDialog)
            assertTrue(dialog.isShowing)
        }
    }

    @Test fun absentGlobalListenerPreservesExistingContentClicks() {
        var calls = 0
        var clearedCalls = 0
        val dialog = show {
            val content = TextView(activity).apply {
                id = android.R.id.text1
                text = "Existing click handler"
                setOnClickListener { calls++ }
            }
            setContentHolder(ViewHolder(content))
            setOnClickListener { _, _ -> clearedCalls++ }
            setOnClickListener(null)
        }
        tap(center(ui { dialog.holderView }))
        ui { assertEquals(1, calls); assertEquals(0, clearedCalls); assertTrue(dialog.isShowing) }
    }

    @Test fun scrollingHeaderAndFooterReportNullItemsAndAdapterPositions() {
        val calls = mutableListOf<Pair<Any?, Int>>()
        val clickedViews = mutableListOf<View?>()
        val dialog = show {
            setHeader(TextView(activity).apply { text = "Header" })
            setFooter(TextView(activity).apply { text = "Footer" })
            setOnItemClickListener { _, item, view, position -> calls += item to position; clickedViews += view }
        }
        val list = ui { dialog.holderView as ListView }
        val rows = ui { (0 until list.childCount).map { list.getChildAt(it) } }
        assertEquals(4, rows.size)
        for (row in rows) tap(center(row))
        ui {
            assertEquals(listOf(null to -1, "first" to 0, "second" to 1, null to 2), calls)
            assertEquals(rows, clickedViews)
            assertSame(dialog.headerView, rows.first())
            assertSame(dialog.footerView, rows.last())
        }
    }

    @Test fun fixedHeadersAndGridColumnsKeepRealItemCallbacksCorrect() {
        for (grid in listOf(false, true)) {
            val calls = mutableListOf<Pair<Any?, Int>>()
            var clicked: View? = null
            val dialog = show {
                if (grid) setContentHolder(GridHolder(2))
                setHeader(TextView(activity).apply { text = "Fixed header" }, true)
                setFooter(TextView(activity).apply { text = "Fixed footer" }, true)
                setOnItemClickListener { _, item, view, position -> calls += item to position; clicked = view }
            }
            val row = ui {
                val list = dialog.holderView as AbsListView
                if (grid) assertEquals(2, (list as GridView).numColumns)
                else {
                    assertEquals(0, (list as ListView).headerViewsCount)
                    assertEquals(0, list.footerViewsCount)
                }
                assertTrue(dialog.headerView!!.isShown)
                assertTrue(dialog.footerView!!.isShown)
                list.getChildAt(1)
            }
            tap(center(row))
            ui { assertEquals(listOf("second" to 1), calls); assertSame(row, clicked) }
            dismiss(dialog)
        }
    }

    @Test fun itemsRemainUsableWhenListenersAreCleared() {
        for (holder in ui { listOf(ListHolder(), GridHolder(2)) }) {
            var calls = 0
            val dialog = show {
                setContentHolder(holder)
                setOnItemClickListener { _, _, _, _ -> calls++ }
                setOnItemClickListener(null)
            }
            val row = ui { (dialog.holderView as AbsListView).getChildAt(0) }
            tap(center(row))
            ui { assertEquals(0, calls); assertTrue(dialog.isShowing) }
            // The holder's nullable listener must also be safe after it has been used.
            ui { holder.setOnItemClickListener { _, _, _ -> calls++ } }
            tap(center(row))
            ui { assertEquals(1, calls); holder.setOnItemClickListener(null) }
            tap(center(row))
            ui { assertEquals(1, calls); assertTrue(dialog.isShowing) }
            dismiss(dialog)
        }
    }

    @Test fun holderKeyListenersReceiveRealKeysAndCanBeCleared() {
        for (holder in ui { listOf(ListHolder(), GridHolder(2), ViewHolder(android.R.layout.simple_list_item_1)) }) {
            val keys = mutableListOf<Pair<Int, Int>>()
            val dialog = show { setContentHolder(holder) }
            ui {
                val target = if (holder is ViewHolder) dialog.findViewById(R.id.dialogplus_view_container)!! else dialog.holderView
                target.isFocusableInTouchMode = true
                assertTrue(target.requestFocus())
                holder.setOnKeyListener { _, code, event -> keys += code to event.action; true }
            }
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_SPACE)
            ui {
                assertEquals(listOf(KeyEvent.KEYCODE_SPACE to KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE to KeyEvent.ACTION_UP), keys)
                holder.setOnKeyListener(null)
            }
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_SPACE)
            ui { assertEquals(2, keys.size); assertTrue(dialog.isShowing) }
            dismiss(dialog)
        }
    }

    @Test fun configuredDimensionsMarginsAndPaddingReachTheDisplayedContent() {
        val dialog = show {
            setContentHolder(ViewHolder(android.R.layout.simple_list_item_1))
            setGravity(Gravity.TOP).setContentWidth(240).setContentHeight(180)
            setMargin(1, 2, 3, 4).setPadding(5, 6, 7, 8).setOutMostMargin(9, 10, 11, 12)
        }
        ui {
            val container = container(dialog)
            assertEquals(240, container.width)
            assertEquals(180, container.height)
            assertEquals(Gravity.TOP, (container.layoutParams as FrameLayout.LayoutParams).gravity)
            val inner = container.getChildAt(0).layoutParams as FrameLayout.LayoutParams
            assertEquals(listOf(1, 2, 3, 4), listOf(inner.leftMargin, inner.topMargin, inner.rightMargin, inner.bottomMargin))
            val content = dialog.holderView
            assertEquals(listOf(5, 6, 7, 8), listOf(content.paddingLeft, content.paddingTop, content.paddingRight, content.paddingBottom))
            val outer = container.parent as View
            val margins = outer.layoutParams as FrameLayout.LayoutParams
            assertEquals(listOf(9, 10, 11, 12), listOf(margins.leftMargin, margins.topMargin, margins.rightMargin, margins.bottomMargin))
        }
    }

    @Test fun centerDefaultsAndHeaderFooterResourcePrecedenceReachTheLayout() {
        lateinit var header: TextView
        lateinit var footer: TextView
        val dialog = show {
            setContentHolder(ViewHolder(android.R.layout.simple_list_item_1)).setGravity(Gravity.CENTER)
            header = TextView(activity).apply { text = "Header override" }
            footer = TextView(activity).apply { text = "Footer override" }
            setHeader(android.R.layout.simple_list_item_1).setHeader(header)
            setFooter(android.R.layout.simple_list_item_1).setFooter(footer)
        }
        ui {
            assertSame(header, dialog.headerView)
            assertSame(footer, dialog.footerView)
            assertTrue(header.isShown && footer.isShown)
            val content = container(dialog).getChildAt(0)
            val margin = activity.resources.getDimensionPixelSize(R.dimen.dialogplus_default_center_margin)
            val params = content.layoutParams as FrameLayout.LayoutParams
            assertEquals(listOf(margin, margin, margin, margin), listOf(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin))
        }
        dismiss(dialog)
        val resources = show {
            setHeader(android.R.layout.simple_list_item_1).setFooter(android.R.layout.simple_list_item_1)
        }
        ui { assertTrue(resources.headerView is TextView); assertTrue(resources.footerView is TextView) }
    }

    @Test fun showDismissAndReuseKeepOwnershipAndNotifyAfterRemoval() {
        var calls = 0
        val detachedAtDismissal = mutableListOf<Boolean>()
        val dialog = ui {
            DialogPlus.newDialog(activity).setAdapter(adapter()).setOnDismissListener {
                detachedAtDismissal += !it.isShowing && container(it).parent.parent == null
                calls++
            }.create().also {
                it.dismiss()
                assertEquals(0, calls)
                assertFalse(it.isShowing)
                it.show()
                it.show()
            }
        }
        val second = show()
        ui { assertTrue(dialog.isShowing); assertTrue(second.isShowing) }
        dismiss(second)
        ui {
            assertTrue(dialog.isShowing)
            dialog.dismiss()
            dialog.dismiss()
            assertTrue(dialog.isShowing)
            assertEquals(0, calls)
        }
        await("first dismissal") { !dialog.isShowing && calls == 1 }
        ui { dialog.show(); assertTrue(dialog.isShowing); dialog.show() }
        dismiss(dialog)
        ui {
            assertEquals(2, calls)
            assertEquals(listOf(true, true), detachedAtDismissal)
            dialog.dismiss()
            assertEquals(2, calls)
        }
    }

    @Test fun emptyContentStillDismissesAndNotifiesAfterRemoval() {
        for (withAdapter in listOf(false, true)) {
            val attachmentsAtDismissal = mutableListOf<Boolean>()
            val dialog = ui {
                DialogPlus.newDialog(activity).apply {
                    if (withAdapter) setAdapter(adapter(emptyList()))
                    setOnDismissListener { attachmentsAtDismissal += it.isShowing }
                }.create().also { it.show() }
            }
            ui { assertEquals(0, container(dialog).height) }
            dismiss(dialog)
            ui { assertEquals(listOf(false), attachmentsAtDismissal) }
        }
    }

    @Test fun backPressAndOverlayRespectCancellationAndCallbackOrder() {
        for (back in listOf(true, false)) for (cancelable in listOf(true, false)) {
            val events = mutableListOf<String>()
            var attachedAtDismissal: Boolean? = null
            val dialog = show {
                setCancelable(cancelable)
                setOnBackPressListener { events += "back" }
                setOnCancelListener { events += "cancel" }
                setOnDismissListener { attachedAtDismissal = it.isShowing; events += "dismiss" }
            }
            if (back) instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
            else {
                // Tap the actual overlay above the bottom-aligned content.
                val point = ui {
                    val root = container(dialog).parent as View
                    val location = IntArray(2)
                    root.getLocationOnScreen(location)
                    location[0] + root.width / 2f to location[1] + container(dialog).top / 2f
                }
                tap(point)
            }
            if (cancelable) {
                await("cancellation dismissal") { !dialog.isShowing && events.lastOrNull() == "dismiss" }
                ui {
                    assertEquals(if (back) listOf("back", "cancel", "dismiss") else listOf("cancel", "dismiss"), events)
                    assertEquals(false, attachedAtDismissal)
                }
            } else {
                ui { assertTrue(dialog.isShowing); assertEquals(if (back) listOf("back") else emptyList<String>(), events) }
                dismiss(dialog)
                ui {
                    assertEquals(if (back) listOf("back", "dismiss") else listOf("dismiss"), events)
                    assertEquals(false, attachedAtDismissal)
                }
            }
        }
    }

    @Test fun draggingExpandsCollapsesAndExpandsAgain() {
        val dialog = show { setExpanded(true, 250) }
        val list = ui { dialog.holderView as ListView }
        val screenHeight = ui { activity.findViewById<View>(android.R.id.content).height }
        ui { assertEquals(250, container(dialog).height) }
        drag(list, -100f)
        await("expanded height") { container(dialog).height == screenHeight }
        // More than 20% of the available height crosses the collapse threshold.
        drag(list, screenHeight * .3f)
        await("collapsed height") { container(dialog).height == 250 }
        drag(list, -100f)
        await("second expansion") { container(dialog).height == screenHeight }
    }

    @Test fun expandedScrolledListScrollsWithoutCollapsingTheDialog() {
        val dialog = show { setExpanded(true); setAdapter(adapter((0..100).map { "item $it" })) }
        val list = ui { dialog.holderView as ListView }
        val screenHeight = ui { activity.findViewById<View>(android.R.id.content).height }
        ui { assertEquals(screenHeight * 2 / 5, container(dialog).height) }
        drag(list, -100f)
        await("expanded height") { container(dialog).height == screenHeight }
        ui { list.setSelectionFromTop(50, 0) }
        await("list scroll position") { list.firstVisiblePosition == 50 }
        drag(list, 100f)
        ui {
            assertTrue("Downward input should scroll towards earlier items", list.firstVisiblePosition < 50)
            assertEquals(screenHeight, container(dialog).height)
        }
    }
}
