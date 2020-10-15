package com.creativeoffice.motosp.Fragmentler

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Activity.LoginActivity
import com.creativeoffice.motosp.Adapter.ForumKonuBasliklariAdapter
import com.creativeoffice.motosp.Adapter.YeniAcilanKonuAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.dialog_konu_ac.view.*
import kotlinx.android.synthetic.main.fragment_forum.view.*


class ForumFragment() : Fragment() {

    var ref = FirebaseDatabase.getInstance().reference
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_forum, container, false)
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        hesapKontrol()
        initVeri(view)

        return view
    }

    private fun hesapKontrol() {
        ref.child("Forum").keepSynced(true)
        ref.child("tum_motorlar").keepSynced(true)
        ref.child("Haberler").keepSynced(true)
        ref.child("users").keepSynced(true)
        ref.child("users").child(mAuth.currentUser!!.uid).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value.toString() == "null") {
                    mAuth.signOut()
                    var intent = Intent(context!!.applicationContext, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                   activity!!.finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun initVeri(view: View) {
        var konularList = ArrayList<ForumKonuData>()
        konularList.clear()

        ref.child("Forum").orderByChild("son_cevap_zamani").limitToLast(4).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {

                    var yeniKonuList = ArrayList<ForumKonuData>()
                    var gelenKonu: ForumKonuData
                    for (i in p0.children) {
                        gelenKonu = i.getValue(ForumKonuData::class.java)!!
                        konularList.add(gelenKonu)
                        yeniKonuList.add(gelenKonu)
                    }
                    konularList.sortByDescending { it.son_cevap_zamani }
                    yeniKonuList.sortByDescending { it.acilma_zamani }
                    setupRecyclerViewForumKonu(konularList, view)

                    setupRecyclerViewYeniKonu(yeniKonuList, view)

                }
            }


        })


        view.imgPlus.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(context!!.applicationContext)
            var inflater: LayoutInflater = layoutInflater
            var dialogView = inflater.inflate(R.layout.dialog_konu_ac, null)


            val lessonsList: MutableList<String> = mutableListOf("Genel", "Tanışma", "Sohbet", "İl Grupları", "Kamp", "Kazalar", "Konu Dışı")

            val adapter = ArrayAdapter(context!!.applicationContext, android.R.layout.simple_spinner_item, lessonsList)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            dialogView.spnKategoriler.adapter = adapter
            var kategori = "Genel"
            dialogView.spnKategoriler.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    kategori = p0?.getItemAtPosition(p2).toString()

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

            builder.setView(dialogView)
            var dialog: Dialog = builder.create()

            dialogView.tvIptal.setOnClickListener {
                dialog.dismiss()
            }

            dialogView.tvGonder.setOnClickListener {


                var ref = FirebaseDatabase.getInstance().reference
                var konuBasligi = dialogView.etKonuBasligi.text.toString()
                var konuCevap = dialogView.etKonuCevap.text.toString()
                var konuKey = ref.child("Forum").push().key


                ref.child("users").child(userID).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var konuyuAcan = p0.value.toString()

                        if (konuBasligi.length >= 5 && konuCevap.length >= 5) {
                            var konuData = ForumKonuData(kategori, true,null, null, konuBasligi, konuCevap, konuKey, userID)

                            ref.child("Forum").child(konuKey.toString()).setValue(konuData)
                            //son cevap ekliyoruzkı sıralayabılelım.
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)
                            ref.child("Forum").child(konuKey.toString()).child("acilma_zamani").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {

                                val intent = Intent(context!!.applicationContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra("konuBasligi", konuData.konu_basligi.toString())
                                intent.putExtra("konuCevabi", konuData.konu_sahibi_cevap.toString())


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
                            Toast.makeText(context!!.applicationContext, "Başlık veya Cevap çok kısa", Toast.LENGTH_LONG).show()
                        }


                    }
                })
            }


            dialog.show()
        }
    }

    private fun setupRecyclerViewForumKonu(gonderilenKonuList: ArrayList<ForumKonuData>, view: View) {
        // rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        view.rcForum.layoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        val ForumKonuAdapter = ForumKonuBasliklariAdapter(activity!!.applicationContext, gonderilenKonuList)

        view.rcForum.adapter = ForumKonuAdapter
        view.rcForum.setItemViewCacheSize(20)

    }

    private fun setupRecyclerViewYeniKonu(yeniKonuList: ArrayList<ForumKonuData>, view: View) {
        // rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        view.rcYeniKonular.layoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val yeniKonuAdapter = YeniAcilanKonuAdapter(context!!.applicationContext, yeniKonuList)
        view.rcYeniKonular.adapter = yeniKonuAdapter
        view.rcYeniKonular.setItemViewCacheSize(20)
    }
}