package com.example.byteduo

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.byteduo.Controller.Loading
import com.example.byteduo.Controller.OrderHandler
import com.example.byteduo.Controller.OrderHandler.Companion.retrieveCartItemsFromDatabase
import com.example.byteduo.View.CardPaymentActivity

class PaymentOptionsDialogFragment(private val clearCartCallback: ClearCartCallback) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.payment_options_dialog, container, false)

        val cardRadioButton: RadioButton = view.findViewById(R.id.cardRadioButton)
        val cashRadioButton: RadioButton = view.findViewById(R.id.cashRadioButton)
        val proceedButton: Button = view.findViewById(R.id.btnProceed)

        proceedButton.setOnClickListener {

            if (!isAdded) {
                // Fragment is not attached, return or handle accordingly
                return@setOnClickListener
            }
            val selectedPaymentMethod = when {
                cardRadioButton.isChecked -> "Pay by Card"
                cashRadioButton.isChecked -> "Pay by Cash"
                else -> "No payment method selected"
            }

            // If "Pay by Cash" is selected, show a dialog and create an order in the database
            if (cashRadioButton.isChecked) {
                //create the order and add to the database
                // Call the createOrderInDatabase method from OrderHandler


                //this is not supposed to be here
                showCashPaymentDialog()

                retrieveCartItemsFromDatabase { cartItems ->
                    // Call the createOrderAndDetails method from OrderHandler
                    OrderHandler.createOrderAndDetails(cartItems,"Cash") { orderId ->

                    }
                }

            } else if(cardRadioButton.isChecked) {
                //navigate to card payment activity
                cardPaymentActivity()
            }
            else{
                showToast(selectedPaymentMethod)
            }
            // Dismiss the dialog
            dismiss()
        }
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }







    private fun showCashPaymentDialog() {
        val context = context
        if (context != null && isAdded) {
            //using the second dialog which uses require contect since this is not am activity
            //val waitDialog = Loading.showWaitDialog2(requireContext())


            val dialogMessage =
                "Order placed successfully. You have chosen to pay by cash. " +
                        "Please pay at the counter when picking up your order. Thank you."
            //waitDialog.dismiss()

            AlertDialog.Builder(requireContext())
                .setMessage(dialogMessage)
                .setPositiveButton("OK") { dialog, _ ->
                    clearCartCallback.onCartCleared()
                    dialog.dismiss() }
                .show()
        } else {
            Log.e("PaymentDialog", "Fragment is not added or context is null.")
        }
    }



    private fun cardPaymentActivity() {
        val intent = Intent(requireContext(), CardPaymentActivity::class.java)
        startActivity(intent)
        dismiss()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }




//    private fun showAlert(message: String) {
//        if (isAdded && activity != null) {
//            AlertDialog.Builder(requireActivity())
//                .setMessage(message)
//                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                .show()
//        } else {
//            Log.e("Alert", "Fragment is not added or activity is null.")
//        }
//    }

}
