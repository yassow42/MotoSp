package com.creativeoffice.motosp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.android.synthetic.main.dialog_register2.*
import kotlinx.android.synthetic.main.dialog_register2.view.*

class RegisterActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    var loading: Dialog? = null
    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_register2)
        mAuth = FirebaseAuth.getInstance()

        etKullaniciAdiLogin.addTextChangedListener(kullaniciAdiKontrol)

        btnRegister.setOnClickListener {
            var kullaniciAdi = etKullaniciAdiLogin.text.toString().trim()
            var eMail = etEposta.text.toString().trim()
            var kullaniciSifre = etSifre.text.toString().trim()
            var kullaniciSifreTekrar = etSifreTekrar.text.toString().trim()
            var userNameKullanimi = false
            var emailKullanimi = false


            if (kullaniciAdi.isNotEmpty() && eMail.isNotEmpty()) {
                textInputLayoutKullaniciAdi.isErrorEnabled = false
                textInputLayoutEmail.isErrorEnabled = false


                if (kullaniciSifre.isNotEmpty() && kullaniciSifreTekrar.isNotEmpty() && kullaniciSifre == kullaniciSifreTekrar) {
                    textInputLayoutSifre.isErrorEnabled = false
                    textInputLayoutSifreTekrar.isErrorEnabled = false

                    ref.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (ds in p0.children) {
                                val gelenKullanicilar = ds.getValue(Users::class.java)!!
                                if (gelenKullanicilar.user_name.equals(kullaniciAdi)) {
                                    userNameKullanimi = true
                                    textInputLayoutKullaniciAdi.isErrorEnabled = true
                                    textInputLayoutKullaniciAdi.setError("Kullanıcı Adı Kullanımdadır.")
                                    break
                                } else {
                                    textInputLayoutKullaniciAdi.isErrorEnabled = false
                                    userNameKullanimi = false
                                }

                                if (gelenKullanicilar.email.equals(eMail)) {
                                    emailKullanimi = true
                                    textInputLayoutEmail.isErrorEnabled = true
                                    textInputLayoutEmail.setError("E - Posta Kullanımdadır.")
                                    break
                                } else {
                                    textInputLayoutEmail.isErrorEnabled = false
                                    emailKullanimi = false
                                }

                            }


                            ///Kullanıcı adı ve Email kullanılmıyorsa kayıt ıslemı yapılır.
                            if (userNameKullanimi == false && emailKullanimi == false) {
                                textInputLayoutEmail.isErrorEnabled = false
                                textInputLayoutKullaniciAdi.isErrorEnabled = false
                                dialogCalistir()
                                mAuth.createUserWithEmailAndPassword(eMail, kullaniciSifre).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                    override fun onComplete(p0: Task<AuthResult>) {
                                        if (p0.isSuccessful) {
                                            var userID = mAuth.currentUser!!.uid.toString()
                                            var user_detail = UserDetails(1, "Default", "Honda", "Activa S", "default", "yok", "yok", 1)
                                            var kaydedilecekUsers = Users(eMail, kullaniciSifre, kullaniciAdi, userID, "Yeni", user_detail)
                                            FirebaseDatabase.getInstance().reference.child("users").child(userID).setValue(kaydedilecekUsers).addOnCompleteListener {
                                                dialogGizle()
                                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                                                finish()
                                            }

                                        } else {
                                            mAuth.currentUser!!.delete().addOnCompleteListener(object : OnCompleteListener<Void> {
                                                override fun onComplete(p0: Task<Void>) {
                                                    if (p0.isSuccessful) {
                                                        Toast.makeText(this@RegisterActivity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            })
                                        }
                                    }
                                })
                            }
                        }
                    })
                } else {
                    textInputLayoutSifre.isErrorEnabled = true
                    textInputLayoutSifreTekrar.isErrorEnabled = true
                    textInputLayoutSifre.setError("Şifreler Uyuşmuyor")
                    textInputLayoutSifreTekrar.setError("Şifreler Uyuşmuyor")
                }
            } else {
                textInputLayoutKullaniciAdi.isErrorEnabled = true
                textInputLayoutKullaniciAdi.setError("Kullanıcı Adı veya E-Posta Boş")
                textInputLayoutEmail.isErrorEnabled = true
                textInputLayoutEmail.setError("Kullanıcı Adı veya E-Posta Boş")


            }


        }
        tvGirisYap.setOnClickListener {
            dialogCalistir()
            Handler().postDelayed({
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
                finish()
                dialogGizle()
            }, 1200)
        }


    }

    var kullaniciAdiKontrol = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length < 7) {
                textInputLayoutKullaniciAdi.isErrorEnabled = true
                textInputLayoutKullaniciAdi.error = "Kullanıcı Adı çok kısa"
            } else textInputLayoutKullaniciAdi.isErrorEnabled = false

        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        finish()
    }


    private fun dialogGizle() {
        loading?.let { if (it.isShowing) it.cancel() }

    }

    private fun dialogCalistir() {
        dialogGizle()
        loading = LoadingDialog.startDialog(this)
    }

    override fun onStop() {
        dialogGizle()
        super.onStop()
    }


}
