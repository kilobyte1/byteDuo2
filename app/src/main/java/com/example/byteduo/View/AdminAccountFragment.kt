package com.example.byteduo.View

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.AccountController
import com.example.byteduo.Controller.Loading
import com.example.byteduo.R
import com.example.byteduo.Model.Admin
import com.example.byteduo.Model.CustomerReview
import com.example.byteduo.Model.FirebaseDBManager
import com.example.byteduo.Model.MenuItems
import com.example.byteduo.View.OrdersAdapter.ReviewsAdapter
import com.google.firebase.auth.FirebaseAuth

class AdminAccountFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var btnRemove: Button
    private lateinit var txtGreeting: TextView
    private var menuItems = listOf<MenuItems>()
    private lateinit var menuItemsAdapter: ArrayAdapter<MenuItems>
    private val firebaseDBManager = FirebaseDBManager
    private lateinit var btnEditItem: Button
    private lateinit var updateDetails: TextView
    private lateinit var txtUpdateEmail: TextView
    private lateinit var txtChangePassword: TextView
    private lateinit var btnViewReview: Button


    private val accountController = AccountController()


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
        txtGreeting = view.findViewById(R.id.txtAdminName)
        btnEditItem = view.findViewById(R.id.btnEditItem)
        updateDetails = view.findViewById(R.id.txtUpdateDetails)
        txtUpdateEmail = view.findViewById(R.id.txtUpdateEmail)
        txtChangePassword = view.findViewById(R.id.txtChangePassword)
        btnViewReview = view.findViewById(R.id.btnViewReviews)


        // Initialize menuItemsAdapter
        menuItemsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        menuItemsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        //set profile name
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            getAdminName(userId)
        }

        updateDetails.setOnClickListener(){
            // Use the getCurrentUserId
            val userId = FirebaseDBManager.getCurrentUserId()

            // Fetch admin details using the AccountController
            if (userId != null) {
                FirebaseDBManager.getAdminInfo(userId) { adminDetails ->

                    showUpdateDetailsDialog(adminDetails)
                }
            }
        }

        txtUpdateEmail.setOnClickListener(){

            showChangeEmailDialog()
        }

        txtChangePassword.setOnClickListener(){

            showChangePasswordDialog()
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



        btnViewReview.setOnClickListener {
            FirebaseDBManager.getAllReviews { reviews ->
                // Create a dialog to display reviews
                showReviewsDialog(reviews)
            }
        }

        // Return the inflated view
        return view
    }

    private fun showReviewsDialog(reviews: List<CustomerReview>) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.dialog_reviews, null)
        dialog.setContentView(dialogView)

        // Find the RecyclerView inside the dialog layout
        val recyclerViewReviews = dialogView.findViewById<RecyclerView>(R.id.recyclerViewReview)

        // Check if recyclerViewReviews is not null before setting the layout manager
        recyclerViewReviews?.layoutManager = LinearLayoutManager(requireContext())

        val reviewsAdapter = ReviewsAdapter(reviews)
        recyclerViewReviews.adapter = reviewsAdapter

        dialog.show()
    }



    private fun showChangePasswordDialog() {

        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.change_password_dialog, null)


        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        builder.setView(dialogView)
            .setPositiveButton("Change Password") { dialog, _ ->
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                accountController.changePassword(requireContext(), newPassword, confirmPassword) { success, errorMessage ->
                    if (success) {
                    } else {
                        // Password change failed, handle the error
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }




    private fun showUpdateDetailsDialog(adminDetails: Admin?) {


        // For simplicity, I'll use AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.update_admin_details_dialog, null)

        // Initialise and set other views
        val fullNameEditText = dialogView.findViewById<EditText>(R.id.etAdminFullName)
        val mobileEditText = dialogView.findViewById<EditText>(R.id.etAdminMobile)
        val usernameEditText = dialogView.findViewById<EditText>(R.id.etAdminUsername)

        // Set values from customerDetails to EditText fields
        fullNameEditText.setText(adminDetails?.fullName)
        mobileEditText.setText(adminDetails?.mobile)
        usernameEditText.setText(adminDetails?.username)

        builder.setView(dialogView)
        builder.setPositiveButton("Update") { dialog, _ ->


            val updatedFullName = fullNameEditText.text.toString()
            val updatedMobile = mobileEditText.text.toString()
            val updatedUsername = usernameEditText.text.toString()



            //commented out because it is not working
            // Show the Loading dialog
            //val waitDialog = Loading.showWaitDialog(requireContext())

            val accountController = AccountController()
            accountController.updateAdminDetailsInDatabase(requireActivity(),updatedFullName, updatedMobile, updatedUsername)


            //update the Full name greetings right after the user updates their details
            val userId = FirebaseDBManager.getCurrentUserId()
            if (userId != null) {
                getAdminName(userId)

                // waitDialog.dismiss()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showChangeEmailDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.change_admin_email_dialog, null)

        // Initialize and set other views
        val newEmailEditText = dialogView.findViewById<EditText>(R.id.etAdminNewEmail)
        val confirmNewEmailEditText = dialogView.findViewById<EditText>(R.id.etAdminConfirmNewEmail)


        builder.setView(dialogView)
        builder.setTitle("Change Email")

        // Positive button for changing email
        builder.setPositiveButton("Change Email") { dialog, _ ->

            val newEmail = newEmailEditText.text.toString()
            val confirmEmail = confirmNewEmailEditText.text.toString()

            // Validate emails
            if (newEmail.isNotEmpty() && confirmEmail.isNotEmpty()) {
                if (newEmail == confirmEmail) {
                    // Show the Loading dialog
                    val waitDialog = Loading.showWaitDialog(requireContext())

                    // Call the function to change the email
                    val accountController = AccountController()
                    accountController.updateAdminEmail(newEmail)

                    // Dismiss the Loading dialog when the update is complete
                    waitDialog.dismiss()

                    // You can show a Toast message without dismissing the AlertDialog
                    Toast.makeText(requireContext(), "A verification email has been sent...", Toast.LENGTH_SHORT).show()
                } else {
                    // Show a Toast message without dismissing the AlertDialog
                    Toast.makeText(requireContext(), "Emails do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter both email fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Negative button for canceling
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the AlertDialog
        builder.show()
    }


    private fun getAdminName(userId: String) {
        FirebaseDBManager.getAdminInfo(userId) { admin ->
            if (admin != null) {
                requireActivity().runOnUiThread {
                    val name = admin.fullName
                    txtGreeting.text = "Hi, $name"
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
