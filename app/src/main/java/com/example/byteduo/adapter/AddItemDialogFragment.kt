package com.example.byteduo.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.byteduo.R
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import com.example.byteduo.model.MenuItems


class AddItemDialogFragment : DialogFragment() {

    private val PICK_IMAGE_REQUEST = 1

    interface AddItemListener {
        fun onAddItem(name: String, price: String)
    }

    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var itemCategoryEditText: EditText
    private lateinit var btnAddImage: Button
    private lateinit var btnDone: Button
    private lateinit var itemImagePreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_item_dialog_form, container, false)
        itemNameEditText = view.findViewById(R.id.editTextName)
        itemPriceEditText = view.findViewById(R.id.editTextPrice)
        itemDescriptionEditText = view.findViewById(R.id.editTextdescription)
        itemCategoryEditText = view.findViewById(R.id.editTextcategory)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        btnDone = view.findViewById(R.id.btnDone)
        itemImagePreview = view.findViewById<ImageView>(R.id.imageView6)

        //ope the phone gallery to add an image
        btnAddImage.setOnClickListener {
            // Create an Intent to pick an image from the gallery
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }

        //btnDone.
        btnDone.setOnClickListener {
            val name = itemNameEditText.text.toString()
            val price = itemPriceEditText.text.toString()
            val description = itemDescriptionEditText.text.toString()
            val category = itemCategoryEditText.text.toString()
            val image = btnAddImage.text.toString()

            if (name.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty() && image.isNotEmpty()) {
                // Convert price to Double (assuming price is a valid number)
                val itemPrice = price.toDoubleOrNull()

                if (itemPrice != null) {
                    // Notify the listener (parent fragment or activity)
                    onAddItem(name, itemPrice, description, category, image)
                    Toast.makeText(context, "Item Added", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    // Display an error message if the price is in an invalid format
                    Toast.makeText(context, "Invalid price format", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Display an error message if any required field is empty
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true)
        }
    }

    // Add onActivityResult method to handle the result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data ?: return

            // Use the selected image URI as needed (e.g., display in an ImageView)
            btnAddImage.text = selectedImageUri.toString()

            // Display the selected image in the ImageView
            itemImagePreview.setImageURI(selectedImageUri)

            // Update the text of the button to indicate that an image is selected
            btnAddImage.text = getString(R.string.update_image)
        }
    }


    fun onAddItem (name: String, price: Double, description: String, category: String, image: String) {

                val newItem = MenuItems(
                    itemName = name,
                    itemPrice = price,
                    description = description,
                    category = category,
                    itemImage = image
                )
                // Add the new item to the database
                newItem.addItem(newItem)

    }
}
