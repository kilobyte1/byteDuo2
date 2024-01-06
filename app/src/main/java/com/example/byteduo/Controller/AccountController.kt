package com.example.byteduo.Controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.byteduo.Model.Admin
import com.example.byteduo.Model.Customer
import com.example.byteduo.Model.LogoutHandler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AccountController : LogoutHandler {


    override fun onLogout(context: Context) {
        // Use FirebaseAuth to sign out the user
        FirebaseAuth.getInstance().signOut()

        // Clear cached user credentials
        FirebaseAuth.getInstance().currentUser?.delete()

        // Navigate to the Sign_in activity
        val intent = Intent(context, Sign_in::class.java)
        context.startActivity(intent)

        // Finish the current activity (optional)
        if (context is Activity) {
            context.finish()
        }
    }

    fun updateCustomerDetailsInDatabase(context: Context, fullName: String, mobile: String, username: String) {
        // Get the current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the user ID is not null
        if (userId != null) {
            // Reference to the "Customers" node in the database
            val databaseReference = FirebaseDatabase.getInstance().getReference("customers").child(userId)

            // Retrieve the user's email from the database
            databaseReference.child("email").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the email exists in the database
                    val userEmail = dataSnapshot.value?.toString()

                    // Create a Customer object with updated details
                    val updatedCustomer = Customer(fullName, userEmail, mobile, username)

                    // Update customer details in the database
                    databaseReference.setValue(updatedCustomer)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Customer details updated successfully in the database
                                Toast.makeText(context, "Your details have been updated successfully.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Failed to update customer details in the database
                                Toast.makeText(context, "Failed to update details", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Toast.makeText(context, "Failed to retrieve user's email. Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle the case where the user ID is null (user not authenticated)
            Toast.makeText(context, "You are not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }


    fun updateAdminDetailsInDatabase(context: Context, fullName: String, mobile: String, username: String) {
        // Get the current user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the user ID is not null
        if (userId != null) {
            // Reference to the "Customers" node in the database
            val databaseReference = FirebaseDatabase.getInstance().getReference("admins").child(userId)

            // Retrieve the user's email from the database
            databaseReference.child("email").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if the email exists in the database
                    val userEmail = dataSnapshot.value?.toString()

                    // Create a Customer object with updated details
                    val updatedAdmin = Admin(fullName, userEmail, mobile, username)

                    // Update customer details in the database
                    databaseReference.setValue(updatedAdmin)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Customer details updated successfully in the database
                                Toast.makeText(context, "Admin details updated successfully.", Toast.LENGTH_SHORT).show()
                            } else {
                                // Failed to update customer details in the database
                                Toast.makeText(context, "Failed to update details", Toast.LENGTH_SHORT).show()
                            }
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Toast.makeText(context, "Failed to retrieve user's email. Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            // Handle the case where the user ID is null (user not authenticated)
            Toast.makeText(context, "You are not authenticated.", Toast.LENGTH_SHORT).show()
        }
    }

    private val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$".toRegex()
    fun changePassword(context: Context, newPassword: String, confirmPassword: String, callback: (success: Boolean, errorMessage: String?) -> Unit) {
        // Validate passwords
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            callback.invoke(false, "Passwords cannot be empty.")
            return
        }
        if (newPassword.length < 8 || confirmPassword.length < 8) {
            callback.invoke(false, "Passwords must be at least 8 characters.")
            return
        }
        if (!newPassword.matches(passwordRegex) || !confirmPassword.matches(passwordRegex)) {
            callback.invoke(false, "Password must contain at least one lowercase letter, one uppercase letter, one digit, one special character, and be at least 8 characters long.")
            return
        }
        if (newPassword != confirmPassword) {
            callback.invoke(false, "Passwords do not match.")
            return
        }
        // Proceed to change password
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Password changed successfully
                        callback.invoke(true, null)
                        // Show a Toast message for success
                        Toast.makeText(context, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Password change failed
                        callback.invoke(false, "Password change failed. Please try again.")
                    }
                }
        } else {
            // User is not authenticated
            callback.invoke(false, "You are not authenticated.")
        }
    }


    fun updateEmail(newEmail: String) {
        val user = FirebaseAuth.getInstance().currentUser
        // Check if the user is not null
        user?.verifyBeforeUpdateEmail(newEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Confirmation email will be sent, and when confirmed, the email
                // also update the email in the database
                updateEmailInDatabase(user.uid, newEmail)
                Log.d("UpdateEmail", "Email updated to $newEmail")
            } else {
                // Update failed, handle the error
                Log.e("UpdateEmail", "Failed to update email: ${task.exception}")
            }
        }
    }


    fun updateAdminEmail(newEmail: String) {
        val user = FirebaseAuth.getInstance().currentUser
        // Check if the user is not null
        user?.verifyBeforeUpdateEmail(newEmail)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Confirmation email will be sent, and when confirmed, the email
                // also update the email in the database
                Log.d("UpdateEmail", "Email updated to $newEmail")
            } else {
                // Update failed, handle the error
                Log.e("UpdateEmail", "Failed to update email: ${task.exception}")
            }
        }
    }



    private fun updateEmailInDatabase(userId: String, newEmail: String) {
        // Reference to the "Customers" node in the database
        val databaseReference = FirebaseDatabase.getInstance().getReference("customers").child(userId)

        // Update email field in the database
        databaseReference.child("email").setValue(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Email updated successfully in the database
                    Log.d("UpdateEmail", "Email updated in the database to $newEmail")
                } else {
                    // Update failed in the database, handle the error
                    Log.e("UpdateEmail", "Failed to update email in the database: ${task.exception}")
                }
            }
    }
}