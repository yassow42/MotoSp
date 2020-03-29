package com.creativeoffice.motosp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.creativeoffice.motosp.Datalar.UserDetails
import com.creativeoffice.motosp.Datalar.Users
import com.creativeoffice.motosp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        setupAuthListener()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        mAuth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            var kullaniciAdi = etKullaniciAdiLogin.text.toString()
            var kullaniciAdiEmail = kullaniciAdi + "@gmail.com"
            var kullaniciSifre = etSifreLogin.text.toString()
            var userNameKullanimi = true
            FirebaseDatabase.getInstance().reference.child("users").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    for (ds in p0.children){
                        var gelenKullanicilar = ds.getValue(Users::class.java)
                        if (gelenKullanicilar!!.user_name.equals(kullaniciAdi)){
                            userNameKullanimi = true
                            break
                        }else{
                            userNameKullanimi = false
                        }
                    }

                    if (userNameKullanimi==true){
                        Toast.makeText(this@LoginActivity,"Kullanıcı Adı Kullanımdadır.", Toast.LENGTH_LONG).show()
                    }else{
                        mAuth.createUserWithEmailAndPassword(kullaniciAdiEmail, kullaniciSifre).addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                            override fun onComplete(p0: Task<AuthResult>) {
                                if (p0!!.isSuccessful) {

                                    var userID = mAuth.currentUser!!.uid.toString()
                                    var user_detail = UserDetails(1,"","Honda","Activa S","default")
                                    var kaydedilecekUsers = Users(kullaniciAdiEmail, kullaniciSifre, kullaniciAdi, userID,user_detail)
                                    FirebaseDatabase.getInstance().reference.child("users").child(userID).setValue(kaydedilecekUsers)

                                } else {
                                    mAuth.currentUser!!.delete() .addOnCompleteListener(object : OnCompleteListener<Void> {
                                        override fun onComplete(p0: Task<Void>) {
                                            if (p0!!.isSuccessful) {
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


        btnLogin.setOnClickListener {
            var kullaniciAdi = etKullaniciAdiLogin.text.toString()
            var kullaniciAdiEmail = kullaniciAdi + "@gmail.com"
            var kullaniciSifre = etSifreLogin.text.toString()
            mAuth.signInWithEmailAndPassword(kullaniciAdiEmail, kullaniciSifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                    override fun onComplete(p0: Task<AuthResult>) {
                        if (p0!!.isSuccessful) {

                            setupAuthListener()

                        } else {
                            Toast.makeText(this@LoginActivity, " Kullanıcı Adı/Sifre Hatalı :", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
        }

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
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
