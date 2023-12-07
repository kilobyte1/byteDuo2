package com.example.byteduo.Controller

import com.google.firebase.auth.FirebaseAuth

class AdminSignUpController {

     fun updateAdminPassword(userId: String, newPassword: String, callback: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Password updated successfully

                    callback()
                } else {
                    // Password update failed
                    // Handle the failure, e.g., show an error message
                }
            }
    }



}