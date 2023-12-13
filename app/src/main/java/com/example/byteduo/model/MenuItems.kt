package com.example.byteduo.model

import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class MenuItems(
    val itemName: String? = null,
    val itemImage: String? = null,
    val itemPrice: Double? = null,
    val description: String? = null,
    val category: String? = null) {

    fun addItem(menuItem: MenuItems) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")
        val newItemReference = databaseReference.push()
        newItemReference.setValue(menuItem)
    }

    // To update an item
    fun updateItem(itemId: String, updatedMenuItem: MenuItems) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems").child(itemId)
        databaseReference.setValue(updatedMenuItem)
    }

    // To delete an item
    fun deleteItem(itemName: String, callback: () -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")
        val query = databaseReference.orderByChild("itemName").equalTo(itemName)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (itemSnapshot in dataSnapshot.children) {
                    itemSnapshot.ref.removeValue().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            callback.invoke()

                        } else {
                            // Handle the error if necessary
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if necessary
            }
        })
    }
}