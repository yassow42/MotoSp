package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ForumKonuBasliklariAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_tumkonular.*
import kotlinx.android.synthetic.main.activity_tumkonular.imgPlus
import kotlinx.android.synthetic.main.dialog_konu_ac.view.*


class TumKonularActivity : AppCompatActivity() {

    lateinit var view: View
    var konularList = ArrayList<ForumKonuData>()
    var kategori = "Tum Konular"
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String

    var ref = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tumkonular)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        //    this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //   this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        kategori = intent.getStringExtra("kategori")
        tvBaslik.text = kategori
        init()
        btn()
    }

    private fun btn() {
        imgPlus.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            view = inflater.inflate(R.layout.dialog_konu_ac, null)
            view.etKonuBasligi.addTextChangedListener(watcherForumKonu)
            view.etKonuCevap.addTextChangedListener(watcherForumCevap)


            val lessonsList: MutableList<String> = mutableListOf("Genel", "Tanışma", "Sohbet", "İl Grupları", "Kamp", "Kazalar", "Konu Dışı")

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lessonsList)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            var secilenKategori = ""
            if (kategori == "Tüm Konular") {
                view.spnKategoriler.adapter = adapter
                secilenKategori = "Genel"
                view.spnKategoriler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                        secilenKategori = p0?.getItemAtPosition(p2).toString()

                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO()
                    }
                }
            } else {
                view.spnKategoriler.visibility = View.GONE
                secilenKategori = kategori
            }


            builder.setView(view)
            var dialog: Dialog = builder.create()

            view.tvIptal.setOnClickListener {
                dialog.dismiss()
            }

            view.tvGonder.setOnClickListener {



                var konuBasligi = view.etKonuBasligi.text.toString()
                var konuCevap = view.etKonuCevap.text.toString()
                var konuKey = ref.child("Forum").push().key

                mAuth.currentUser!!.uid?.let {
                    ref.child("users").child(mAuth.currentUser!!.uid).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var konuyuAcan = p0.value.toString()

                            if (konuBasligi.length >= 5 && konuCevap.length >= 5) {
                                var konuData = ForumKonuData(secilenKategori, true, null, null, konuBasligi, konuCevap, konuKey, konuyuAcan, userID)

                                ref.child("Forum").child(konuKey.toString()).setValue(konuData)
                                //son cevap ekliyoruzkı sıralayabılelım.
                                ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)
                                ref.child("Forum").child(konuKey.toString()).child("acilma_zamani").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {

                                    val intent = Intent(this@TumKonularActivity, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    intent.putExtra("konuBasligi", konuData.konu_basligi.toString())
                                    intent.putExtra("konuCevabi", konuData.konu_sahibi_cevap.toString())

                                    intent.putExtra("userName", konuData.konuyu_acan.toString())
                                    intent.putExtra("konuKey", konuData.konu_key)
                                    intent.putExtra("konuyu_acan_key", konuData.konuyu_acan_key)
                                    startActivity(intent)
                                }

                                //ilk olarak konuyu acanı son cevaba kaydedıyoruz...
                                var soncevapData = ForumKonuData.son_cevap(konuCevap, konuKey, konuyuAcan, null, userID, konuKey.toString())
                                ref.child("Forum").child(konuKey.toString()).child("son_cevap").setValue(soncevapData)
                                ref.child("Forum").child(konuKey.toString()).child("son_cevap").child("cevap_zamani").setValue(ServerValue.TIMESTAMP)

                                //cevaba da eklıyruz kı ılk gorunsun
                                var cevapkey = ref.child("Forum").child(konuKey.toString()).child("cevaplar").push().key
                                var cevapData = ForumKonuData.son_cevap(konuCevap, cevapkey, konuyuAcan, null, userID, konuKey.toString())
                                ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).setValue(cevapData)
                                ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).child("cevap_zamani").setValue(ServerValue.TIMESTAMP)
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this@TumKonularActivity, "Başlık veya Cevap çok kısa", Toast.LENGTH_LONG).show()
                            }


                        }
                    })
                }

            }


            dialog.show()
        }
    }

    private fun init() {
        var genelSayisi = 0
        var tanismaSayisi = 0
        var sohbetSayisi = 0
        var ilGruplariSayisi = 0
        var kampSayisi = 0
        var kazalarSayisi = 0
        var konuDisi = 0



        ref.child("Forum").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {


                    var gelenKonu: ForumKonuData
                    for (i in p0.children) {
                        if (i.child("konu_acik_mi").value.toString().toBoolean()) {
                            gelenKonu = i.getValue(ForumKonuData::class.java)!!
                            if (kategori == "Tüm Konular") {
                                konularList.add(gelenKonu)
                            }
                            if (gelenKonu.kategori.toString() == kategori) {
                                konularList.add(gelenKonu)
                            }
                            if (gelenKonu.kategori.toString() == "Genel") genelSayisi++
                            if (gelenKonu.kategori.toString() == "Tanışma") tanismaSayisi++
                            if (gelenKonu.kategori.toString() == "Sohbet") sohbetSayisi++
                            if (gelenKonu.kategori.toString() == "İl Grupları") ilGruplariSayisi++
                            if (gelenKonu.kategori.toString() == "Kamp") kampSayisi++
                            if (gelenKonu.kategori.toString() == "Kazalar") kazalarSayisi++
                            if (gelenKonu.kategori.toString() == "Konu Dışı") konuDisi++

                        }

                        ref.child("Sayisal_Veriler/Forum/Genel").setValue(genelSayisi)
                        ref.child("Sayisal_Veriler/Forum/Tanışma").setValue(tanismaSayisi)
                        ref.child("Sayisal_Veriler/Forum/Sohbet").setValue(sohbetSayisi)
                        ref.child("Sayisal_Veriler/Forum/İl Grupları").setValue(ilGruplariSayisi)
                        ref.child("Sayisal_Veriler/Forum/Kamp").setValue(kampSayisi)
                        ref.child("Sayisal_Veriler/Forum/Kazalar").setValue(kazalarSayisi)
                        ref.child("Sayisal_Veriler/Forum/Konu Dışı").setValue(konuDisi)

                    }
                    konularList.sortByDescending { it.son_cevap_zamani }


                    rcTumKonular.layoutManager = LinearLayoutManager(this@TumKonularActivity, LinearLayoutManager.VERTICAL, false)
                    var tumKonularAdapter = ForumKonuBasliklariAdapter(this@TumKonularActivity, konularList)
                    rcTumKonular.adapter = tumKonularAdapter
                    rcTumKonular.setItemViewCacheSize(20)

                }
            }
        })
    }


    var watcherForumKonu = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 5) {

                view.tvGonder.visibility = View.VISIBLE
                view.tvGonder.isEnabled = true
                view.tvGonder.setBackgroundResource(R.color.yesil)
            } else {
                view.tvGonder.isEnabled = false
                view.tvGonder.setBackgroundResource(R.color.beyaz)

            }
        }

    }
    var watcherForumCevap = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 5) {

                view.tvGonder.visibility = View.VISIBLE
                view.tvGonder.isEnabled = true
                view.tvGonder.setBackgroundResource(R.color.yesil)
            } else {
                view.tvGonder.isEnabled = false
                view.tvGonder.setBackgroundResource(R.color.beyaz)

            }
        }

    }
}
