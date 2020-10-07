package com.creativeoffice.motosp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.creativeoffice.motosp.R


object LoadingDialog {

    fun startDialog(context: Context): Dialog? {
        val progressDialog = Dialog(context)

        progressDialog?.let {
            it.show()
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setContentView(R.layout.proggres_dialog)
            it.setCancelable(true)
            it.setCanceledOnTouchOutside(true)
            return it
        }
    }


}