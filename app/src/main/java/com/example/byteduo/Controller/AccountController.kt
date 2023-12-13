package com.example.byteduo.Controller

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.byteduo.model.LogoutHandler
import com.google.firebase.auth.FirebaseAuth

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


}