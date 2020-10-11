package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_splash_screen.*
import kotlin.random.Random

class SplashScreenActivity : AppCompatActivity() {
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000 //1.25 seconds

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String
    var loading: Dialog? = null

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        mAuth = FirebaseAuth.getInstance()
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //   this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
   //     FirebaseDatabase.getInstance().reference.child("Sayisal_Veriler").addListenerForSingleValueEvent(FirebaseDuyuru)
        imgIcon2.setAnimation(AnimationUtils.loadAnimation(this, R.anim.olusma_sol_splash))

        mDelayHandler = Handler()
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)



        //    userID = mAuth.currentUser!!.uid//"0wHcJGHkQefYWzxTMOkXTEaaMgj1"


    }

    fun dialogGizle() {
        loading?.let { if (it.isShowing) it.cancel() }
    }

    fun dialogCalistir() {
        dialogGizle()
        loading = LoadingDialog.startDialog(this)
    }


    var FirebaseDuyuru = object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {

            var tumKonuSayisi = p0.child("Forum").child("Tüm Konular").value.toString()
            var motorSayisi = p0.child("Motor_Sayisi").value.toString()

            var sayi = Random.nextInt(1, 3)
            when (sayi) {

                1 -> {
                    tvDuyuru.text = "Forum Sayımız $tumKonuSayisi yükseldi..."

                }
                2 -> {
                    tvDuyuru.text = "Uygulamamızda " + motorSayisi + "adet Motosiklet Modelinin Detayları Bulunmakta"

                }
                3 -> {
                    tvDuyuru.text = sayi.toString()

                }
            }



        }

        override fun onCancelled(error: DatabaseError) {

        }

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

}