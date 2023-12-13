package com.example.byteduo.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.byteduo.R
import com.example.byteduo.model.MenuItems

class MenuItemsAdapter(
    private val onAddClickListener: (MenuItems, itemCount: Int) -> Unit,
    private val onUpdateCartListener: () -> Unit) : RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {


    private var items: List<MenuItems> = listOf()

    fun setItems(items: List<MenuItems>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_menu_item_view, parent, false)
        return ViewHolder(view, items, onAddClickListener, onUpdateCartListener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = items[position]
        holder.bind(menuItem)

        // Set click listener for the btnAdd
        holder.btnAdd.setOnClickListener {
            onAddClickListener.invoke(menuItem, holder.getItemCount())
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View, private val items: List<MenuItems>,
                     private val onAddClickListener: (MenuItems, itemCount: Int) -> Unit,
                     private val onUpdateCartListener: () -> Unit) : RecyclerView.ViewHolder(itemView) {


        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemImageView: ImageView =  itemView.findViewById(R.id.itemImageView)
        private val itemDescription: TextView = itemView.findViewById(R.id.itemDescriptionTextView)

        //btn add
        val btnAdd: Button = itemView.findViewById(R.id.btnAdd)
        private val btnInc: Button = itemView.findViewById(R.id.incBtn)
        private val btnDec: Button = itemView.findViewById(R.id.decBtn)
        private val counter: TextView = itemView.findViewById(R.id.counter)

        private var itemCount: Int = 0

        //expose the item count
        fun getItemCount(): Int {
            return itemCount
        }

        init {
            // Set click listener for the btnAdd
            btnAdd.setOnClickListener {
                onAddClickListener.invoke(items[adapterPosition], itemCount)
                onUpdateCartListener.invoke()
            }


            //the inc and dec buttons will be used to set the quantity of the item
            //the customer wants to buy before they add to cart

            //handle the addition
            btnInc.setOnClickListener {
                itemCount++
                updateCounter()
            }
            // handle the minus
            btnDec.setOnClickListener {
                if (itemCount > 0) {
                    itemCount--
                    updateCounter()
                }
            }
        }
        //update the counter
        private fun updateCounter() {
            // Update the counter TextView
            counter.text = itemCount.toString()
        }


        //bind the items in the view to one container
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
