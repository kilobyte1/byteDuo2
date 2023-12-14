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
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.byteduo.model.MenuItems


class AddItemDialogFragment : DialogFragment() {

    private val PICK_IMAGE_REQUEST = 1

    private val menuItems = listOf("Hot Coffee", "Ice Teas", "Hot Teas", "Bakery", "Drinks")


    //lateinit' allows initializing a not-null property
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var btnAddImage: Button
    private lateinit var btnDone: Button
    private lateinit var itemImagePreview: ImageView

    var imageUri :String =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_item_dialog_form, container, false)
        itemNameEditText = view.findViewById(R.id.editTextName)
        itemPriceEditText = view.findViewById(R.id.editTextPrice)
        itemDescriptionEditText = view.findViewById(R.id.editTextdescription)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        btnDone = view.findViewById(R.id.btnDone)
        itemImagePreview = view.findViewById(R.id.imageView6)
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)



        //spinner for the category since the case sensitivity can affect the retrieval of a menu item to the Home
        //
        val placeholder = "Select Category"
        val itemsWithPlaceholder = listOf(placeholder) + menuItems
        //array adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsWithPlaceholder)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        categorySpinner.adapter = adapter


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
            val category = categorySpinner.selectedItem.toString()
            val image = imageUri

            //if the selected category is not the palce holder name (Category)
            if (category != placeholder){
                if (name.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty() && category.isNotEmpty() && image.isNotEmpty()) {
                    // Convert price to Double
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

            } else{
                Toast.makeText(context, "Invalid category", Toast.LENGTH_SHORT).show()

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

            // store the uri in a separate variable
            imageUri  = selectedImageUri.toString()

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
