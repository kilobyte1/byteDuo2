package com.example.byteduo.View

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Window
import android.widget.Button
import android.widget.Toast
import com.example.byteduo.Controller.AdminMain
import com.example.byteduo.Controller.AdminSignUpController
import com.example.byteduo.Controller.CustomerMain
import com.example.byteduo.R
import com.example.byteduo.model.FirebaseDBManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminCompleteInfo : AppCompatActivity() {

    lateinit var fullNameInput: TextInputEditText
    lateinit var usernameInput: TextInputEditText
    lateinit var passwordInput: TextInputEditText
    lateinit var confirmPasswordInput: TextInputEditText
    lateinit var mobileNumberInput: TextInputEditText
    lateinit var btnRegister: Button
    lateinit var mAuth: FirebaseAuth
    lateinit var dialog: Dialog

    // Database reference
    lateinit var adminsRef: DatabaseReference
    lateinit var adminSignUpController: AdminSignUpController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_complete_info)


        adminSignUpController = AdminSignUpController()
        mAuth = FirebaseAuth.getInstance()
        adminsRef = FirebaseDatabase.getInstance().getReference("admins")

        btnRegister = findViewById(R.id.btnRegister)
        fullNameInput = findViewById(R.id.etFullName)
        usernameInput = findViewById(R.id.etUserName)
        passwordInput = findViewById(R.id.etPassword)
        confirmPasswordInput = findViewById(R.id.etConfirmPassword)
        mobileNumberInput = findViewById(R.id.etMobileNumber)

        // Fetch existing admin details and populate the fields
        fetchAdminDetails()


        btnRegister.setOnClickListener() {
            registerAdmin()
        }
    }

    private fun fetchAdminDetails() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            adminsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(adminSnapshot: DataSnapshot) {
                    if (adminSnapshot.exists()) {
                        val adminFullName =
                            adminSnapshot.child("fullName").getValue(String::class.java)
                        val adminUsername =
                            adminSnapshot.child("username").getValue(String::class.java)
                        val adminMobile = adminSnapshot.child("mobile").getValue(String::class.java)
                        // Add other admin details as needed

                        // Populate the fields that already exist in the database
                        fullNameInput.setText(adminFullName)
                        usernameInput.setText(adminUsername)
                        mobileNumberInput.setText(adminMobile)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error case
                }
            })
        }
    }

    private fun registerAdmin() {

        try {


            val userId = mAuth.currentUser?.uid
            if (userId != null) {
                val fullName = fullNameInput.text.toString().trim()
                val username = usernameInput.text.toString().lowercase().trim()
                val mobile = mobileNumberInput.text.toString().trim()
                val enteredPassword = passwordInput.text.toString()
                val enteredConfirmPassword = confirmPasswordInput.text.toString()


                // Set maximum lengths
                val maxFullNameLength = 50
                val maxUsernameLength = 20
                val maxMobileLength = 15
                val maxPasswordLength = 20
                val maxConfirmPasswordLength = 20

                if (fullName.isEmpty() || username.isEmpty() || mobile.isEmpty() || enteredPassword.isEmpty() || enteredConfirmPassword.isEmpty()) {
                    // Show error: All fields are required
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                    return
                } else if (fullName.length < 2) {
                    fullNameInput.setError("Full name should not be less than 2 characters")
                    fullNameInput.requestFocus()
                    return
                } else if (enteredPassword != enteredConfirmPassword) {
                    // Show error: Password does not match
                    confirmPasswordInput.setError("Password does not match")
                    confirmPasswordInput.requestFocus()
                    return
                } else if (enteredPassword.length < 8) {
                    // Show error: Password must be at least 8 characters
                    passwordInput.setError("Password must be at least 8 characters")
                    passwordInput.requestFocus()
                    return
                } else if (mobile.length < 10) {
                    //sanitize mobile number
                    mobileNumberInput.setError("Invalid mobile number. It must be at least 10 digits.")
                    mobileNumberInput.requestFocus()
                    return
                } else if (!mobile.matches(Regex("[0-9]+"))) {
                    mobileNumberInput.setError("Invalid mobile number. It should only contain digits.")
                    mobileNumberInput.requestFocus()
                    return
                } else if (!mobile.startsWith("0")) {
                    mobileNumberInput.setError("Invalid mobile number. It should start with 0")
                    mobileNumberInput.requestFocus()
                    return
                } else if (fullName.length > maxFullNameLength) {
                    // Show error: Full name is too long
                    fullNameInput.setError("Full name is too long (maximum $maxFullNameLength characters)")
                    fullNameInput.requestFocus()
                    return
                } else if (username.length > maxUsernameLength) {
                    // Show error: Username is too long
                    usernameInput.setError("Username is too long (maximum $maxUsernameLength characters)")
                    usernameInput.requestFocus()
                    return
                } else if (mobile.length > maxMobileLength) {
                    // Show error: Mobile number is too long
                    mobileNumberInput.setError("Mobile number is too long (maximum $maxMobileLength characters)")
                    mobileNumberInput.requestFocus()
                    return

                } else if (enteredPassword.length > maxPasswordLength) {
                    // Show error: Password is too long
                    passwordInput.setError("Password is too long (maximum $maxPasswordLength characters)")
                    passwordInput.requestFocus()
                    return
                } else if (enteredConfirmPassword.length > maxConfirmPasswordLength) {
                    // Show error: Confirmation password is too long
                    confirmPasswordInput.setError("Confirmation password is too long (maximum $maxConfirmPasswordLength characters)")
                    confirmPasswordInput.requestFocus()
                    return
                } else {
                    //load
                    dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.dialog_wait)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                    // Update admin details in the database

                    adminsRef.child(userId).child("fullName").setValue(fullName)
                    adminsRef.child(userId).child("username").setValue(username)
                    adminsRef.child(userId).child("mobile").setValue(mobile)

                    //update the password in authenticator
                    adminSignUpController.updateAdminPassword(userId, enteredPassword) {
                        Log.d("UpdatePassword", "Update password callback executed")
                        dialog.dismiss()
                        //when update is successful
                        Toast.makeText(
                            this,
                            "Registration completed successfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val intent = Intent(this, AdminMain::class.java)
                        startActivity(intent)
                        // Finish the activity or navigate to another screen if needed
                        finish()
                    }


                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("RegisterAdmin", "Exception: ${e.message}")
        }
    }
}