package com.example.byteduo.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import com.example.byteduo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is already logged in with Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {

            // User is already logged in, determine the user type and redirect accordingly
            determineUserTypeAndRedirect(currentUser.uid)
        } else {
            val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)

            btnGetStarted.setOnClickListener() {
                applyShadowAnimation(btnGetStarted)
                val intent = Intent(this, Sign_up::class.java)
                startActivity(intent)
            }
        }
    }

    private fun determineUserTypeAndRedirect(userId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val adminsReference = database.child("admins").child(userId)

        adminsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Admin is logged in, redirect to admin home screen
                    val intent = Intent(this@MainActivity, AdminMain::class.java)
                    startActivity(intent)
                } else {
                    // Customer is logged in, redirect to customer home screen
                    val intent = Intent(this@MainActivity, CustomerMain::class.java)
                    startActivity(intent)
                }
                finish()  // Finish the current activity to prevent the user from going back to the login screen
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun applyShadowAnimation(view: View) {
        // Animate the button with a shadow-like effect
        view.animate()
            .scaleX(3.1f)
            .scaleY(3.1f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // After the animation completes, reset the scale to its original size
                view.scaleX = 1f
                view.scaleY = 1f
            }
            .start()
    }
}
