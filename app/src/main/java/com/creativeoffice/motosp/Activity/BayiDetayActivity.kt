package com.creativeoffice.motosp.Activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.BayiYorumlariAdapter
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_bayi_detay.*
import kotlinx.android.synthetic.main.dialog_yorum.view.*
import kotlin.collections.ArrayList


class BayiDetayActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String


    var ref = FirebaseDatabase.getInstance().reference

    var sehir: String? = null
    var ilce: String? = null
    var bayiAdi: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayi_detay)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid

        bayiAdi = intent.getStringExtra("bayi_adi")
        sehir = intent.getStringExtra("sehir")
        ilce = intent.getStringExtra("ilce")

        ref.child("Bayiler").keepSynced(true)



        butonlar()
        veriler()

    }

    private fun veriler() {


        var yorumlarList = ArrayList<BayilerData.BayiYorumlari>()

        var hizmetPuaniToplam = 0f
        var yildizSayisi = 0
        ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    if( p0.child("yorumlar").hasChildren()){
                        for (ds in p0.child("yorumlar").children) {
                            var gelenData = ds.getValue(BayilerData.BayiYorumlari::class.java)!!
                            yorumlarList.add(gelenData)
                        }
                    }else yorumlarList.add(BayilerData.BayiYorumlari(5f,"İlk Yorumu Yapmak İster misin?",111111111,"sad","Admin"))


                    for (ds in p0.child("yildizlar").children) {
                        hizmetPuaniToplam += ds.value.toString().toFloat()
                        yildizSayisi++
                    }

                    if (hizmetPuaniToplam > 0) {
                        var hizmetPuani = hizmetPuaniToplam / yildizSayisi
                        rbBayi.rating = hizmetPuani
                        tvHizmetPuani.text = hizmetPuani.toDouble().toString()
                        tvYildizKisi.text = "(" + yorumlarList.size + ")"
                    }

                    tvBayiAdi.text = bayiAdi
                    tvNumara.text = p0.child("numara").value.toString()
                    tvAdres.text = p0.child("adres").value.toString()



                    rcBayiYorum.layoutManager = LinearLayoutManager(this@BayiDetayActivity, LinearLayoutManager.VERTICAL, false)
                    val adapter = BayiYorumlariAdapter(this@BayiDetayActivity, yorumlarList)
                    rcBayiYorum.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun butonlar() {

        imgEkle.setOnClickListener {

            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.popup_bayi_detay_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popYorumYap -> {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        var inflater: LayoutInflater = layoutInflater
                        var view: View = inflater.inflate(R.layout.dialog_yorum, null)

                        builder.setView(view)
                        var dialog: Dialog = builder.create()

                        var rbYorumYap = 2.5f
                        view.rbYorumYap.setOnRatingChangeListener { ratingBar, rating, fromUser ->
                            rbYorumYap = ratingBar!!.rating
                        }

                        view.tvIptal.setOnClickListener {
                            dialog!!.dismiss()
                        }

                        var key = ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).push().key.toString()
                        view.tvGonder.setOnClickListener {

                            var yorum = view.etYorum.text.toString()
                            var data = BayilerData.BayiYorumlari(rbYorumYap, yorum, System.currentTimeMillis(), key, userID)

                            ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).child("yorumlar").child(key).setValue(data)
                            ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).child("yildizlar").child(userID).setValue(rbYorumYap)
                            dialog.dismiss()
                        }


                        dialog.show()
                    }


                }
                return@OnMenuItemClickListener true
            })
            popup.show()
        }

        imgGeri.setOnClickListener { onBackPressed() }
    }


}