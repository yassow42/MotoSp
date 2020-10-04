package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.BayiYorumlariAdapter
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.YorumlarData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_bayi_detay.*
import kotlinx.android.synthetic.main.activity_bayi_detay.floatingGenel
import kotlinx.android.synthetic.main.activity_bayi_detay.floatingYorumYap
import kotlinx.android.synthetic.main.activity_bayi_detay.imgGeri
import kotlinx.android.synthetic.main.activity_bayi_detay.tvYildizKisi
import kotlinx.android.synthetic.main.dialog_konu_ac.view.*
import kotlinx.android.synthetic.main.dialog_parca_ekle.view.*
import kotlinx.android.synthetic.main.dialog_yakit_tuketim.view.*
import kotlinx.android.synthetic.main.dialog_yorum.view.*
import kotlinx.android.synthetic.main.dialog_yorum.view.tvGonder
import kotlinx.android.synthetic.main.dialog_yorum.view.tvIptal


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
        yorumlarList.clear()

        var hizmetPuaniToplam = 0f
        var yildizSayisi = 0
        var gelenData: BayilerData.BayiYorumlari
        ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    if (p0.child("yorumlar").hasChildren()) {
                        for (ds in p0.child("yorumlar").children) {
                            gelenData = ds.getValue(BayilerData.BayiYorumlari::class.java)!!
                            yorumlarList.add(gelenData)
                        }
                    } else yorumlarList.add(BayilerData.BayiYorumlari("dsa", "dsa", "dsads", 5f, "İlk Yorumu Yapmak İster misin?", 111111111, "sad", "Admin"))


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
                    p0.child("numara").value.toString()?.let {
                        tvNumara.text = it
                        tvNumara.setOnClickListener {

                            val arama = Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                            arama.data = Uri.parse("tel:" + p0.child("numara").value.toString())

                            startActivity(arama)
                        }
                    }
                    p0.child("adres").value.toString()?.let {
                        tvAdres.text = it
                        tvAdres.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("google.navigation:q= " + p0.child("adres").value.toString())
                            startActivity(intent)
                        }
                    }



                    rcBayiYorum.layoutManager = LinearLayoutManager(this@BayiDetayActivity, LinearLayoutManager.VERTICAL, false)
                    val adapter = BayiYorumlariAdapter(this@BayiDetayActivity, yorumlarList, userID)
                    rcBayiYorum.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun butonlar() {

        swipeRefreshLayoutBayi.setOnRefreshListener {
            veriler()
            swipeRefreshLayoutBayi.isRefreshing = false
        }

        var isOpen = false
        val fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        val rotateClockWise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        val rotateAntiClockWise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise)

        floatingGenel.setOnClickListener {

            if (isOpen) {
                floatingYorumYap.startAnimation(fabClose)
                floatingGenel.startAnimation(rotateClockWise)
                isOpen = false
            } else {
                floatingYorumYap.startAnimation(fabOpen)
                floatingGenel.startAnimation(rotateAntiClockWise)
                floatingYorumYap.isClickable
                isOpen = true
            }


            floatingYorumYap.setOnClickListener {
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
                    dialog.dismiss()
                }


                view.tvGonder.setOnClickListener {
                    var yorum = view.etYorum.text.toString()
                    if (view.etYorum.length()>1){
                        var key = ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).push().key.toString()
                        var data = BayilerData.BayiYorumlari(bayiAdi, sehir, ilce, rbYorumYap, yorum, System.currentTimeMillis(), key, userID)

                        ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).child("yorumlar").child(key).setValue(data)
                        ref.child("Bayiler").child(sehir.toString()).child(ilce.toString()).child(bayiAdi.toString()).child("yildizlar").child(userID).setValue(rbYorumYap)
                        dialog.dismiss()
                    }

                }
                view.etYorum.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (1< s!!.length && s!!.length < 150) view.tvGonder.isClickable = true
                        else view.tvGonder.isClickable = false


                    }

                    override fun afterTextChanged(s: Editable?) {

                    }

                })


                dialog.show()
            }


        }
/*
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
                            var data = BayilerData.BayiYorumlari(bayiAdi, sehir, ilce, rbYorumYap, yorum, System.currentTimeMillis(), key, userID)

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
*/
        imgGeri.setOnClickListener { onBackPressed() }
    }


}