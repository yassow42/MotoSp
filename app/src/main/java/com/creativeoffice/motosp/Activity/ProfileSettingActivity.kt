package com.creativeoffice.motosp.Activity

import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.creativeoffice.motosp.Datalar.Users
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.SignOutFragment
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.creativeoffice.motosp.utils.EventBusDataEvents
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_profile_setting.*
import kotlinx.android.synthetic.main.dialog_sifre_degistir.view.*
import kotlinx.android.synthetic.main.dialog_username_degistir.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProfileSettingActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 3
    private val TAG = "ProfileSettingActivity"
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var userID: String
    lateinit var userData: Users
    var ref = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //  this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        userID = mAuth.currentUser!!.uid

        setupNavigationView()
        setupToolbar()
        fragmentNavigation()
    }


    private fun fragmentNavigation() {


        tvCikisYap.setOnClickListener {
            //Frame layout içinde fragment cagırma yapıyoruz. transaction
            profileSettingRoot.visibility = View.GONE

            var dialog = SignOutFragment()
            dialog.show(supportFragmentManager, "cikisDiyalogGoster")

        }

        tvUserNameDegistir.setOnClickListener {
            var userNameKullanimda = false

            var view = LayoutInflater.from(this).inflate(R.layout.dialog_username_degistir, null)
            val builder = AlertDialog.Builder(this).setView(view)
            view.etKullaniciAdi.setText(userData.user_name)


            builder.setNegativeButton("İptal", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })

            builder.setPositiveButton("Değiştir", null)
            var dialog = builder.create()

            dialog.setOnShowListener(OnShowListener {
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.setOnClickListener(View.OnClickListener { ing ->
                    var kullaniciAdi = view.etKullaniciAdi.text.toString()
                    if (kullaniciAdi.length > 6) {
                        ref.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                for (ds in p0.children) {
                                    val gelenKullanicilar = ds.getValue(Users::class.java)!!
                                    if (gelenKullanicilar.user_name.equals(kullaniciAdi)) {
                                        userNameKullanimda = true
                                        break
                                    } else {
                                        userNameKullanimda = false
                                    }
                                }

                                if (userNameKullanimda) {
                                    view.textInputLayoutKullaniciAdi.isErrorEnabled = true
                                    view.textInputLayoutKullaniciAdi.error = "Kullanıcı Adı Kullanımdadır"
                                } else {
                                    ref.child("users").child(userID).child("user_name").setValue(kullaniciAdi)
                                    dialog.dismiss()
                                }
                            }
                        })
                    } else {
                        view.textInputLayoutKullaniciAdi.isErrorEnabled = true
                        view.textInputLayoutKullaniciAdi.error = "Kullanıcı Adı Çok Kısa"
                    }


                })
            })


            dialog.show()
        }

        tvSifreniDegistirHesapAyarlari.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            var view = LayoutInflater.from(this).inflate(R.layout.dialog_sifre_degistir, null)
            builder.setView(view)

            builder.setNegativeButton("İptal", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })

            builder.setPositiveButton("Şifre Güncelle", DialogInterface.OnClickListener { dialog, which ->
                FirebaseDatabase.getInstance().reference.child("users").child(mUser.uid).child("password").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        if (view.etEskiSifre.text.toString().equals(p0.value.toString())) {
                            if (view.etYeniSifre.text.length > 5 && view.etYeniSifre2.text.length > 5) {
                                if (view.etYeniSifre.text.toString().equals(view.etYeniSifre2.text.toString())) {

                                    mUser.updatePassword(view.etYeniSifre.text.toString()).addOnCompleteListener {

                                        FirebaseDatabase.getInstance().reference.child("users").child(mUser.uid).child("password").setValue(view.etYeniSifre.text.toString())
                                        dialog.dismiss()
                                    }.addOnFailureListener {
                                        Toast.makeText(this@ProfileSettingActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
                                    }


                                } else {
                                    Toast.makeText(this@ProfileSettingActivity, "Yeni Şifreler Uyuşmuyor", Toast.LENGTH_SHORT).show()
                                }


                            } else {
                                Toast.makeText(this@ProfileSettingActivity, "Şifre Çok Kısa", Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(this@ProfileSettingActivity, "Eski Şifre Yanlış", Toast.LENGTH_SHORT).show()
                        }
                    }

                })

            })

            var dialog = builder.create()
            dialog.show()


        }

    }


    override fun onBackPressed() {

        profileSettingRoot.visibility = View.VISIBLE

        super.onBackPressed()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationContainer)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationContainer) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationContainer.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


    @Subscribe(sticky = true)
    internal fun onUserName(gelenUserData: EventBusDataEvents.KullaniciData) {
        userData = gelenUserData.userData
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
