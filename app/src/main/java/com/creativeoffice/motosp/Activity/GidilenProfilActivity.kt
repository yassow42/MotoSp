package com.creativeoffice.motosp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_gidilen_profil.*

class GidilenProfilActivity : AppCompatActivity() {
    lateinit var gidilenUserID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gidilen_profil)
      //  this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        gidilenUserID  = intent.getStringExtra("gidilenUserID")!!


        kullaniciVerileriniGetir()

    }


    private fun kullaniciVerileriniGetir() {

        FirebaseDatabase.getInstance().reference.child("users").child(gidilenUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    // var gelenKullanici = p0.getValue(Users::class.java)!!
                    tvKullaniciAdi.text = p0.child("user_name").value.toString()
                    var marka = p0.child("user_details").child("kullanilan_motor_marka").value.toString()
                    var model = p0.child("user_details").child("kullanilan_motor_model").value.toString()
                    var puan = p0.child("user_details").child("puan").value.toString()
                    var sonAktif = p0.child("user_details").child("son_aktiflik_zamani").value.toString().toLong()
                    tvPuan.text = puan
                    tvMarkaProfile.text = marka
                    tvModelProfile.text = model
                    tvSonAktif.text = "Son Çevrimiçi: " + TimeAgo.getTimeAgoComments(sonAktif)

                    var imgURL = p0.child("user_details").child("profile_picture").value.toString()
                    if (imgURL != "default") {
                        Picasso.get().load(imgURL).into(circleProfileImage)
                        mProgressBarActivityProfile.visibility = View.GONE
                    } else {
                        mProgressBarActivityProfile.visibility = View.GONE
                    }



                    if (marka != "Marka Seçiniz" && model != "Model Seçiniz") {

                        FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                var gelenMotorDetaylari = p0.getValue(ModelDetaylariData::class.java)!!

                                var agirlik = gelenMotorDetaylari.agirlik.toString()
                                var beygir = gelenMotorDetaylari.beygir.toString()
                                var devir = gelenMotorDetaylari.devir.toString()
                                var hiz = gelenMotorDetaylari.hiz.toString()
                                var kategori = gelenMotorDetaylari.kategori.toString()
                                var silindir = gelenMotorDetaylari.silindirHacmi.toString()
                                var tork = gelenMotorDetaylari.tork.toString()
                                var yakitKap = gelenMotorDetaylari.yakitkap.toString()
                              //  var yakitTuk = gelenMotorDetaylari.yakitTuk.toString()

                                tvMarkaProfile.text = marka
                                tvModelProfile.text = model
                                detay_agirlik.text = agirlik
                                detay_beygir.text = beygir
                                detay_devir.text = devir
                                detay_hiz.text = hiz
                                detay_kategori.text = kategori
                                detay_silindirhacmi.text = silindir
                                detay_tork.text = tork
                                detay_yakitKap.text = yakitKap
                                //  detay_yakitTuk.text = yakitTuk


                            }
                        })
                    }
                }
            }
        })
    }
}
