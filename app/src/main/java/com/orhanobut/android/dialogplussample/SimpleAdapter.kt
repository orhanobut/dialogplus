package com.orhanobut.android.dialogplussample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class SimpleAdapter(
    context: Context,
    private val isGrid: Boolean,
    private val count: Int
) : BaseAdapter() {

  private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

  override fun getCount(): Int {
    return count
  }

  override fun getItem(position: Int): Any {
    return position
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val viewHolder: ViewHolder
    var view: View? = convertView

    if (view == null) {
      view = if (isGrid) {
        layoutInflater.inflate(R.layout.simple_grid_item, parent, false)
      } else {
        layoutInflater.inflate(R.layout.simple_list_item, parent, false)
      }

      viewHolder = ViewHolder(
          view.findViewById(R.id.text_view),
          view.findViewById(R.id.image_view)
      )
      view.tag = viewHolder
    } else {
      viewHolder = view.tag as ViewHolder
    }

    val context = parent.context
    when (position) {
      0 -> {
        viewHolder.textView.text = context.getString(R.string.google_plus_title)
        viewHolder.imageView.setImageResource(R.drawable.ic_google_plus_icon)
      }
      1 -> {
        viewHolder.textView.text = context.getString(R.string.google_maps_title)
        viewHolder.imageView.setImageResource(R.drawable.ic_google_maps_icon)
      }
      else -> {
        viewHolder.textView.text = context.getString(R.string.google_messenger_title)
        viewHolder.imageView.setImageResource(R.drawable.ic_google_messenger_icon)
      }
    }

    return view!!
  }

  data class ViewHolder(val textView: TextView, val imageView: ImageView)
}
