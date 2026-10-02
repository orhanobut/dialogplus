package com.orhanobut.dialogplus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView

/** Platform GridView content with optional header and footer views. */
open class GridHolder(private val columnNumber: Int) : HolderAdapter, AdapterView.OnItemClickListener {
    private var backgroundResource = 0
    private lateinit var itemView: GridView
    private lateinit var headerContainer: ViewGroup
    private lateinit var footerContainer: ViewGroup
    private var listener: OnHolderListener? = null
    private var keyListener: View.OnKeyListener? = null
    private var headerView: View? = null
    private var footerView: View? = null

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
    override fun setAdapter(adapter: BaseAdapter) { itemView.adapter = adapter }
    override fun setBackgroundResource(colorResource: Int) { backgroundResource = colorResource }
    override fun getView(inflater: LayoutInflater, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.dialog_grid, parent, false)
        view.findViewById<View>(R.id.dialogplus_outmost_container).setBackgroundResource(backgroundResource)
        itemView = view.findViewById(R.id.dialogplus_list)
        itemView.numColumns = columnNumber
        itemView.onItemClickListener = this
        itemView.setOnKeyListener { v, code, event ->
            checkNotNull(keyListener) { "keyListener should not be null" }.onKey(v, code, event)
        }
        headerContainer = view.findViewById(R.id.dialogplus_header_container)
        footerContainer = view.findViewById(R.id.dialogplus_footer_container)
        return view
    }
    override fun setOnItemClickListener(listener: OnHolderListener?) { this.listener = listener }
    override fun setOnKeyListener(keyListener: View.OnKeyListener?) { this.keyListener = keyListener }
    override val inflatedView: View get() = itemView
    override val header: View? get() = headerView
    override val footer: View? get() = footerView
    override fun onItemClick(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val callback = listener ?: return
        callback.onItemClick(parent.getItemAtPosition(position), view, position)
    }
}
