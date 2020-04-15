package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.android.synthetic.main.dialog_konu_ac.view.tvGonder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity() {

    lateinit var konularList: ArrayList<ForumKonuData>
    lateinit var cevapYazilanKonuList: ArrayList<ForumKonuData>
    lateinit var sonYorumlarList: ArrayList<YorumlarData>
    lateinit var tumModeller: ArrayList<ModelDetaylariData>
    lateinit var tumHaberler: ArrayList<HaberlerData>

    lateinit var view: View

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var userID: String

    private val ACTIVITY_NO = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (FirebaseDatabase.getInstance().reference == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        }

        FirebaseDatabase.getInstance().reference.keepSynced(true)


        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
        konularList = ArrayList()
        cevapYazilanKonuList = ArrayList()
        sonYorumlarList = ArrayList()
        tumModeller = ArrayList()
        tumHaberler = ArrayList()

        initVeri(userID)
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
                var haberAltBaslik = view.etAltBaslik.text.toString()
                var haberIcerik = view.etIcerik.text.toString()
                var haberVideo = view.etVideo.text.toString()
                var haberVideolumu = view.etHaberVideolumu.text.toString().toBoolean()
                var haberKey = ref.child("Haberler").push().key

                var haberData = HaberlerData(haberBaslik, haberIcerik, haberVideo, null, haberKey, haberAltBaslik, haberVideolumu)

                ref.child("Haberler").child(haberKey.toString()).setValue(haberData).addOnCompleteListener {
                    ref.child("Haberler").child(haberKey.toString()).child("haber_eklenme_zamani").setValue(ServerValue.TIMESTAMP)
                    dialog.dismiss()
                }.addOnFailureListener {

                }
            }


            dialog.show()

        }



        imgPlus.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            view = inflater.inflate(R.layout.dialog_konu_ac, null)
            view.etKonuBasligi.addTextChangedListener(watcherForumKonu)
            view.etKonuCevap.addTextChangedListener(watcherForumCevap)
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

                        if (konuBasligi.length >= 5 && konuCevap.length>=5){
                            var konuData = ForumKonuData(null, null, konuBasligi, konuCevap, konuKey, konuyuAcan, userID)

                            ref.child("Forum").child(konuKey.toString()).setValue(konuData)

                            //son cevap ekliyoruzkı sıralayabılelım.
                            ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)
                            ref.child("Forum").child(konuKey.toString()).child("acilma_zamani").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {

                                val intent = Intent(this@HomeActivity, KonuDetayActivity::class.java)//.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                intent.putExtra("konuBasligi", konuData.konu_basligi.toString())
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
                        }else{
                            Toast.makeText(this@HomeActivity,"Başlık veya Cevap çok kısa",Toast.LENGTH_LONG).show()
                        }




                    }
                })
            }


            dialog.show()
        }

        tvTumKonular.setOnClickListener {

            /*
            setupRecyclerViewForumKonu(konularList)
            rcForum.layoutParams.height = MATCH_PARENT
            */
            val intent = Intent(this, TumKonularActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            startActivity(intent)


        }
    }



    private fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat(" d MMM hh:mm ", Locale("tr"))
        return sdf.format(date)

    }

    private fun initVeri(userID: String) {
        val ref = FirebaseDatabase.getInstance().reference
        //son aktiviteyi kaydetme

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
                        //    cevapYazilanKonuList.add(konularList[4])//son konu baslıklarının sayısını 5 ten 4 e dusurdum
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
                    var sonYorumlarTumList = ArrayList<YorumlarData>()
                    sonYorumlarTumList = ArrayList()
                    for (ds in p0.children) {
                        var gelenVeri = ds.getValue(YorumlarData::class.java)!!
                        sonYorumlarTumList.add(gelenVeri)
                    }

                    sonYorumlarTumList.sortByDescending { it.yorum_zaman }
                    if (sonYorumlarTumList.size > 4) {
                        sonYorumlarList.add(sonYorumlarTumList[0])
                        sonYorumlarList.add(sonYorumlarTumList[1])
                        sonYorumlarList.add(sonYorumlarTumList[2])
                        sonYorumlarList.add(sonYorumlarTumList[3])
                    } else if (sonYorumlarTumList.size > 3) {
                        sonYorumlarList.add(sonYorumlarTumList[0])
                        sonYorumlarList.add(sonYorumlarTumList[1])
                        sonYorumlarList.add(sonYorumlarTumList[2])
                    } else if (sonYorumlarTumList.size > 2) {
                        sonYorumlarList.add(sonYorumlarTumList[0])
                        sonYorumlarList.add(sonYorumlarTumList[1])
                    } else if (sonYorumlarTumList.size > 1) {
                        sonYorumlarList.add(sonYorumlarTumList[0])
                        sonYorumlarList.add(sonYorumlarTumList[1])
                    } else if (sonYorumlarTumList.size > 0) {
                        sonYorumlarList.add(sonYorumlarTumList[0])
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

    override fun onStart() {
        FirebaseDatabase.getInstance().reference.child("users").child(userID).child("user_details").child("son_aktiflik_zamani").setValue(ServerValue.TIMESTAMP)
        super.onStart()
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
