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
            userReference.child("mobile").setValue(customer.mobile)
            userReference.child("role").setValue(customer.role)
            userReference.child("active").setValue(customer.active)
        }
    }

    fun getCurrentUserId(): String? {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        return currentUser?.uid
    }




    fun getAdminInfo(userId: String, callback: (Admin?) -> Unit) {
        val adminReference = FirebaseDatabase.getInstance().getReference("admins").child(userId)

        adminReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val admin = snapshot.getValue(Admin::class.java)
                    callback(admin)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback(null)
            }
        })
    }


    fun getCustomerInfo(userId: String, callback: (Customer?) -> Unit) {
        val customerReference = FirebaseDatabase.getInstance().getReference("customers").child(userId)

        customerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val customers = snapshot.getValue(Customer::class.java)
                    callback(customers)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback(null)
            }
        })
    }

    fun getNumberOfItemsInCart(userId: String, callback: (Int) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Sum up the quantities of all items in the cart
                var totalQuantity = 0

                for (itemSnapshot in snapshot.children) {
                    val quantity = (itemSnapshot.child("quantity").value as? Long)?.toInt() ?: 0
                    totalQuantity += quantity
                }

                callback(totalQuantity)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(0) // Return 0 in case of an error
            }
        })
    }

    fun getMenuItems(callback: (List<MenuItems>) -> Unit) {
        try {
            // Initialize Firebase Database
            val menuItemsRef = FirebaseDatabase.getInstance().getReference("MenuItems")

            // Fetch menu items from the database
            menuItemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val menuItems = mutableListOf<MenuItems>()

                    for (itemSnapshot in dataSnapshot.children) {
                        val key = itemSnapshot.key
                        val menuItem = itemSnapshot.getValue(MenuItems::class.java)?.copy(itemId = key)
                        menuItem?.let { menuItems.add(it) }
                    }

                    // Invoke the callback with the menu items
                    callback(menuItems)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearUserCart(userId: String) {
        val cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)
        cartReference.removeValue()
    }

     fun clearUserCart() {
        val userId = getCurrentUserId()

        if (userId != null) {
            val cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

            // Remove all items from the cart
            cartReference.removeValue()
        }
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
