package com.orhanobut.android.dialogplussample

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.dialogplus.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    findViewById<View>(R.id.showDialogButton).setOnClickListener { v -> showDialogPlus() }
  }

  private fun showDialogPlus() {
    val holderId = holderRadioGroup.checkedRadioButtonId
    val showHeader = headerCheckBox.isChecked
    val showFooter = footerCheckBox.isChecked
    val fixedHeader = fixedHeaderCheckBox.isChecked
    val fixedFooter = fixedFooterCheckBox.isChecked
    val expanded = expandedCheckBox.isChecked
    val gravity: Int = when (positionRadioGroup!!.checkedRadioButtonId) {
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

    val adapter = SimpleAdapter(this@MainActivity, isGrid, listCountInput.text.toString().toInt())
    val builder = DialogPlus.newDialog(this).apply {
      setContentHolder(holder)

      val header = if (showHeader) R.layout.header else -1
      if (header != -1) {
        setHeader(R.layout.header, fixedHeader)
      }

      val footer = if (showFooter) R.layout.footer else -1
      if (footer != -1) {
        setFooter(R.layout.footer, fixedFooter)
      }

      setCancelable(true)
      setGravity(gravity)
      setAdapter(adapter)
      setOnClickListener { dialog, view ->
        if (view is TextView) {
          toast(view.text.toString())
        }
      }
      setOnItemClickListener { dialog, item, view, position ->
        val textView = view.findViewById<TextView>(R.id.text_view)
        toast(textView.text.toString())
      }
      //        .setOnDismissListener(dismissListener)
      setExpanded(expanded)

      if (contentHeightInput.text.toString().toInt() != -1) {
        setContentHeight(contentHeightInput.text.toString().toInt())
      } else {
        setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
      }

      if (contentWidthInput.text.toString().toInt() != -1) {
        setContentWidth(800)
      }

      setOnCancelListener { dialog -> toast("cancelled") }
      setOverlayBackgroundResource(android.R.color.transparent)
      //        .setContentBackgroundResource(R.drawable.corner_background)
      //                .setOutMostMargin(0, 100, 0, 0)
    }
    builder.create().show()
  }

  private fun toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
  }
}
