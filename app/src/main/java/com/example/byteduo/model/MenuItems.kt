package com.example.byteduo.model

import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class MenuItems(
    var itemId: String? = null,
    var itemName: String? = null,
    var itemImage: String? = null,
    var itemPrice: Double? = null,
    var description: String? = null,
    var category: String? = null): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemName)
        parcel.writeString(itemImage)
        parcel.writeValue(itemPrice)
        parcel.writeString(description)
        parcel.writeString(category)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MenuItems> {
        override fun createFromParcel(parcel: Parcel): MenuItems {
            return MenuItems(parcel)
        }

        override fun newArray(size: Int): Array<MenuItems?> {
            return arrayOfNulls(size)
        }
    }
    fun addItem(menuItem: MenuItems) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")
        val newItemReference = databaseReference.push()

        val newItemKey = newItemReference.key
        // Add the key to the MenuItems object
        menuItem.itemId = newItemKey

        newItemReference.setValue(menuItem)

    }

    // To update an item
    fun updateItem(itemName: String, updatedMenuItem: MenuItems) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems").child(itemName)
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