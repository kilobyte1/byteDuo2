package com.example.byteduo.View

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.byteduo.Controller.Loading
import com.example.byteduo.Controller.OrderHandler
import com.example.byteduo.Controller.OrderHandler.Companion.retrieveCartItemsFromDatabase
import com.example.byteduo.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CardPaymentActivity : AppCompatActivity() {


    private lateinit var cardNumberEditText: EditText
    private lateinit var cvvEditText: EditText
    private lateinit var expiryEditText: EditText

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_payment)

        // Initialize UI elements
        cardNumberEditText = findViewById(R.id.etCardNumber)
        cvvEditText = findViewById(R.id.etCVV)
        expiryEditText = findViewById(R.id.etExpiry)

        val payButton: Button = findViewById(R.id.btnPay)

         payButton.setOnClickListener {
             if (isValidCardDetails()) {
                 // Show a waiting dialog while processing the payment
                 val waitDialog = Loading.showWaitDialog(this@CardPaymentActivity)

                 // Handle the payment
                 handleCardPayment()

                 // Dismiss the waiting dialog after payment processing
                 Handler(Looper.getMainLooper()).postDelayed({
                     waitDialog.dismiss()

                     // Show a message dialog to inform the user about the successful order
                     showAlert("Thank you for your order.\nPayment has been processed successfully. " +
                             "Proceed to the order page to monitor the status of your order")
                 }, 2000) // Delayed dismissal for demonstration purposes
             }
         }

     }


    private fun handleCardPayment() {
        // Perform card payment logic here

        // Assuming the card payment is successful, create the order and add to the database
        retrieveCartItemsFromDatabase { cartItems ->
            // Call the createOrderAndDetails method from OrderHandler
            OrderHandler.createOrderAndDetails(cartItems) { orderId ->
                // Handle any additional logic after creating the order, e.g., showing a success message
                showToast("Payment successful. Order ID: $orderId")

                // Navigate to the order page or perform any other necessary actions
            }
        }
    }




    //validate the card detail. For now, we will focus on the datatypes
    private fun isValidCardDetails(): Boolean {
        val cardNumber = cardNumberEditText.text.toString()
        val cvv = cvvEditText.text.toString()
        val expiry = expiryEditText.text.toString()

        // Check if any of the fields is empty
        if (cardNumber.isEmpty() || cvv.isEmpty() || expiry.isEmpty()) {
            showAlert("Please fill in all card details.")
            return false
        }

        // Check if the card number is valid
        if (cardNumber.length != 16) {
            showAlert("Invalid card number. It must be 16 digits.")
            return false
        } else if (cvv.length > 3) {
            showAlert("Invalid CVV")
            return false
        } else if (isValidExpiryDate(expiry)) {
            showAlert("Invalid expiry date.")
            return false
        }

        // If all validations pass
        return true
    }

    //check if the expiry date is not in the past
    private fun isValidExpiryDate(expiryDate: String): Boolean {
        try {
            // Parse the expiry date in MM/yyyy format
            val dateFormat = SimpleDateFormat("MM/yyyy", Locale.US)
            val expiryDateObj = dateFormat.parse(expiryDate)

            // Get the current date
            val currentDate = Calendar.getInstance().time

            // Check if the expiry date is in the future
            return expiryDateObj != null && expiryDateObj.after(currentDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false
    }
    private fun showAlert(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    //with lambda
    private fun showAlert(message: String, onOkClicked: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { _, _ ->
            onOkClicked() // Execute the provided lambda when "OK" is clicked
        }
        builder.show()
    }
}
