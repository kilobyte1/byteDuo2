package com.example.byteduo.adapter

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity


class MenuAdapter(private val context: FragmentActivity, private val items: List<String>): BaseAdapter() {

    private var selectedPosition = -1



    //function to know the selected position of the menu item
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }
    // Function to get the default fragment position
    fun getDefaultFragmentPosition(): Int {

        //default opened fragment should be Hot Teas
        return items.indexOf("Hot Coffee")
    }
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL

        val bullet = "\u2615" // Unicode bullet character
        val textView = TextView(context)

        // Concatenate bullet and item text
        val bulletedText = "$bullet ${items[position]}"
        textView.text = bulletedText
        textView.textSize = 15f

        // Change text color based on selection
        if (position == selectedPosition) {
            textView.setTextColor(Color.WHITE)
        } else {
            textView.setTextColor(Color.BLACK)
        }

        textView.rotation = -90f  // Text oriented upwards

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 100, 0, 110) //
        textView.layoutParams = params

        linearLayout.addView(textView)
        return linearLayout
    }

}
