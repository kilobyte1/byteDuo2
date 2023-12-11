package com.example.byteduo.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


object FirebaseDBManager {

    private val databaseReference = FirebaseDatabase.getInstance().reference

    fun addCustomer(customer: Customer) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val userReference = databaseReference.child("customers").child(it)
            userReference.child("email").setValue(customer.email)
            userReference.child("fullName").setValue(customer.fullName)
            userReference.child("username").setValue(customer.username)
            userReference.child("role").setValue(customer.role)
            userReference.child("isActive").setValue(customer.isActive)
        }
    }

    fun getCurrentUserId(): String? {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        return currentUser?.uid
    }



//    fun addAdmin(admin: Admin) {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//        userId?.let {
//            val userReference = databaseReference.child("admins").child(it)
//            userReference.setValue(admin)
//        }
//    }


//     fun checkUsernameAvailability(username: String, listener: UsernameAvailabilityListener) {
//        val usersRef = FirebaseDatabase.getInstance().getReference("customers")
//        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Username already exists
//                        listener.onUsernameUnavailable()
//                    } else {
//                        // Username is available
//                        listener.onUsernameAvailable()
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    // Handle errors
//                }
//            }
//        )
//    }

//    fun checkEmailAvailability(email: String, listener: EmailAvailabilityListener) {
//        val usersRef = FirebaseDatabase.getInstance().getReference("users")
//        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Email already exists
//                        listener.onEmailUnavailable()
//                    } else {
//                        // Email is available
//                        listener.onEmailAvailable()
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    // Handle errors
//                }
//            }
//        )
//    }

//    interface EmailAvailabilityListener {
//        fun onEmailAvailable()
//        fun onEmailUnavailable()
//    }
//
//    interface UsernameAvailabilityListener {
//        fun onUsernameAvailable()
//        fun onUsernameUnavailable()
//    }
//
//    private val usersRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("customers")
//
//    fun getEncryptedPasswordByUsername(username: String, callback: (String?) -> Unit) {
//        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(
//            object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Username found, retrieve encrypted password
//                        val userSnapshot = dataSnapshot.children.first()
//                        val encryptedPassword = userSnapshot.child("password").getValue(String::class.java)
//                        callback(encryptedPassword)
//                    } else {
//                        // Username not found
//                        callback(null)
//                    }
//                }
//
//                override fun onCancelled(databaseError: DatabaseError) {
//                    // Handle errors
//                    callback(null)
//                }
//            }
//        )
//    }


}
