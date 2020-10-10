package com.creativeoffice.motosp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.creativeoffice.motosp.Datalar.UserDetails
import com.creativeoffice.motosp.Datalar.Users
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.LoadingDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login2.*
import kotlinx.android.synthetic.main.dialog_register2.view.*

class LoginActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var loading: Dialog? = null
    var ref = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        setupAuthListener()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)

        //  this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()

        ref.child("users").keepSynced(true)

        btnRegister.setOnClickListener {
            dialogCalistir()
            Handler().postDelayed({
                startActivity(Intent(this, RegisterActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                finish()
                dialogGizle()
            },1200)

/*
            var builder: AlertDialog.Builder = AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_register2, null)

            builder.setView(view)
            var dialog: Dialog = builder.create()

            view.btnRegisterAlertDialog.setOnClickListener {
                var kullaniciAdi = view.etKullaniciAdiLoginAlertDialog.text.toString()
                var kullaniciAdiEmail = kullaniciAdi + "@gmail.com"
                var kullaniciSifre = view.etSifreLoginAlertDialog.text.toString()
                var userNameKullanimi = false


                ref.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for (ds in p0.children) {
                            val gelenKullanicilar = ds.getValue(Users::class.java)!!
                            if (gelenKullanicilar.user_name.equals(kullaniciAdi)) {
                                userNameKullanimi = true
                                break
                            } else {
                                userNameKullanimi = false
                            }
                        }


                        if (userNameKullanimi == true) {
                            Toast.makeText(this@LoginActivity, "Kullanıcı Adı Kullanımdadır.", Toast.LENGTH_LONG).show()
                        } else {
                            mAuth.createUserWithEmailAndPassword(kullaniciAdiEmail, kullaniciSifre).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                override fun onComplete(p0: Task<AuthResult>) {
                                    if (p0.isSuccessful) {
                                        var userID = mAuth.currentUser!!.uid.toString()
                                        var user_detail = UserDetails(
                                            1, "Default", "Honda", "Activa S", "default",
                                            "yok", "yok", 1
                                        )
                                        var kaydedilecekUsers = Users(kullaniciAdiEmail, kullaniciSifre, kullaniciAdi, userID, "Yeni", user_detail)
                                        ref.child("users").child(userID).setValue(kaydedilecekUsers).addOnCompleteListener {
                                            setupAuthListener()
                                        }

                                    } else {
                                        mAuth.currentUser!!.delete().addOnCompleteListener(object : OnCompleteListener<Void> {
                                            override fun onComplete(p0: Task<Void>) {
                                                if (p0.isSuccessful) {
                                                    Toast.makeText(this@LoginActivity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        })
                                    }
                                }
                            })
                        }
                    }
                })
            }



            dialog.show()*/
        }
        btnLogin.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)

            var eMail = etEpostaLogin.text.toString().trim()
            var kullaniciSifre = etSifreLogin.text.toString().trim()
            if (!eMail.isNullOrEmpty() && !kullaniciSifre.isNullOrEmpty()){


                mAuth.signInWithEmailAndPassword(eMail, kullaniciSifre)
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {
                            if (p0.isSuccessful) {
                                setupAuthListener()
                            } else {
                                val snackbar = Snackbar.make(tumLayout,"Kullanıcı Adı/Sifre Hatalı",2500)
                                val snackbarView = snackbar.view
                                val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                                textView.setTextColor(Color.WHITE)
                                textView.textSize = 16f
                                snackbar.show()
                            }
                        }

                    })
            }else{
                val snackbar = Snackbar.make(tumLayout,"Kullanıcı Adı veya Sifre Boş",2500)
                val snackbarView = snackbar.view
                val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                textView.setTextColor(Color.WHITE)
                textView.textSize = 16f
                snackbar.show()
            }

        }

    }

    private fun dialogGizle() {
        loading?.let { if (it.isShowing) it.cancel() }

    }

    private fun dialogCalistir() {
        dialogGizle()
        loading = LoadingDialog.startDialog(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        finish()

    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    var intent = Intent(this@LoginActivity, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
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


    override fun onStop() {
        super.onStop()
        dialogGizle()
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
