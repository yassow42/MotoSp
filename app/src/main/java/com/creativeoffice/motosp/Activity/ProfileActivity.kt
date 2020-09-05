package com.creativeoffice.motosp.Activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.CevaplarAdapter
import com.creativeoffice.motosp.Adapter.ProfilYorumlarimAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.YorumlarData
import com.creativeoffice.motosp.ProfileEditFragment
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        //  this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //  this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


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

                    tvPuan.text = puan.toString()
                    tvMarkaProfile.text = marka
                    tvModelProfile.text = model

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

                                tvMarkaProfile.text = marka
                                tvModelProfile.text = model


                            }
                        })
                    } else {
                        tvMarkaProfile.text = "Marka"
                        tvModelProfile.text = "Model"
                    }

                    if (p0.child("yorumlarim").hasChildren()) {
                        var yorumlarim = ArrayList<ForumKonuData.cevaplar>()
                        for (ds in p0.child("yorumlarim").children) {
                            var gelenData = ds.getValue(ForumKonuData.cevaplar::class.java)!!
                            yorumlarim.add(gelenData)
                        }

                        rcYorumlarim.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, true)
                        rcMotorlarim.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, true)
                        rcTecrubelerim.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, true)

                        val adapter = ProfilYorumlarimAdapter(this@ProfileActivity, yorumlarim, userID)
                        rcYorumlarim.adapter = adapter
                        rcTecrubelerim.adapter = adapter
                        rcMotorlarim.adapter = adapter


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
