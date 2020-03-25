package com.creativeoffice.motosp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.CevaplarAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_konu_detay.*
import kotlinx.android.synthetic.main.dialog_cevap_yaz.view.*

class KonuDetayActivity : AppCompatActivity() {
    lateinit var cevapList: ArrayList<ForumKonuData.cevaplar>
    lateinit var mAuth: FirebaseAuth
    var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konu_detay)

        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        cevapList = ArrayList()

        var konuBasligi = intent.getStringExtra("konuBasligi")
        var userName = intent.getStringExtra("userName")
        var tarih = intent.getStringExtra("tarih")
        var konuKey = intent.getStringExtra("konuKey")

        tvKonuBasligi.text = konuBasligi.toString()
        tvZaman.text = tarih
        tvUserName.text = userName

        initBtn(konuKey)
        initVeri(konuKey)
    }

    private fun initBtn(konuKey: String?) {
        imgCevapEkle.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_cevap_yaz, null)

            builder.setView(view)
            builder.setTitle("Yorumun...")
            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Gönder", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var ref = FirebaseDatabase.getInstance().reference
                    var konuyaVerilenCevap = view.etCevap.text.toString()


                    //  var cevapKey = ref.child("Forum").child("cevaplar").push().key
                    ref.child("users").child(userID.toString()).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {


                            var cevapkey = ref.child("Forum").child(konuKey.toString()).child("cevaplar").push().key
                            var cevapYazan = p0.value.toString()
                            //ilk olarak cevap vereni son cevap olarak kaydediyruz.
                            var soncevapData = ForumKonuData.son_cevap(konuyaVerilenCevap, cevapkey, cevapYazan, null, userID,konuKey.toString())
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap").setValue(soncevapData)
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap").child("cevap_zamani").setValue(ServerValue.TIMESTAMP)

                            //son cevap zamanı ekliyoruz
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)

                            //cevaba da eklıyruz

                            var cevapData = ForumKonuData.son_cevap(konuyaVerilenCevap, cevapkey, cevapYazan, null, userID,konuKey.toString())
                            ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).setValue(cevapData)
                            ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).child("cevap_zamani").setValue(ServerValue.TIMESTAMP)
                                .addOnCompleteListener {

                                    initVeri(konuKey)
                                }


                            /*
                                   Toast.makeText(this@KonuDetayActivity, "Bir Hata oldu. Verilere erişilemiyor.",Toast.LENGTH_LONG).show()
                                   Log.e("Error","Cevap Eklemede Sorun oluştu. KonuDetayActivity")
                            */


                        }
                    })
                }
            })
            var dialog: Dialog = builder.create()
            dialog.show()


        }
    }


    private fun initVeri(konuKey: String?) {
        cevapList.clear()


        FirebaseDatabase.getInstance().reference.child("Forum").child(konuKey.toString()).child("cevaplar")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (i in p0.children) {
                            var gelenKonu = i.getValue(ForumKonuData.cevaplar::class.java)
                            cevapList.add(gelenKonu!!)
                        }
                        cevapList.sortBy { it.cevap_zamani }

                        setupRecyclerViewCevap()
                    }

                }


            })
    }

    private fun setupRecyclerViewCevap() {
        rcCevaplar.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //   rcBayi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val ForumKonuAdapter = CevaplarAdapter(this, cevapList,userID)

        rcCevaplar.setHasFixedSize(true)
        rcCevaplar.adapter = ForumKonuAdapter
        rcCevaplar.setItemViewCacheSize(20)


    }
}
