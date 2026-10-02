package com.orhanobut.dialogplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/** Holds a layout resource or an existing view, with fixed header and footer containers. */
open class ViewHolder : Holder {
    private var backgroundResource = 0
    private lateinit var container: ViewGroup
    private lateinit var headerContainer: ViewGroup
    private lateinit var footerContainer: ViewGroup
    private var headerView: View? = null
    private var footerView: View? = null
    private var keyListener: View.OnKeyListener? = null
    private var contentView: View? = null
    private var viewResourceId = -1

    constructor(viewResourceId: Int) { this.viewResourceId = viewResourceId }
    constructor(contentView: View) { this.contentView = contentView }

    override fun addHeader(view: View) = addHeader(view, false)
    override fun addHeader(view: View, fixed: Boolean) {
        headerContainer.addView(view)
        headerView = view
    }
    override fun addFooter(view: View) = addFooter(view, false)
    override fun addFooter(view: View, fixed: Boolean) {
        footerContainer.addView(view)
        footerView = view
    }
    override fun setBackgroundResource(colorResource: Int) { backgroundResource = colorResource }
    override fun getView(inflater: LayoutInflater, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.dialog_view, parent, false)
        view.findViewById<View>(R.id.dialogplus_outmost_container).setBackgroundResource(backgroundResource)
        container = view.findViewById(R.id.dialogplus_view_container)
        container.setOnKeyListener(keyListener)
        if (viewResourceId != -1) {
            contentView = inflater.inflate(viewResourceId, parent, false)
        } else {
            (contentView?.parent as? ViewGroup)?.removeView(contentView)
        }
        container.addView(checkNotNull(contentView))
        headerContainer = view.findViewById(R.id.dialogplus_header_container)
        footerContainer = view.findViewById(R.id.dialogplus_footer_container)
        return view
    }
    override fun setOnKeyListener(keyListener: View.OnKeyListener?) {
        this.keyListener = keyListener
        if (::container.isInitialized) container.setOnKeyListener(keyListener)
    }
    override val inflatedView: View get() = checkNotNull(contentView) { "Inflate the holder first" }
    override val header: View? get() = headerView
    override val footer: View? get() = footerView
}
