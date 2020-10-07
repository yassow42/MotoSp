package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.creativeoffice.motosp.Datalar.EtkinlikData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_etkinlik_detay.*
import kotlinx.android.synthetic.main.item_etkinlikler.tvEtkinlikAdi
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EtkinlikDetayActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etkinlik_detay)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
      //  this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ref.child("Etkinlikler").keepSynced(true)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid


        var etkinlikKey = intent?.getStringExtra("etkinlik_key").toString()
        var etkinlikZamani = intent?.getStringExtra("etkinlik_zamani").toString()




        setupString(etkinlikZamani)
        butonlar(etkinlikKey)
        setupNavigationView()
    }

    private fun setupString(etkinlikZamani: String?) {
        tvEtkinlikAdi.text = intent?.getStringExtra("etkinlik_adi").toString()
        tvEtkinlikDetaylari.text = intent?.getStringExtra("etkinlik_detaylari").toString()

        etkinlikZamani?.let {
            tvEtkinlikSaati.text = formatDateSaat(it.toLong()).toString()
            tvEtkinlikZamani.text = formatDate(it.toLong()).toString()
        }


    }

    private fun butonlar(etkinlikKey: String) {
        var katilanlarList = ArrayList<String>()
        ref.child("Etkinlikler").child(etkinlikKey).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    var etkinlikData = p0.getValue(EtkinlikData::class.java)!!
                    for (ds in p0.child("katilanlar").children) {
                        katilanlarList.add(ds.value.toString())
                        if (ds.value.toString() == userID.toString()) {
                            tvKatıl.text = "Katıldın"
                        } else {
                            tvKatıl.text = "Katıl"
                        }
                    }
                    tvKatilimciSayisi.text = (katilanlarList.size + 1).toString() + " kişi katılacak..."
                    if (etkinlikData.olusturan_key.toString() == userID.toString()) {
                        tvKatıl.visibility = View.GONE
                        // Log.e("sadddd",etkinlikData.olusturan_key)
                    }

                    if (p0.child("katilanlar").hasChildren()) {
                        var katilanlar = ArrayList<String>()
                        for (ds in p0.child("katilanlar").children) {
                            katilanlar.add(ds.value.toString())
                        }

                        ref.child("Etkinlikler").child(etkinlikKey).child("katilanlar_sayisi").setValue(katilanlar.size)


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
            } else if (tvKatıl.text == "Katıldın") {
                tvKatıl.setText("Katıl")
                ref.child("Etkinlikler").child(etkinlikKey).child("katilanlar").child(userID).removeValue()

            }


        }
    }

    fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("EEE, dd MMM", Locale("tr"))
        return sdf.format(date)
    }

    fun formatDateSaat(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("HH:mm", Locale("tr"))
        return sdf.format(date)
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}