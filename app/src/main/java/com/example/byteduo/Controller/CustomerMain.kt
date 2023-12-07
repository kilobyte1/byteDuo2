package com.example.byteduo.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.byteduo.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerMain : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.btm_nav)
        val navController = Navigation.findNavController(this, R.id.nav_cus_fragment)
        NavigationUI.setupWithNavController(bottomNavigation, navController)
    }
}