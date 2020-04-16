package com.creativeoffice.motosp.Activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.ProfileEditFragment
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.creativeoffice.motosp.utils.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 3
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()


        setupAuthListener()
        setupNavigationView()
        kullaniciVerileriniGetir()
        setupToolbar()

        imgProfileSetting.setOnClickListener {
            startActivity(Intent(this, ProfileSettingActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }

    }


    private fun kullaniciVerileriniGetir() {
        var userID = mAuth.currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    // var gelenKullanici = p0.getValue(Users::class.java)!!
                    tvKullaniciAdi.text = p0.child("user_name").value.toString()
                    var marka = p0.child("user_details").child("kullanilan_motor_marka").value.toString()
                    var model = p0.child("user_details").child("kullanilan_motor_model").value.toString()
                    var puan = p0.child("user_details").child("puan").value.toString().toInt()
                    var unvan = p0.child("user_unvan").value.toString()

                    tvUnvan.text = unvan
                    tvPuan.text = puan.toString()
                    tvMarkaProfile.text = marka
                    tvModelProfile.text = model


                    when(puan){

                        in 480..1000 ->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Başkan")
                        }
                        in 240..479 ->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Hızlı")
                        }
                        in 120..239->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Hızlanan")
                        }
                        in 60..119->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Rölantı")
                        }
                        in 30..59->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Acemi")
                        }
                        in 15..29->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Yeni")
                        }
                        in 0..14->{
                            FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_unvan").setValue("Yeni")
                        }
                    }



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
                                var yakitTuk = gelenMotorDetaylari.yakitTuk.toString()

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

    private fun setupToolbar() {

        tvProfilDuzenleButton.setOnClickListener {
            profileRoot.visibility = View.GONE
            profileContainer.visibility = View.VISIBLE
            bottomNavigationContainer.visibility = View.GONE
            val transaction = supportFragmentManager.beginTransaction()

            transaction.replace(R.id.profileContainer, ProfileEditFragment())
            transaction.addToBackStack("profil eklendi")
            transaction.commit()


        }
    }

    override fun onBackPressed() {

        profileRoot.visibility = View.VISIBLE
        bottomNavigationContainer.visibility = View.VISIBLE

        super.onBackPressed()
    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationContainer)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationContainer) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationContainer.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    var intent = Intent(this@ProfileActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                } else {

                }


            }

        }
    }

    override fun onStart() {
        mAuth.addAuthStateListener(mAuthListener)
        super.onStart()
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
