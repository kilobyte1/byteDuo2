package com.example.byteduo.Controller

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.byteduo.View.AdminCompleteInfo
import com.example.byteduo.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Sign_in : AppCompatActivity() {


    lateinit var emailInput: TextInputEditText
    lateinit var passwordInput: TextInputEditText
    lateinit var mAuth: FirebaseAuth
    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val signup = findViewById<TextView>(R.id.txtDontHaveAnAccount)
        val login = findViewById<Button>(R.id.btnLogin)
        emailInput = findViewById(R.id.etEmail)
        passwordInput = findViewById(R.id.etSigninPassword)


        mAuth = FirebaseAuth.getInstance()

        val maxEmailLength = 100
        val minPasswordLength = 6

        //when the user clicks on the sign in text on the sign up page,
        //it should take the user to the sign in page
        signup.setOnClickListener(){
            val intent = Intent(this, Sign_up::class.java)

            startActivity(intent)
        }

        login.setOnClickListener() {
            val email = emailInput.text.toString().trim()
            val enteredPassword = passwordInput.text.toString().trim()



            if (email.isEmpty() || enteredPassword.isEmpty()) {
                // Show error: Both email and password are required
                Toast.makeText(this, "Both email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailRegex = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
            if (email.length > maxEmailLength || !emailRegex.matches(email)) {
                // Show error: Invalid Email Address
                emailInput.setError("Please enter a valid Email Address (maximum $maxEmailLength characters)")
                emailInput.requestFocus()
                return@setOnClickListener
            }

            if (enteredPassword.length < minPasswordLength) {
                // Show error: Password must be at least 6 characters
                passwordInput.setError("Password must be at least $minPasswordLength characters")
                passwordInput.requestFocus()
                return@setOnClickListener
            }

            // Show the wait dialog using DialogUtils
            val dialog = Loading.showWaitDialog(this@Sign_in)



            mAuth.signInWithEmailAndPassword(email, enteredPassword)

                .addOnCompleteListener { task ->
                    //if user details are correct
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        if (user != null) {
                            checkUserStatusAndSignIn(user.uid)
                        }
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "User not found"
                            is FirebaseAuthInvalidCredentialsException -> "Invalid credentials"
                            else -> "Login failed: ${task.exception?.message}"
                        }

                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.e("LoginActivity", "Authentication failed: $errorMessage")
                    }

                    // Dismiss the dialog regardless of success or failure
                    dialog.dismiss()
                }
        }


    }

    private fun checkUserStatusAndSignIn(userId: String?) {
        val customersRef = FirebaseDatabase.getInstance().getReference("customers")
        val adminsRef = FirebaseDatabase.getInstance().getReference("admins")

        if (userId != null) {
            customersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(customerSnapshot: DataSnapshot) {
                    if (customerSnapshot.exists()) {
                        // Check if the user is active
                        val isActive = customerSnapshot.child("active").getValue(Boolean::class.java)

                        if (isActive == true) {
                            // User is active, sign them into CustomerMain
                            // Create an Intent to go to CustomerMain
                            val intent = Intent(this@Sign_in, CustomerMain::class.java)
                            startActivity(intent)
                        } else {
                            // User is not active
                            Log.d("UserStatus", "User is not active")
                            Toast.makeText(this@Sign_in, "Your account has been disabled", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Check the admins node if the user is not found in customers
                        adminsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(adminSnapshot: DataSnapshot) {
                                if (adminSnapshot.exists()) {
                                    // Check if the admin is active
                                    val isActive = adminSnapshot.child("active").getValue(Boolean::class.java)

                                    if (isActive == true) {
                                        // Check if admin details are empty
                                        val adminFullName = adminSnapshot.child("fullName").getValue(String::class.java)
                                        val adminUsername = adminSnapshot.child("username").getValue(String::class.java)
                                        val adminMobile = adminSnapshot.child("mobile").getValue(String::class.java)

                                        //check if the admin details are empty
                                        if (adminFullName.isNullOrEmpty() || adminUsername.isNullOrEmpty() ||
                                            adminMobile.isNullOrEmpty()) {
                                            // Admin details are empty, redirect to AdminCompleteInfo
                                            val intent = Intent(this@Sign_in, AdminCompleteInfo::class.java)
                                            startActivity(intent)
                                        } else {
                                            // Admin details are not empty, sign them into AdminMain
                                            val intent = Intent(this@Sign_in, AdminMain::class.java)
                                            startActivity(intent)
                                        }
                                    } else {
                                        // Admin is not active
                                        Log.d("UserStatus", "Admin is not active")
                                        Toast.makeText(this@Sign_in, "Your admin account has been disabled", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Handle the case when the user does not exist in "admins" node
                                    Log.d("UserStatus", "Admin not found in admins")
                                    Toast.makeText(this@Sign_in, "Admin not found", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle the error case for admins
                                Log.e("UserStatus", "Error retrieving admin status: ${error.message}")
                                Toast.makeText(this@Sign_in, "Error retrieving admin status", Toast.LENGTH_SHORT).show()
                            }
                        })

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error case for customers
                    Log.e("UserStatus", "Error retrieving customer status: ${error.message}")
                    Toast.makeText(this@Sign_in, "Error retrieving customer status", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle the case when userId is null
            Log.d("UserStatus", "User ID is null")
        }
    }





}