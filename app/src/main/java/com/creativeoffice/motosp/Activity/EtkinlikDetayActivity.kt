package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_etkinlik_detay.*
import kotlinx.android.synthetic.main.item_etkinlikler.tvEtkinlikAdi

class EtkinlikDetayActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etkinlik_detay)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        tvEtkinlikAdi.text = intent.getStringExtra("etkinlik_adi").toString()
        var etkinlikKey = intent.getStringExtra("etkinlik_key").toString()


        setupNavigationView()
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        butonlar(etkinlikKey)
    }

    private fun butonlar(etkinlikKey: String) {
        var katilanlarList = ArrayList<String>()
        ref.child("Etkinlikler").child(etkinlikKey).child("katilanlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        katilanlarList.add(ds.value.toString())
                        if (ds.value.toString() == userID.toString()) {
                            tvKatıl.text = "Katıldın"
                        }else{
                            tvKatıl.text = "Katıl"
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        tvKatıl.setOnClickListener {
            if (tvKatıl.text == "Katıl") {
                ref.child("Etkinlikler").child(etkinlikKey).child("katilanlar").child(userID).setValue(userID)
                tvKatıl.setText("Katıldın")
            }else if(tvKatıl.text == "Katıldın") {
                tvKatıl.setText("Katıl")
                ref.child("Etkinlikler").child(etkinlikKey).child("katilanlar").child(userID).removeValue()

            }


        }
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}