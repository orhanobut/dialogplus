package com.orhanobut.android.dialogplussample

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.GridHolder
import com.orhanobut.dialogplus.Holder
import com.orhanobut.dialogplus.ListHolder
import com.orhanobut.dialogplus.ViewHolder

class MainActivity : Activity() {

  private var activeDialog: DialogPlus? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (Build.VERSION.SDK_INT >= 20) {
      Api20WindowInsets.apply(window.decorView)
    }
    findViewById<View>(R.id.showDialogButton).setOnClickListener { showDialogPlus() }
  }

  private fun showDialogPlus() {
    activeDialog?.dismiss()
    val holderRadioGroup = findViewById<RadioGroup>(R.id.holderRadioGroup)
    val positionRadioGroup = findViewById<RadioGroup>(R.id.positionRadioGroup)
    val listCountInput = findViewById<EditText>(R.id.listCountInput)
    val contentHeightInput = findViewById<EditText>(R.id.contentHeightInput)
    val contentWidthInput = findViewById<EditText>(R.id.contentWidthInput)
    val holderId = holderRadioGroup.checkedRadioButtonId
    val showHeader = findViewById<CheckBox>(R.id.headerCheckBox).isChecked
    val showFooter = findViewById<CheckBox>(R.id.footerCheckBox).isChecked
    val fixedHeader = findViewById<CheckBox>(R.id.fixedHeaderCheckBox).isChecked
    val fixedFooter = findViewById<CheckBox>(R.id.fixedFooterCheckBox).isChecked
    val expanded = findViewById<CheckBox>(R.id.expandedCheckBox).isChecked
    val gravity: Int = when (positionRadioGroup.checkedRadioButtonId) {
      R.id.topPosition -> Gravity.TOP
      R.id.centerPosition -> Gravity.CENTER
      else -> Gravity.BOTTOM
    }

    val isGrid: Boolean
    val holder: Holder
    when (holderId) {
      R.id.basic_holder_radio_button -> {
        holder = ViewHolder(R.layout.content)
        isGrid = false
      }
      R.id.list_holder_radio_button -> {
        holder = ListHolder()
        isGrid = false
      }
      else -> {
        holder = GridHolder(3)
        isGrid = true
      }
    }

    val count = (listCountInput.text.toString().toIntOrNull() ?: 16).coerceAtLeast(0)
    val adapter = SimpleAdapter(this, isGrid, count)
    val builder = DialogPlus.newDialog(this).apply {
      setContentHolder(holder)

      if (showHeader) {
        setHeader(R.layout.header, fixedHeader)
      }

      if (showFooter) {
        setFooter(R.layout.footer, fixedFooter)
      }

      setCancelable(true)
      setGravity(gravity)
      setAdapter(adapter)
      setOnClickListener { _, view ->
        if (view is TextView) {
          toast(view.text.toString())
        }
      }
      setOnItemClickListener { _, _, view, _ ->
        val textView = view?.findViewById<TextView>(R.id.text_view)
        if (textView != null) toast(textView.text.toString())
      }
      setExpanded(expanded)
      setContentHeight(contentHeightInput.text.toString().toIntOrNull()
          ?.takeIf { it > 0 } ?: ViewGroup.LayoutParams.WRAP_CONTENT)
      contentWidthInput.text.toString().toIntOrNull()?.takeIf { it > 0 }?.let { setContentWidth(it) }
      setOnCancelListener { toast("cancelled") }
      setOnDismissListener { dialog -> if (activeDialog === dialog) activeDialog = null }
    }
    activeDialog = builder.create().also { it.show() }
  }

  override fun onDestroy() {
    activeDialog?.dismiss()
    activeDialog = null
    super.onDestroy()
  }

  private fun toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}
