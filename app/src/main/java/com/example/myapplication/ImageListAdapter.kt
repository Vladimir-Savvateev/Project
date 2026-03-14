package com.example.myapplication
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ImageListAdapter(
   context: Context,
   val items: List<ImageItem>,
   val onButtonClick: (item: ImageItem) -> Unit
) : BaseAdapter() {

    val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): ImageItem = items[position]

    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.image_buttons_for_main_listview, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as? ViewHolder ?: ViewHolder(view)
        }
        bindView(holder, position)
        return view
    }
    fun bindView(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.imageId)
        holder.textView.text = item.headLine
        holder.imageView.contentDescription = item.headLine
        holder.imageView.setOnClickListener {
            onButtonClick(item)
        }
    }
    class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.ImageButton)
        val textView: TextView = view.findViewById(R.id.textView)
    }
}