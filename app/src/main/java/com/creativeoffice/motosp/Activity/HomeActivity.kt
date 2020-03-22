package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.ForumKonuBasliklariAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_main.bottomNavigationView
import kotlinx.android.synthetic.main.dialog_konu_ac.*
import kotlinx.android.synthetic.main.dialog_konu_ac.view.*

class HomeActivity : AppCompatActivity() {

    lateinit var konuList: ArrayList<ForumKonuData>
    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    private val ACTIVITY_NO = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        var userID = mAuth.currentUser!!.uid
        konuList = ArrayList()

        initVeri()
        initBtn(userID)
        setupNavigationView()
    }

    private fun initBtn(userID: String) {

        imgPlus.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_konu_ac, null)

            builder.setView(view)
            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Gönder", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var ref = FirebaseDatabase.getInstance().reference
                    var konuBasligi = view.etKonuBasligi.text.toString()
                    var konuCevap = view.etKonuCevap.text.toString()
                    var konuKey = ref.child("Forum").push().key
                    ref.child("users").child(userID).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var konuyuAcan = p0.value.toString()

                            var konuData = ForumKonuData(null,null, konuBasligi, konuCevap, konuKey, konuyuAcan,userID)

                            ref.child("Forum").child(konuKey.toString()).setValue(konuData)

                            //son cevap ekliyoruzkı sıralayabılelım.
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)
                            //konu acılma zaaman verisi
                            ref.child("Forum").child(konuKey.toString()).child("acilma_zamani").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {
                                setupRecyclerViewForumKonu()
                            }

                            //ilk olarak konuyu acanı son cevaba kaydedıyoruz...
                            var soncevapData = ForumKonuData.son_cevap(konuCevap,konuKey,konuyuAcan,null,userID)
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap").setValue(soncevapData)
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap").child("cevap_zamani").setValue(ServerValue.TIMESTAMP)

                            //cevaba da eklıyruz kı ılk gorunsun
                            var cevapkey =  ref.child("Forum").child(konuKey.toString()).child("cevaplar").push().key
                            var cevapData = ForumKonuData.son_cevap(konuCevap,cevapkey,konuyuAcan,null,userID)
                            ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).setValue(cevapData)
                            ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).child("cevap_zamani").setValue(ServerValue.TIMESTAMP)



                        }
                    })
                }
            })
            var dialog: Dialog = builder.create()
            dialog.show()


        }
    }


    private fun initVeri() {
        FirebaseDatabase.getInstance().reference.child("Forum")//.child("konu")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    for (i in p0.children) {
                        var gelenKonu = i.getValue(ForumKonuData::class.java)
                        konuList.add(gelenKonu!!)
                    }

                    konuList.sortBy{ it.son_cevap_zamani }

                    setupRecyclerViewForumKonu()
                }


            })
    }

    private fun setupRecyclerViewForumKonu() {
        rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //   rcBayi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val ForumKonuAdapter = ForumKonuBasliklariAdapter(this, konuList)

        rcForum.adapter = ForumKonuAdapter
        rcForum.setItemViewCacheSize(20)


    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
