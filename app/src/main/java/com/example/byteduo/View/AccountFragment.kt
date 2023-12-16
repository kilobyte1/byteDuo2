package com.example.byteduo.View

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.byteduo.Controller.AccountController
import com.example.byteduo.Controller.Loading
import com.example.byteduo.R
import com.example.byteduo.model.Customer
import com.example.byteduo.model.FirebaseDBManager
import com.example.byteduo.model.FirebaseDBManager.getCurrentUserId
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {
    private lateinit var imgProfile: ImageView
    private lateinit var txtGreeting: TextView
    private lateinit var btnUpdateDetails: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button
    private lateinit var btnChanEmail: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        txtGreeting = view.findViewById(R.id.txtGreeting)
        btnUpdateDetails = view.findViewById(R.id.btnUpdateDetails)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnChanEmail = view.findViewById(R.id.btnChangeEmail)

        //set profile name
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            getCusName(userId)
        }

        btnUpdateDetails.setOnClickListener {
            // Use the getCurrentUserId or any other method to get the current user's ID
            val userId = getCurrentUserId()

            // Fetch customer details using the AccountController
            if (userId != null) {
                FirebaseDBManager.getCustomerInfo(userId) { customerDetails ->
                    // Now, you have the customer details, open the dialog form
                    showUpdateDetailsDialog(customerDetails)
                }
            }
        }

        btnChanEmail.setOnClickListener(){
            showChangeEmailDialog()
        }


        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        btnLogout.setOnClickListener {
            // Handle logout
            val accountController = AccountController()
            accountController.onLogout(requireContext())
        }


        return view
    }

    private fun showChangeEmailDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.change_email_dialog, null)

        // Initialize and set other views
        val newEmailEditText = dialogView.findViewById<EditText>(R.id.etNewEmail)
        val confirmNewEmailEditText = dialogView.findViewById<EditText>(R.id.etConfirmNewEmail)


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
                    accountController.updateEmail(newEmail)

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





    private fun showUpdateDetailsDialog(customerDetails: Customer?) {


        // For simplicity, I'll use AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.update_details_dialog, null)

        // Initialise and set other views
        val fullNameEditText = dialogView.findViewById<EditText>(R.id.etFullName)
        val mobileEditText = dialogView.findViewById<EditText>(R.id.etMobile)
        val usernameEditText = dialogView.findViewById<EditText>(R.id.etUsername)

        // Set values from customerDetails to EditText fields
        fullNameEditText.setText(customerDetails?.fullName)
        mobileEditText.setText(customerDetails?.mobile)
        usernameEditText.setText(customerDetails?.username)

        builder.setView(dialogView)
        builder.setPositiveButton("Update") { dialog, _ ->


            val updatedFullName = fullNameEditText.text.toString()
            val updatedMobile = mobileEditText.text.toString()
            val updatedUsername = usernameEditText.text.toString()



            //commented out because it is not working
            // Show the Loading dialog
            //val waitDialog = Loading.showWaitDialog(requireContext())

            val accountController = AccountController()
            accountController.updateCustomerDetailsInDatabase(requireActivity(),updatedFullName, updatedMobile, updatedUsername)


            //update the Full name greetings right after the user updates their details
            val userId = getCurrentUserId()
            if (userId != null) {
                getCusName(userId)

               // waitDialog.dismiss()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.change_password_dialog, null)

        // Initialize and set other views
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.etNewPassword)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.etConfirmPassword)

        builder.setView(dialogView)
        //builder.setTitle("Change Password")

        // Positive button for changing password
        builder.setPositiveButton("Change Password") { dialog, _ ->

            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validate passwords
            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword == confirmPassword) {
                // Passwords match, proceed with changing the password
                // Show the Loading dialog
                val waitDialog = Loading.showWaitDialog(requireContext())

                // Call the function to change the password
                val accountController = AccountController()
                accountController.changePassword(requireContext(),newPassword)

                waitDialog.dismiss()

            } else {
                // Passwords don't match
                //Toast.makeText(this,"Passwords do not match")
            }

            dialog.dismiss()
        }

        // Negative button for canceling
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }



    private fun getCusName(userId: String) {
        FirebaseDBManager.getCustomerInfo(userId) { customer ->
            if (customer != null) {
                requireActivity().runOnUiThread {
                    val name = customer.fullName
                    txtGreeting.text = "Hi, $name"
                }
            }
        }
    }
}
