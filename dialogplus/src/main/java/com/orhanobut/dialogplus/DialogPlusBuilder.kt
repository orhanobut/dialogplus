package com.orhanobut.dialogplus

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.FrameLayout

/** Configures a dialog. Dimensions and margins are pixels. Use an Activity context. */
open class DialogPlusBuilder internal constructor(open val context: Context) {
    private val margin = IntArray(4) { -1 }
    private val padding = IntArray(4)
    private val outMostMargin = IntArray(4)
    private val params = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM,
    )
    private var adapterValue: BaseAdapter? = null
    private var footerViewValue: View? = null
    private var headerViewValue: View? = null
    private var holderValue: Holder? = null
    private var itemClickListener: OnItemClickListener? = null
    private var clickListener: OnClickListener? = null
    private var dismissListener: OnDismissListener? = null
    private var cancelListener: OnCancelListener? = null
    private var backPressListener: OnBackPressListener? = null
    private var cancelable = true
    private var contentBackground = android.R.color.white
    private var headerResource = -1
    private var fixedHeader = false
    private var footerResource = -1
    private var fixedFooter = false
    private var inAnimationResource = -1
    private var outAnimationResource = -1
    private var expanded = false
    private var defaultHeight = 0
    private var overlayBackground = R.color.dialogplus_black_overlay

    open fun setAdapter(adapter: BaseAdapter): DialogPlusBuilder = apply { adapterValue = adapter }
    open fun setFooter(resourceId: Int): DialogPlusBuilder = setFooter(resourceId, false)
    open fun setFooter(resourceId: Int, fixed: Boolean): DialogPlusBuilder = apply {
        footerResource = resourceId
        fixedFooter = fixed
    }
    open fun setFooter(view: View): DialogPlusBuilder = setFooter(view, false)
    open fun setFooter(view: View, fixed: Boolean): DialogPlusBuilder = apply {
        footerViewValue = view
        fixedFooter = fixed
    }
    open fun setHeader(resourceId: Int): DialogPlusBuilder = setHeader(resourceId, false)
    open fun setHeader(resourceId: Int, fixed: Boolean): DialogPlusBuilder = apply {
        headerResource = resourceId
        fixedHeader = fixed
    }
    open fun setHeader(view: View): DialogPlusBuilder = setHeader(view, false)
    open fun setHeader(view: View, fixed: Boolean): DialogPlusBuilder = apply {
        headerViewValue = view
        fixedHeader = fixed
    }
    open fun setCancelable(isCancelable: Boolean): DialogPlusBuilder = apply { cancelable = isCancelable }
    open fun setContentHolder(holder: Holder): DialogPlusBuilder = apply { holderValue = holder }
    @Deprecated("Use setContentBackgroundResource", ReplaceWith("setContentBackgroundResource(resourceId)"))
    open fun setBackgroundColorResId(resourceId: Int): DialogPlusBuilder = setContentBackgroundResource(resourceId)
    open fun setContentBackgroundResource(resourceId: Int): DialogPlusBuilder = apply { contentBackground = resourceId }
    open fun setOverlayBackgroundResource(resourceId: Int): DialogPlusBuilder = apply { overlayBackground = resourceId }
    open fun setGravity(gravity: Int): DialogPlusBuilder = apply {
        params.gravity = gravity
    }
    open fun setInAnimation(inAnimResource: Int): DialogPlusBuilder = apply { inAnimationResource = inAnimResource }
    open fun setOutAnimation(outAnimResource: Int): DialogPlusBuilder = apply { outAnimationResource = outAnimResource }
    open fun setOutMostMargin(left: Int, top: Int, right: Int, bottom: Int): DialogPlusBuilder = apply {
        outMostMargin[0] = left
        outMostMargin[1] = top
        outMostMargin[2] = right
        outMostMargin[3] = bottom
    }
    open fun setMargin(left: Int, top: Int, right: Int, bottom: Int): DialogPlusBuilder = apply {
        margin[0] = left
        margin[1] = top
        margin[2] = right
        margin[3] = bottom
    }
    open fun setPadding(left: Int, top: Int, right: Int, bottom: Int): DialogPlusBuilder = apply {
        padding[0] = left
        padding[1] = top
        padding[2] = right
        padding[3] = bottom
    }
    open fun setOnItemClickListener(listener: OnItemClickListener?): DialogPlusBuilder = apply { itemClickListener = listener }
    open fun setOnClickListener(listener: OnClickListener?): DialogPlusBuilder = apply { clickListener = listener }
    open fun setOnDismissListener(listener: OnDismissListener?): DialogPlusBuilder = apply { dismissListener = listener }
    open fun setOnCancelListener(listener: OnCancelListener?): DialogPlusBuilder = apply { cancelListener = listener }
    open fun setOnBackPressListener(listener: OnBackPressListener?): DialogPlusBuilder = apply { backPressListener = listener }
    open fun setExpanded(expanded: Boolean): DialogPlusBuilder = apply { this.expanded = expanded }
    open fun setExpanded(expanded: Boolean, defaultContentHeight: Int): DialogPlusBuilder = apply {
        this.expanded = expanded
        defaultHeight = defaultContentHeight
    }
    open fun setContentHeight(height: Int): DialogPlusBuilder = apply { params.height = height }
    open fun setContentWidth(width: Int): DialogPlusBuilder = apply { params.width = width }
    open fun create(): DialogPlus {
        holder.setBackgroundResource(contentBackgroundResource)
        return DialogPlus(this)
    }
    open val footerView: View? get() = Utils.getView(context, footerResource, footerViewValue)
    open val headerView: View? get() = Utils.getView(context, headerResource, headerViewValue)
    open val holder: Holder get() = holderValue ?: ListHolder().also { holderValue = it }
    open val adapter: BaseAdapter? get() = adapterValue
    open val inAnimation: Animation get() = AnimationUtils.loadAnimation(context,
        if (inAnimationResource == -1) Utils.getAnimationResource(params.gravity, true) else inAnimationResource)
    open val outAnimation: Animation get() = AnimationUtils.loadAnimation(context,
        if (outAnimationResource == -1) Utils.getAnimationResource(params.gravity, false) else outAnimationResource)
    open val contentParams: FrameLayout.LayoutParams get() {
        if (expanded) params.height = defaultContentHeight
        return params
    }
    open val isExpanded: Boolean get() = expanded
    open val outmostLayoutParams: FrameLayout.LayoutParams get() = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
    ).apply { setMargins(outMostMargin[0], outMostMargin[1], outMostMargin[2], outMostMargin[3]) }
    open val isCancelable: Boolean get() = cancelable
    open val onItemClickListener: OnItemClickListener? get() = itemClickListener
    open val onClickListener: OnClickListener? get() = clickListener
    open val onDismissListener: OnDismissListener? get() = dismissListener
    open val onCancelListener: OnCancelListener? get() = cancelListener
    open val onBackPressListener: OnBackPressListener? get() = backPressListener
    open val contentMargin: IntArray get() {
        val minimumMargin = context.resources.getDimensionPixelSize(R.dimen.dialogplus_default_center_margin)
        for (i in margin.indices) margin[i] = DialogLayoutRules.margin(params.gravity, margin[i], minimumMargin)
        return margin
    }
    open val contentPadding: IntArray get() = padding
    open val defaultContentHeight: Int get() {
        if (defaultHeight == 0) defaultHeight = DialogLayoutRules.defaultHeight(Utils.getDisplayHeight(context as Activity))
        return defaultHeight
    }
    open val overlayBackgroundResource: Int get() = overlayBackground
    open val contentBackgroundResource: Int get() = contentBackground
    open val isFixedHeader: Boolean get() = fixedHeader
    open val isFixedFooter: Boolean get() = fixedFooter
}
