package com.example.byteduo.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.byteduo.Controller.AccountController
import com.example.byteduo.EditItemDialogFragment
import com.example.byteduo.R
import com.example.byteduo.adapter.AddItemDialogFragment
import com.example.byteduo.model.FirebaseDBManager
import com.example.byteduo.model.MenuItems
import com.google.firebase.auth.FirebaseAuth

class AdminAccountFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var btnRemove: Button
    private lateinit var adminName: TextView
    private var menuItems = listOf<MenuItems>()
    private lateinit var menuItemsAdapter: ArrayAdapter<MenuItems>
    private val firebaseDBManager = FirebaseDBManager
    private lateinit var btnEditItem: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_account, container, false)

        // Find the logout button directly without null check
        val btnLogout = view.findViewById<Button>(R.id.btnAdminLogout)
        val btnAddItem = view.findViewById<Button>(R.id.btnAddItem)
        btnRemove = view.findViewById(R.id.btnRemoveItem)
        adminName = view.findViewById(R.id.txtAdminName)
        btnEditItem = view.findViewById(R.id.btnEditItem)


        // Initialize menuItemsAdapter
        menuItemsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        menuItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        //set profile name
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            updateAdminName(userId)
        }



        btnAddItem.setOnClickListener {
            // Show the Add Item dialog
            val dialog = AddItemDialogFragment()
            dialog.show(parentFragmentManager, "AddItemDialog")
        }

        // Create an instance of AccountController
        val accountController = AccountController()
        // Set a click listener for the logout button
        btnLogout.setOnClickListener {
            // Call the logout function in the controller
            accountController.onLogout(requireContext())
        }

        //  fetch menu items
        //function in dbmanager
        spinner = view.findViewById(R.id.removeSpinner)

        //btn Remove
        //  fetch menu items
        fetchMenuItems()
        btnRemove.setOnClickListener(){
            removeSelectedItem()
        }

        //btn edit
        btnEditItem.setOnClickListener(){
            handleSelectedItem()
        }

        // Return the inflated view
        return view
    }

    private fun updateAdminName(userId: String) {
        FirebaseDBManager.getAdminInfo(userId) { admin ->
            if (admin != null) {
                requireActivity().runOnUiThread {
                    val name = admin.fullName
                    adminName.text = "Hi, $name"
                }
            }
        }
    }
    private fun fetchMenuItems() {
        firebaseDBManager.getMenuItems { menuItems ->
            // Update the menuItems list
            this.menuItems = menuItems
            // Populate the spinner with menu items
            populateSpinner(menuItems)
        }
    }

    private fun populateSpinner(menuItems: List<MenuItems>) {

        val spinnerItems = menuItems.map { it.itemName ?: "" }
        //set place holder name
        val placeholder = "Select an item"
        val itemsWithPlaceholder = listOf(placeholder) + spinnerItems


        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsWithPlaceholder)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the default selection to the placeholder
        spinner.setSelection(0)
    }

    private fun removeSelectedItem() {
        val selectedMenuItemPosition = spinner.selectedItemPosition

        if (selectedMenuItemPosition != AdapterView.INVALID_POSITION) {
            if (selectedMenuItemPosition == 0) {
                // The placeholder "Select an item" is selected
                showToast("Please select an actual item to remove.")
                return
            }

            val selectedMenuItem = menuItems.getOrNull(selectedMenuItemPosition - 1)
            val itemName = selectedMenuItem?.itemName

            if (itemName != null) {
                // Remove the item from Firebase
                val menuItemsManager = MenuItems()
                menuItemsManager.deleteItem(itemName) {
                    // Show a Toast message indicating successful removal
                    showToast("Item '$itemName' removed successfully.")
                    // Callback to refresh the spinner after successful deletion
                    fetchMenuItems()

                }
            }
        }
    }


    private fun handleSelectedItem() {
        val selectedMenuItemPosition = spinner.selectedItemPosition

        if (selectedMenuItemPosition != AdapterView.INVALID_POSITION) {
            if (selectedMenuItemPosition == 0) {
                // The placeholder "Select an item" is selected
                showToast("Please select an actual item.")
                return
            }

            if (menuItems != null) {
                val selectedMenuItem = menuItems.getOrNull(selectedMenuItemPosition - 1)

                // Open the EditItemDialogFragment and pass the selected item's details
                if (selectedMenuItem != null) {

                    showEditDialog(selectedMenuItem)
                }
            }
        }
    }

    private fun showEditDialog(selectedMenuItem: MenuItems) {
        val dialog = EditItemDialogFragment.newInstance(selectedMenuItem)
        Log.d("MyApp", "Entering updateItem function")

        dialog.show(childFragmentManager, "EditItemDialogFragment")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
