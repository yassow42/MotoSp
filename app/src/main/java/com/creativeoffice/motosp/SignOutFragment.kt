package com.creativeoffice.motosp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.DialogFragment
import com.creativeoffice.motosp.Activity.LoginActivity
import com.creativeoffice.motosp.Activity.ProfileActivity


import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*


/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        var alert = AlertDialog.Builder(activity)
            .setTitle("Instagram'dan Çıkış Yap")
            .setMessage("Emin Misiniz ?")
            .setPositiveButton("Çıkış Yap", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    FirebaseAuth.getInstance().signOut()
                    activity!!.finish()
                    startActivity(Intent(activity!!.applicationContext,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                }

            })
            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    dismiss()
                    startActivity(Intent(context!!.applicationContext,ProfileActivity::class.java))
                }

            })
            .create()

        return alert
    }


}
