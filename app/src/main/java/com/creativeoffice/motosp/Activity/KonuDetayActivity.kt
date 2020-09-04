package com.creativeoffice.motosp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.CevaplarAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_konu_detay.*
import kotlinx.android.synthetic.main.dialog_konu_cevap_duzenle.view.*
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class KonuDetayActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth

    lateinit var cevapList: ArrayList<ForumKonuData.cevaplar>
    lateinit var konuBasligi: String
    lateinit var konuCevabi: String
    lateinit var konuSahibiUserName: String
    lateinit var konuKey: String
    lateinit var konuAcanKey: String

    lateinit var CevaplarAdapter: CevaplarAdapter

    var yorumFotoUri: Uri? = null

    lateinit var data: ByteArray
    val RESIM_SEC = 100

    var userID: String? = null
    val ref = FirebaseDatabase.getInstance().reference

    init {
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konu_detay)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
       // this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        cevapList = ArrayList()
        setupRecyclerViewCevap()

        konuBasligi = intent.getStringExtra("konuBasligi").toString()
        konuCevabi = intent.getStringExtra("konuCevabi").toString()
        konuKey = intent.getStringExtra("konuKey").toString()
        konuAcanKey = intent.getStringExtra("konuyu_acan_key").toString()



        tvKonuBasligi.text = konuBasligi
        tvCevap.text = konuCevabi

        initBtn(konuKey)
        initVeri(konuKey)
    }

    private fun initBtn(konuKey: String?) {
        etCevapKonu.addTextChangedListener(watcherForumCevap)

        imgSend.setOnClickListener {


            val konuyaVerilenCevap = etCevapKonu.text.toString()
            //  var cevapKey = ref.child("Forum").child("cevaplar").push().key
            ref.child("users").child(userID.toString()).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    //cevaba bi key olusturduk
                    val cevapkey = ref.child("Forum").child(konuKey.toString()).child("cevaplar").push().key
                    val cevapYazan = p0.value.toString()
                    //ilk olarak cevap vereni son cevap olarak kaydediyruz.
                    val soncevapData = ForumKonuData.son_cevap(konuyaVerilenCevap, cevapkey, cevapYazan, null, userID, konuKey.toString())
                    ref.child("Forum").child(konuKey.toString()).child("son_cevap").setValue(soncevapData)
                    ref.child("Forum").child(konuKey.toString()).child("son_cevap").child("cevap_zamani").setValue(ServerValue.TIMESTAMP)
                    //son cevap zamanı ekliyoruz
                    ref.child("Forum").child(konuKey.toString()).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)

                    //cevaba da eklıyruz
                    val cevapData = ForumKonuData.son_cevap(konuyaVerilenCevap, cevapkey, cevapYazan, null, userID, konuKey.toString())
                    ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).setValue(cevapData)
                    ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).child("cevap_zamani").setValue(ServerValue.TIMESTAMP)
                        .addOnCompleteListener {
                            etCevapKonu.text.clear()
                            imgYorumFotosu.visibility = View.GONE


                            if (yorumFotoUri != null) {


                                FirebaseStorage.getInstance().reference.child("YorumFotolari").child(konuKey.toString()).child(cevapkey.toString()).putFile(yorumFotoUri!!) // burada fotografı kaydettik veritabanına.
                                    .addOnSuccessListener { UploadTask ->
                                        UploadTask.storage.downloadUrl.addOnSuccessListener { itUri ->
                                            val downloadUrl = itUri.toString()
                                            ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapkey.toString()).child("Foto").setValue(downloadUrl)
                                            Toast.makeText(this@KonuDetayActivity, "Yorumun Gönderildi", Toast.LENGTH_SHORT).show()
                                            CevaplarAdapter.notifyDataSetChanged()
                                        }
                                    }
                            }
                        }
                }
            })


        }

        imgFoto.setOnClickListener {
            var intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent, RESIM_SEC)
        }


        imgAyarlar.setOnClickListener {


            val popup = PopupMenu(this, imgAyarlar)
            popup.inflate(R.menu.popup_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popDüzenle -> {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

                        val view: View = View.inflate(this, R.layout.dialog_konu_cevap_duzenle, null)

                        view.etKonuCevabi.setText(konuBasligi)
                        builder.setView(view)

                        val dialog: Dialog = builder.create()
                        view.btnKaydet.setOnClickListener {

                            FirebaseDatabase.getInstance().reference.child("Forum").child(konuKey.toString()).child("konu_basligi").setValue(view.etKonuCevabi.text.toString())
                                .addOnCompleteListener {
                                    val intent = Intent(this, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                    intent.putExtra("konuBasligi", view.etKonuCevabi.text.toString())
                                    intent.putExtra("konuKey", konuKey)
                                    intent.putExtra("konuyu_acan_key", konuAcanKey)
                                    startActivity(intent)

                                }

                            dialog.dismiss()

                        }
                        view.btnIptal.setOnClickListener {

                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                    R.id.popSil -> {
                        val alert = AlertDialog.Builder(this)
                            .setTitle("Yorumu Sil")
                            .setMessage("Emin Misiniz ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {

                                    ref.child("Forum").child(konuKey.toString()).removeValue().addOnCompleteListener {
                                        val intent = Intent(this@KonuDetayActivity, HomeActivity::class.java)//.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        startActivity(intent)
                                    }
                                }

                            })
                            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    p0!!.dismiss()
                                }
                            }).create()

                        alert.show()
                    }
                }
                return@OnMenuItemClickListener true
            })

            popup.show()


        }

    }

    private fun initVeri(konuKey: String?) {
        ref.child("Forum").child(konuKey.toString()).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child("cevaplar").hasChildren()) {
                    cevapList.clear()
                    for (i in p0.child("cevaplar").children) {
                        val gelenKonu = i.getValue(ForumKonuData.cevaplar::class.java)
                        cevapList.add(gelenKonu!!)
                    }
                    cevapList.sortByDescending { it.cevap_zamani }
                }
                val tarih = p0.child("acilma_zamani").value
                tarih?.let {
                    tvZaman.text = formatDate(tarih.toString().toLong()).toString()
                }
                CevaplarAdapter.notifyDataSetChanged()
            }
        })

        ref.child("users").child(konuAcanKey.toString()).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                tvUserName.text = p0.value.toString()
                konuSahibiUserName = p0.value.toString()
            }

        })


    }


    private fun setupRecyclerViewCevap() {
        rcCevaplar.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //   rcBayi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        CevaplarAdapter = CevaplarAdapter(this, cevapList, userID)
        rcCevaplar.setHasFixedSize(true)
        rcCevaplar.adapter = CevaplarAdapter
        rcCevaplar.setItemViewCacheSize(20)

    }

    private fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("h:mm E MM/dd ", Locale("tr"))
        return sdf.format(date)

    }

    var watcherForumCevap = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 3) {
                imgSend.isEnabled = true
            } else {
                imgSend.isEnabled = false
            }
        }

    }

/*
    override fun onBackPressed() {
        super.onBackPressed()
        onBackPressed()
    }
*/
    override fun onResume() {
        super.onResume()
        if (userID == konuAcanKey) {
            imgAyarlar.visibility = View.VISIBLE
        } else {
            imgAyarlar.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == RESULT_OK && data!!.data != null) {
            yorumFotoUri = data.data
            imgYorumFotosu.visibility = View.VISIBLE
            imgYorumFotosu.setImageURI(yorumFotoUri)


        }

    }
}
