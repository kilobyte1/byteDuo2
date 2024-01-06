package com.example.byteduo.View

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class MenuAdapter(private val context: FragmentActivity, private val items: List<String>): BaseAdapter() {

    // The position of the currently selected menu item
    private var selectedPosition = -1

    // Function to set the selected position of the menu item and trigger a data update
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    // Function to get the default position for the initially opened fragment
    fun getDefaultFragmentPosition(): Int {
        // Default opened fragment is "Hot Coffee"
        return items.indexOf("Hot Coffee")
    }

    // Returns the total number of items in the menu
    override fun getCount(): Int = items.size

    // Returns the data item at the specified position
    override fun getItem(position: Int): Any = items[position]

    // Returns the row ID of the item at the specified position
    override fun getItemId(position: Int): Long = position.toLong()

    // Creates and returns a View for the item at the specified position
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Create a vertical LinearLayout to hold the TextView
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL

        // Unicode coffee cup character
        val bullet = "\u2615"
        // Create a TextView for the menu item
        val textView = TextView(context)

        // Concatenate the coffee cup character and the menu item text
        val bulletedText = "$bullet ${items[position]}"
        textView.text = bulletedText
        textView.textSize = 15f

        // Change text color based on selection
        if (position == selectedPosition) {
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setTextColor(Color.BLACK)
        }

        // Rotate the text 90 degrees counter-clockwise
        textView.rotation = -90f

        // Set layout parameters for the TextView
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 100, 0, 110)
        textView.layoutParams = params

        // Add the TextView to the LinearLayout
        linearLayout.addView(textView)

        // Return the LinearLayout as the View for the item
        return linearLayout
    }
}