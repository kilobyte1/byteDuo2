package com.example.byteduo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.byteduo.R
import com.example.byteduo.model.MenuItems
import org.w3c.dom.Text

class MenuItemsAdapter : RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {

    private var items: List<MenuItems> = listOf()

    fun setItems(items: List<MenuItems>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_menu_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = items[position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemImageView: ImageView =  itemView.findViewById(R.id.itemImageView)
        private val itemDescription: TextView = itemView.findViewById(R.id.itemDescriptionTextView)

        fun bind(menuItem: MenuItems) {
            val itemImageUrl = menuItem.itemImage
            if (itemImageUrl != null) {
                loadRoundedImage(itemImageUrl, itemImageView, itemView.context)
            }
            itemNameTextView.text = menuItem.itemName
            itemPriceTextView.text = String.format("£%.2f", menuItem.itemPrice)
            itemDescription.text = menuItem.description
        }

        private fun loadRoundedImage(itemImageUrl: String, itemImageView: ImageView, context: Context) {
            Glide.with(context)
                .load(itemImageUrl)
                .circleCrop()
                .into(itemImageView)
        }
    }

}
