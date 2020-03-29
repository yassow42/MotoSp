package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ForumKonuBasliklariAdapter
import com.creativeoffice.motosp.Adapter.HaberAdapter
import com.creativeoffice.motosp.Adapter.SonMotorYorumAdapter
import com.creativeoffice.motosp.Adapter.YeniAcilanKonuAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.YorumlarData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.dialog_haber_ekle.view.*
import kotlinx.android.synthetic.main.dialog_konu_ac.view.*

class HomeActivity : AppCompatActivity() {

    lateinit var konularList: ArrayList<ForumKonuData>
    lateinit var cevapYazilanKonuList: ArrayList<ForumKonuData>
    lateinit var sonYorumlarList: ArrayList<YorumlarData>
    lateinit var tumModeller: ArrayList<ModelDetaylariData>
    lateinit var tumHaberler: ArrayList<HaberlerData>


    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener

    private val ACTIVITY_NO = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mAuth = FirebaseAuth.getInstance()
        val userID = mAuth.currentUser!!.uid
        konularList = ArrayList()
        cevapYazilanKonuList = ArrayList()
        sonYorumlarList = ArrayList()
        tumModeller = ArrayList()
        tumHaberler = ArrayList()

        initVeri()
        initBtn(userID)
        setupNavigationView()
    }

    private fun initBtn(userID: String) {

        imgHaberEkle.setOnClickListener {
            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_haber_ekle, null)

            builder.setView(view)
            var dialog: Dialog = builder.create()

            view.btnGonder.setOnClickListener {
                var ref = FirebaseDatabase.getInstance().reference

                var haberBaslik = view.etBaslik.text.toString()
                var haberIcerik = view.etIcerik.text.toString()
                var haberVideo = view.etVideo.text.toString()

                var haberKey = ref.child("Haberler").push().key

                var haberData = HaberlerData(haberBaslik, haberIcerik, haberVideo, null, haberKey)

                ref.child("Haberler").child(haberKey.toString()).setValue(haberData).addOnCompleteListener {
                    ref.child("Haberler").child(haberKey.toString()).child("haber_eklenme_zamani").setValue(ServerValue.TIMESTAMP)
                    dialog.dismiss()
                }.addOnFailureListener{
                    Toast.makeText(this,"Haber eklenmedi",Toast.LENGTH_LONG).show()
                }
            }


            dialog.show()

        }



        imgPlus.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_konu_ac, null)

            builder.setView(view)
            var dialog: Dialog = builder.create()

            view.tvIptal.setOnClickListener {
                dialog.dismiss()
            }

            view.tvGonder.setOnClickListener {
                var ref = FirebaseDatabase.getInstance().reference
                var konuBasligi = view.etKonuBasligi.text.toString()
                var konuCevap = view.etKonuCevap.text.toString()
                var konuKey = ref.child("Forum").push().key
                ref.child("users").child(userID).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var konuyuAcan = p0.value.toString()

                        var konuData = ForumKonuData(null, null, konuBasligi, konuCevap, konuKey, konuyuAcan, userID)

                        ref.child("Forum").child(konuKey.toString()).setValue(konuData)

                        //son cevap ekliyoruzkı sıralayabılelım.
                        ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)
                        //konu acılma zaaman verisi
                        ref.child("Forum").child(konuKey.toString()).child("acilma_zamani").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {
                            setupRecyclerViewForumKonu(cevapYazilanKonuList)
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

                        setupRecyclerViewForumKonu(cevapYazilanKonuList)
                        dialog.dismiss()

                    }
                })
            }


            dialog.show()
        }

        tvTumKonular.setOnClickListener {

            setupRecyclerViewForumKonu(konularList)
            rcForum.layoutParams.height = MATCH_PARENT


        }
    }


    private fun initVeri() {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Forum").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    var yeniKonuList = ArrayList<ForumKonuData>()
                    yeniKonuList = ArrayList()

                    var gelenKonu: ForumKonuData
                    for (i in p0.children) {
                        gelenKonu = i.getValue(ForumKonuData::class.java)!!
                        konularList.add(gelenKonu)

                    }
                    konularList.sortByDescending { it.son_cevap_zamani }
                    if (konularList.size > 4) {
                        cevapYazilanKonuList.add(konularList[0])
                        cevapYazilanKonuList.add(konularList[1])
                        cevapYazilanKonuList.add(konularList[2])
                        cevapYazilanKonuList.add(konularList[3])
                        cevapYazilanKonuList.add(konularList[4])
                        yeniKonuList.add(konularList[0])
                        yeniKonuList.add(konularList[1])
                        yeniKonuList.add(konularList[2])
                        yeniKonuList.add(konularList[3])
                        yeniKonuList.add(konularList[4])
                    } else if (konularList.size > 3) {
                        cevapYazilanKonuList.add(konularList[0])
                        cevapYazilanKonuList.add(konularList[1])
                        cevapYazilanKonuList.add(konularList[2])
                        cevapYazilanKonuList.add(konularList[3])
                        yeniKonuList.add(konularList[0])
                        yeniKonuList.add(konularList[1])
                        yeniKonuList.add(konularList[2])
                        yeniKonuList.add(konularList[3])
                    } else if (konularList.size > 2) {
                        cevapYazilanKonuList.add(konularList[0])
                        cevapYazilanKonuList.add(konularList[1])
                        cevapYazilanKonuList.add(konularList[2])
                        yeniKonuList.add(konularList[0])
                        yeniKonuList.add(konularList[1])
                        yeniKonuList.add(konularList[2])
                    } else if (konularList.size > 1) {
                        cevapYazilanKonuList.add(konularList[0])
                        cevapYazilanKonuList.add(konularList[1])
                        yeniKonuList.add(konularList[0])
                        yeniKonuList.add(konularList[1])
                    } else if (konularList.size > 0) {
                        cevapYazilanKonuList.add(konularList[0])
                        yeniKonuList.add(konularList[0])
                    }

                    yeniKonuList.sortByDescending { it.acilma_zamani }
                    cevapYazilanKonuList.sortByDescending { it.son_cevap_zamani }

                    setupRecyclerViewForumKonu(cevapYazilanKonuList)
                    setupRecyclerViewYeniKonu(yeniKonuList)
                }
            }
        })

        ref.child("tum_motorlar").child("yorumlar_son").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenVeri = ds.getValue(YorumlarData::class.java)!!
                        sonYorumlarList.add(gelenVeri)

                    }
                    setupRecyclerViewSonYorum()
                }
            }


        })
        ref.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var modeller = ds.getValue(ModelDetaylariData::class.java)!!
                        tumModeller.add(modeller)
                    }
                }
            }
        })

        ref.child("Haberler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var haberler = ds.getValue(HaberlerData::class.java)!!
                        tumHaberler.add(haberler)
                    }
                    tumHaberler.sortByDescending { it.haber_eklenme_zamani }
                    setupRecyclerViewHaberler()
                }

            }
        })

    }

    private fun setupRecyclerViewForumKonu(gonderilenKonuList: ArrayList<ForumKonuData>) {
        // rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rcForum.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val ForumKonuAdapter = ForumKonuBasliklariAdapter(this, gonderilenKonuList)
        ForumKonuAdapter.notifyDataSetChanged()
        rcForum.adapter = ForumKonuAdapter
        rcForum.setItemViewCacheSize(20)

    }

    private fun setupRecyclerViewYeniKonu(yeniKonuList: ArrayList<ForumKonuData>) {
        // rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rcYeniKonular.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val yeniKonuAdapter = YeniAcilanKonuAdapter(this, yeniKonuList)
        rcYeniKonular.adapter = yeniKonuAdapter
        rcYeniKonular.setItemViewCacheSize(20)
    }

    private fun setupRecyclerViewSonYorum() {
        // rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rcSonModelMesajlari.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val yeniKonuAdapter = SonMotorYorumAdapter(this, sonYorumlarList, tumModeller)
        rcSonModelMesajlari.adapter = yeniKonuAdapter
        rcSonModelMesajlari.setItemViewCacheSize(20)
    }

    private fun setupRecyclerViewHaberler() {
        // rcForum.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rcHaber.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val haberlerAdapter = HaberAdapter(this, tumHaberler)
        rcHaber.adapter = haberlerAdapter

        ar_indicator_haber.attachTo(rcHaber, true)
        ar_indicator_haber.isScrubbingEnabled = true


    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


}
