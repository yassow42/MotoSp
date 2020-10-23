package com.creativeoffice.motosp.Activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ProfilYorumlarimAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.UserDetails
import com.creativeoffice.motosp.Datalar.Users
import com.creativeoffice.motosp.ProfileEditFragment
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.creativeoffice.motosp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.Exception


class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 4
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userData: Users
    var ref = FirebaseDatabase.getInstance().reference


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


    }


    private fun kullaniciVerileriniGetir() {

        var userID = mAuth.currentUser!!.uid


        ref.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.child("yorumlarim").hasChildren()) {
                    var yorumlarim = ArrayList<ForumKonuData.cevaplar>()
                    for (ds in p0.child("yorumlarim").children) {
                        var gelenData = ds.getValue(ForumKonuData.cevaplar::class.java)!!
                        yorumlarim.add(gelenData)
                    }

                    rcYorumlarim.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, true)
                    //            rcMotorlarim.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, true)
                    //            rcTecrubelerim.layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.VERTICAL, true)

                    val adapter = ProfilYorumlarimAdapter(this@ProfileActivity, yorumlarim, userID)
                    rcYorumlarim.adapter = adapter
                    rcTecrubelerim.adapter = adapter
                    rcMotorlarim.adapter = adapter


                }
            }
        })
    }

    private fun setupToolbar() {
        imgProfileSetting.setOnClickListener {
            startActivity(Intent(this, ProfileSettingActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
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


    override fun onResume() {
        setupNavigationView()
        super.onResume()
    }


    @Subscribe(sticky = true)
    internal fun onUserName(gelenUserData: EventBusDataEvents.KullaniciData) {
        userData = gelenUserData.userData
        var imgURL = "default"

        var usersDetails = userData.user_details!!
        imgURL = usersDetails.profile_picture.toString()

        if (imgURL != "default") {
            Picasso.get().load(imgURL).into(circleProfileImage)
            mProgressBarActivityProfile.visibility = View.GONE
        } else mProgressBarActivityProfile.visibility = View.GONE


        tvKullaniciAdi.text = userData.user_name.toString()
        tvMarkaProfile.text = usersDetails.kullanilan_motor_marka.toString()
        tvModelProfile.text = usersDetails.kullanilan_motor_model.toString()
        tvPuan.text = usersDetails.puan.toString()

        if (usersDetails.biyografi.isNullOrEmpty() || usersDetails.biyografi == "Default") {
            tvBiyografi.visibility = View.GONE
        } else {
            tvBiyografi.visibility = View.VISIBLE
            tvBiyografi.text = usersDetails.biyografi.toString()
        }


        linearLayoutSehirIlce.visibility = View.GONE
        if (usersDetails.sehir.toString() != "yok") {
            tvAdresSehir.text = usersDetails.sehir.toString()
            linearLayoutSehirIlce.visibility = View.VISIBLE
        }
        if (usersDetails.ilce.toString() != "yok") {
            tvAdresIlce.text = usersDetails.ilce.toString()
            linearLayoutSehirIlce.visibility = View.VISIBLE
        }


        //    userData = p0.getValue(Users::class.java)!!
        //   var usersDetails = p0.child("user_details").getValue(UserDetails::class.java)!!

    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


}
