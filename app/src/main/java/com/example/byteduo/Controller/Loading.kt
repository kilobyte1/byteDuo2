package com.example.byteduo.Controller

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.example.byteduo.R

class Loading {
    companion object {
        fun showWaitDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_wait)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            return dialog
        }

    }

}
