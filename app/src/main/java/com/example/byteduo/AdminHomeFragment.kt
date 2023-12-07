package com.example.byteduo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.byteduo.model.Admin
import com.example.byteduo.model.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [adminHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        // Initialization code
        val userName = view.findViewById<TextView>(R.id.txtUserName)
        val search = view.findViewById<EditText>(R.id.txtSearch)
        val addBtn = view.findViewById<Button>(R.id.btnAdd)
        val editBtn = view.findViewById<Button>(R.id.btnEdit)
        val deleteBtn = view.findViewById<Button>(R.id.btnDelete)


        // Check for null after findViewById
        if (userName == null ) {
            Log.e("AdminHomeFragment", "null")
            return view
        }

        // Set an OnClickListener to enable focus when search is clicked
        search.setOnClickListener { search.isFocusableInTouchMode = true
            search.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().reference
            val adminReference = database.child("admins").child(userId)

            adminReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("SnapshotData", snapshot.toString()) // Log the snapshot data

                    if (snapshot.exists()) {
                        val admin = snapshot.getValue(Admin::class.java)
                        Log.d("User", "Admin is $admin")
                        if (admin != null) {
                            requireActivity().runOnUiThread {
                                val name = admin.username
                                userName.text = name
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }

        return view
    }



    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AdminHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}




