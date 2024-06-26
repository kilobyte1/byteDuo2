package com.example.byteduo.View

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.byteduo.Model.ImageLoaderUtil
import com.example.byteduo.Model.MenuItems
import com.example.byteduo.Model.MenuItemsCategoriesProvider
import com.example.byteduo.R

class EditItemDialogFragment : DialogFragment() {
    // Request code for picking an image from the gallery
    private val PICK_IMAGE_REQUEST = 1

    // The menu item to be updated
    private lateinit var menuItems: MenuItems
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var itemDescriptionEditText: EditText
    private lateinit var btnChangeImage: Button
    private lateinit var updateImagePreview: ImageView
    private lateinit var btnUpdate: Button
    private lateinit var categorySpinner: Spinner

    private val menuItemsCategories = MenuItemsCategoriesProvider.menuItemsCategories
    private var imageUri :String =""


    companion object {
        private const val ARG_SELECTED_ITEM = "selected_item"

        fun newInstance(selectedItem: MenuItems): EditItemDialogFragment {
            val args = Bundle()
            args.putParcelable(ARG_SELECTED_ITEM, selectedItem)

            val fragment = EditItemDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            menuItems = it.getParcelable(ARG_SELECTED_ITEM)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.edit_item_dialog_form, container, false)
        Log.d("ByteDuo", "Entering onCreateView of EditItemDialogFragment")

        // Initialize and populate UI elements in the dialog form
        itemNameEditText = view.findViewById(R.id.updateItemName)
        itemPriceEditText = view.findViewById(R.id.updateItemPrice)
        itemDescriptionEditText = view.findViewById(R.id.updateItemDescription)
        btnChangeImage = view.findViewById(R.id.btnChangeImage)
        btnUpdate = view.findViewById(R.id.btnUpdate)
        categorySpinner = view.findViewById(R.id.updateItemCategory)
        updateImagePreview = view.findViewById(R.id.updateImagePreview)

        // Populate the UI elements with data from the selected item
        itemNameEditText.setText(menuItems.itemName)
        itemPriceEditText.setText(menuItems.itemPrice.toString())
        itemDescriptionEditText.setText(menuItems.description)


        //set the image
        val imageUrl = menuItems.itemImage
        if (!imageUrl.isNullOrBlank()) {
            // Load the image using the image loader
            ImageLoaderUtil.loadRoundedImage(imageUrl, updateImagePreview, requireContext())
//            Glide.with(requireContext())
//                .load(imageUrl)
//                .into(updateImagePreview)
        }

        //spinner for the category since the case sensitivity can affect the retrieval of a menu item to the Home
        //
        val placeholder = "Select Category"
        val itemsWithPlaceholder = listOf(placeholder) + menuItemsCategories
        //array adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            itemsWithPlaceholder
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        categorySpinner.adapter = adapter

        btnChangeImage.setOnClickListener(){
            // Create an Intent to pick an image from the gallery
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }

        // Set up click listener for the "Update" button
        btnUpdate.setOnClickListener {
            // Get the updated details from the EditText fields
            val updatedName = itemNameEditText.text.toString()
            val updatedPrice = itemPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val updatedDescription = itemDescriptionEditText.text.toString()
            val updatedCategory = categorySpinner.selectedItem.toString()
            Log.d("UpdateItem", "ImageUri: $imageUri")
            val updatedImage = imageUri
            if (updatedCategory != placeholder && updatedName.isNotBlank() && updatedPrice != null && updatedDescription.isNotBlank()) {
                // Update the properties of updatedMenuItem with the new data
                menuItems.itemName = updatedName
                menuItems.itemPrice = updatedPrice
                menuItems.itemImage = updatedImage
                menuItems.description = updatedDescription
                menuItems.category = updatedCategory
                Log.d("UpdateItem", "UpdatedMenuItem: $menuItems")

                // Call the function to update the item
                updateItem(menuItems)
                dismiss()
            } else{
                showToast("Please fill in all fields and select a category")
            }
        }
        return view
    }

    // Function to update the item
    private fun updateItem(updatedDetails: MenuItems) {
        val updateItemTask = menuItems.updateMenuItem(updatedDetails)
        updateItemTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Update successful
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri = data.data ?: return
            // store the uri in a separate variable
            imageUri  = selectedImageUri.toString()
            // Display the selected image in the ImageView
            updateImagePreview.setImageURI(selectedImageUri)
            // Update the text of the button to indicate that an image is selected
            btnChangeImage.text = getString(R.string.change_image)
        }
    }

}