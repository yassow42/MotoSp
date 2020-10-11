package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.SehirAdapter
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_bayi.*
import kotlinx.android.synthetic.main.dialog_bayi_ekle.view.*
import kotlinx.android.synthetic.main.dialog_konu_ac.view.*

class BayiActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 3
    var sehirList = ArrayList<BayilerData>()
    lateinit var view: View
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayi)
        //   this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //  this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        ref.child("Bayiler").keepSynced(true)
        sehirList = ArrayList()



        butonlar()
        initVeri()
        setupNavigationView()
    }

    private fun butonlar() {
        imgPlus.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            view = inflater.inflate(R.layout.dialog_bayi_ekle, null)
            builder.setPositiveButton("Gönder", DialogInterface.OnClickListener { dialog, which ->

                var sehir = view.etSehir.text.toString()
                var ilce = view.etIlce.text.toString()
                var bayiAdi = view.etBayiAdi.text.toString()
                var adres = view.etAdres.text.toString()
                var tel = view.etTel.text.toString()

                var data = BayilerData.BayiDetaylari(bayiAdi, tel, adres, sehir, ilce, "Veri Saglanamadı")
                var onayKey = ref.child("OnayBekleyen_Bayiler").push().key.toString()
                ref.child("OnayBekleyen_Bayiler").child(onayKey).setValue(data)

                val snackbar = Snackbar.make(textView13,"Bayiniz incelenip onay verildikten sonra eklenecektir. Teşekkürler...",5000)
                snackbar.show()

                dialog.dismiss()
            })

            builder.setNegativeButton("İptal", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()

            })

            builder.setView(view)
            var dialog: Dialog = builder.create()






            dialog.show()
        }
    }

    private fun initVeri() {

        ref.child("Bayiler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                for (sehir in p0.children) {
                    var gelendata = BayilerData(sehir.key.toString())
                    sehirList.add(gelendata)
                }
                setupRecyclerViewSehir()

            }


        })
    }

    private fun setupRecyclerViewSehir() {
        rcBayi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        val SehirAdapter = SehirAdapter(this, sehirList)
        rcBayi.adapter = SehirAdapter
        //  rcBayi.setItemViewCacheSize(50)
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)
        finish()
        super.onBackPressed()
    }
}
