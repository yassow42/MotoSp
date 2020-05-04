package com.creativeoffice.motosp.Activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.creativeoffice.motosp.ProfileEditFragment
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.SignOutFragment
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.dialog_sifre_degistir.*
import kotlinx.android.synthetic.main.dialog_sifre_degistir.view.*
import kotlinx.android.synthetic.main.dialog_sifre_degistir.view.etYeniSifre
import java.util.zip.Inflater

class ProfileSettingActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 3
    private val TAG = "ProfileSettingActivity"
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        setupNavigationView()
        setupToolbar()
        fragmentNavigation()
    }


    private fun fragmentNavigation() {


        tvCikisYap.setOnClickListener {
            //Frame layout içinde fragment cagırma yapıyoruz. transaction
            profileSettingRoot.visibility = View.GONE

            var dialog = SignOutFragment()
            dialog.show(supportFragmentManager, "cikisDiyalogGoster")

        }


        tvSifreniDegistirHesapAyarlari.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            var view = LayoutInflater.from(this).inflate(R.layout.dialog_sifre_degistir, null)
            builder.setView(view)
            var dialog = builder.create()
            view.tvIptal.setOnClickListener {
                dialog.dismiss()
            }
            view.tvGonder.setOnClickListener {

                FirebaseDatabase.getInstance().reference.child("users").child(mUser.uid).child("password").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (view.etEskiSifre.text.toString().equals(p0.value.toString())) {
                            if (view.etYeniSifre.text.length > 5 &&view.etYeniSifre2.text.length > 5 ){
                                if (view.etYeniSifre.text.toString().equals(view.etYeniSifre2.text.toString())) {

                                    mUser.updatePassword(view.etYeniSifre.text.toString()).addOnCompleteListener{
                                        //   Log.e("sad",it.toString())
                                        FirebaseDatabase.getInstance().reference.child("users").child(mUser.uid).child("password").setValue(view.etYeniSifre.text.toString())
                                        dialog.dismiss()
                                    }.addOnFailureListener {
                                        Log.e("sad1",it.toString())
                                    }


                                } else {
                                    Toast.makeText(this@ProfileSettingActivity, "Yeni Şifreler Uyuşmuyor", Toast.LENGTH_SHORT).show()
                                }


                            }else {
                                Toast.makeText(this@ProfileSettingActivity, "Şifre Çok Kısa", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this@ProfileSettingActivity, "Eski Şifre Yanlış", Toast.LENGTH_SHORT).show()
                        }
                    }

                })

            }


            dialog.show()


        }

    }

    override fun onBackPressed() {

        profileSettingRoot.visibility = View.VISIBLE

        super.onBackPressed()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationContainer)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationContainer) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationContainer.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
