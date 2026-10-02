package com.orhanobut.dialogplus

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.FrameLayout

/** A dialog attached to an Activity's content view. Create, show and dismiss on the UI thread. */
open class DialogPlus internal constructor(builder: DialogPlusBuilder) {
    private val activity = builder.context as Activity
    private val holder = builder.holder
    private val itemClickListener = builder.onItemClickListener
    private val clickListener = builder.onClickListener
    private val dismissListener = builder.onDismissListener
    private val cancelListener = builder.onCancelListener
    private val backPressListener = builder.onBackPressListener
    private val cancelable = builder.isCancelable
    private val decorView = activity.window.decorView.findViewById<ViewGroup>(android.R.id.content)
    private val rootView: ViewGroup
    private val contentContainer: ViewGroup
    private val outAnim = builder.outAnimation
    private val inAnim = builder.inAnimation
    private var isDismissing = false
    private var unregisterBackCallback: (() -> Unit)? = null

    init {
        val inflater = LayoutInflater.from(builder.context)
        rootView = inflater.inflate(R.layout.base_container, decorView, false) as ViewGroup
        rootView.layoutParams = builder.outmostLayoutParams
        rootView.findViewById<View>(R.id.dialogplus_outmost_container).setBackgroundResource(builder.overlayBackgroundResource)
        contentContainer = rootView.findViewById(R.id.dialogplus_content_container)
        contentContainer.layoutParams = builder.contentParams
        val contentView = createView(inflater, builder.headerView, builder.isFixedHeader,
            builder.footerView, builder.isFixedFooter, builder.adapter)
        val margin = builder.contentMargin
        contentView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
        ).apply { setMargins(margin[0], margin[1], margin[2], margin[3]) }
        val padding = builder.contentPadding
        holderView.setPadding(padding[0], padding[1], padding[2], padding[3])
        contentContainer.addView(contentView)
        if (cancelable) {
            rootView.setOnClickListener {
                cancelListener?.onCancel(this)
                dismiss()
            }
            rootView.setOnTouchListener { view, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) view.performClick()
                true
            }
        }
        if (builder.isExpanded && holder.inflatedView is AbsListView) {
            val view = holder.inflatedView as AbsListView
            view.setOnTouchListener(ExpandTouchListener(view, contentContainer,
                builder.contentParams.gravity, Utils.getDisplayHeight(activity), builder.defaultContentHeight))
        }
    }
    /** Shows this instance once; repeated calls while attached have no effect. */
    open fun show() {
        if (isShowing) return
        decorView.addView(rootView)
        contentContainer.startAnimation(inAnim)
        contentContainer.requestFocus()
        val keyListener = View.OnKeyListener { _, code, event ->
            if (event.action == KeyEvent.ACTION_UP && code == KeyEvent.KEYCODE_BACK) {
                handleBackPress()
                true
            } else false
        }
        holder.setOnKeyListener(keyListener)
        contentContainer.setOnKeyListener(keyListener)
        if (Build.VERSION.SDK_INT >= 33) {
            unregisterBackCallback = Api33BackPress.register(activity) { handleBackPress() }
        }
    }
    open val isShowing: Boolean get() = rootView.parent === decorView
    /** Removes this instance after its exit animation, then notifies the dismissal listener. */
    open fun dismiss() {
        if (isDismissing || !isShowing) return
        isDismissing = true
        unregisterBackCallback?.invoke()
        unregisterBackCallback = null
        val remove = Runnable {
            decorView.removeView(rootView)
            isDismissing = false
            dismissListener?.onDismiss(this)
        }
        // A zero-sized view never draws its animation, so no animation-end callback arrives.
        if (contentContainer.width == 0 || contentContainer.height == 0) {
            decorView.post(remove)
        } else {
            outAnim.setAnimationListener(object : SimpleAnimationListener() {
                override fun onAnimationEnd(animation: Animation) { decorView.post(remove) }
            })
            contentContainer.startAnimation(outAnim)
        }
    }
    open fun findViewById(resourceId: Int): View? = contentContainer.findViewById(resourceId)
    open val headerView: View? get() = holder.header
    open val footerView: View? get() = holder.footer
    open val holderView: View get() = holder.inflatedView

    private fun createView(inflater: LayoutInflater, header: View?, fixedHeader: Boolean,
                           footer: View?, fixedFooter: Boolean, adapter: BaseAdapter?): View {
        val view = holder.getView(inflater, rootView)
        if (holder is ViewHolder) assignClickListenerRecursively(view)
        if (header != null) {
            assignClickListenerRecursively(header)
            holder.addHeader(header, fixedHeader)
        }
        if (footer != null) {
            assignClickListenerRecursively(footer)
            holder.addFooter(footer, fixedFooter)
        }
        if (adapter != null && holder is HolderAdapter) {
            holder.setAdapter(adapter)
            itemClickListener?.let { listener ->
                holder.setOnItemClickListener { item, clickedView, position ->
                    listener.onItemClick(this, item, clickedView, position)
                }
            }
        }
        return view
    }
    private fun assignClickListenerRecursively(view: View) {
        val listener = clickListener ?: return
        if (view is ViewGroup) for (i in view.childCount - 1 downTo 0) assignClickListenerRecursively(view.getChildAt(i))
        if (view.id != View.NO_ID && view !is AdapterView<*>) {
            view.setOnClickListener { listener.onClick(this, it) }
        }
    }
    private fun handleBackPress() {
        backPressListener?.onBackPressed(this)
        if (cancelable) onBackPressed(this)
    }
    /** Cancels this dialog and starts dismissal. Kept for existing callers. */
    open fun onBackPressed(dialogPlus: DialogPlus) {
        cancelListener?.onCancel(this)
        dismiss()
    }
    companion object {
        /** Creates a builder using an Activity context. */
        // Keep the generated Java static method non-final so existing subclasses can hide it.
        @Suppress("NON_FINAL_MEMBER_IN_OBJECT")
        @JvmStatic open fun newDialog(context: Context): DialogPlusBuilder = DialogPlusBuilder(context)
    }
}
