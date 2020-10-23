package com.creativeoffice.motosp.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.CevaplarAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_konu_detay.*
import kotlinx.android.synthetic.main.dialog_konu_cevap_duzenle.view.*
import java.io.ByteArrayOutputStream
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
    lateinit var cevapKey: String
    var yorumFotoUri: Uri? = null

    lateinit var data: ByteArray
    val RESIM_SEC = 100

    var userID: String? = null
    val ref = FirebaseDatabase.getInstance().reference

    var loading: Dialog? = null
    var handler = Handler()

    init {
        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konu_detay)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        //   this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        cevapList = ArrayList()
        setupRecyclerViewCevap()

        konuBasligi = intent.getStringExtra("konuBasligi").toString()
        konuCevabi = intent.getStringExtra("konuCevabi").toString()
        konuKey = intent.getStringExtra("konuKey").toString()
        konuAcanKey = intent.getStringExtra("konuyu_acan_key").toString()



        tvKonuBasligi.text = konuBasligi
        tvCevap.text = konuCevabi

        dialogCalistir()
        handler.postDelayed({ initVeri(konuKey) }, 800)
        initBtn(konuKey)

    }


    private fun dialogGizle() {
        handler.postDelayed({ loading?.let { if (it.isShowing) it.cancel() } }, 800)

    }

    private fun dialogCalistir() {
        dialogGizle()
        loading = LoadingDialog.startDialog(this)
    }

    private fun initBtn(konuKey: String) {

        swKonuDetay.setOnRefreshListener {
            dialogCalistir()
            initVeri(konuKey)
            swKonuDetay.isRefreshing = false
        }


        etCevapKonu.addTextChangedListener(watcherForumCevap)


        imgSend.setOnClickListener {
            if (!etCevapKonu.text.isNullOrEmpty()) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)


                val konuyaVerilenCevap = etCevapKonu.text.toString()
                //  var cevapKey = ref.child("Forum").child("cevaplar").push().key
                ref.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var imgURL = p0.child("user_details/profile_picture").value.toString()
                        if (imgURL != "default") {
                            Picasso.get().load(imgURL).into(imgUser)
                        }
                        //cevaba bi key olusturduk
                        cevapKey = ref.child("Forum").child(konuKey).child("cevaplar").push().key.toString()
                        val cevapYazan = p0.child("user_name").value.toString()

                        //ilk olarak cevap vereni son cevap olarak kaydediyruz.
                        val soncevapData = ForumKonuData.son_cevap(konuyaVerilenCevap, cevapKey, cevapYazan, System.currentTimeMillis(), userID, konuKey.toString())
                        ref.child("Forum").child(konuKey).child("son_cevap").setValue(soncevapData)
                        ref.child("Forum").child(konuKey).child("son_cevap").child("cevap_zamani").setValue(ServerValue.TIMESTAMP)

                        //son cevap zamanı ekliyoruz
                        ref.child("Forum").child(konuKey).child("son_cevap_zamani").setValue(ServerValue.TIMESTAMP)

                        //cevabı da eklıyruz
                        val cevapData = ForumKonuData.son_cevap(konuyaVerilenCevap, cevapKey, cevapYazan, System.currentTimeMillis(), userID, konuKey)
                        ref.child("Forum").child(konuKey).child("cevaplar").child(cevapKey).setValue(cevapData)

                        etCevapKonu.text!!.clear()
                        
                        ref.child("Forum").child(konuKey).child("cevaplar").child(cevapKey).child("Foto").setValue("null").addOnCompleteListener {
                            imgYorumFotosu.visibility = View.GONE

                            val snackbar = Snackbar.make(tumLayout, "Yorumun gönderildi...", Snackbar.LENGTH_LONG)
                            val textView = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
                            textView.textSize = 16f
                            snackbar.show()

                            if (yorumFotoUri != null) {
                                Picasso.get().load(R.drawable.photo).into(imgFoto)
                                imgFoto.setPadding(16, 16, 16, 16)
                                var comperessed = BackgroundResimCompress()
                                comperessed.execute(yorumFotoUri)
                                yorumFotoUri = null
                            }

                        }.addOnFailureListener {
                            Snackbar.make(tumLayout, "Yorum Gönderilemedi... ", 1500).show()
                        }

                        //kullanıcının yaptığı yorumu profiline ekledık.
                        var yorumRef = ref.child("users").child(userID.toString()).child("yorumlarim")
                        yorumRef.child(cevapKey).setValue(cevapData)
                        yorumRef.child(cevapKey).child("konu_basligi").setValue(konuBasligi)
                        yorumRef.child(cevapKey).child("konu_sahibi_cevap").setValue(konuCevabi)
                        yorumRef.child(cevapKey).child("konuyu_acan_key").setValue(konuAcanKey)
                    }
                })


            } else {
                val snackbar = Snackbar.make(tumLayout, "Yorum yok... ", Snackbar.LENGTH_LONG)
                snackbar.show()
            }

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

                            ref.child("Forum").child(konuKey.toString()).child("konu_basligi").setValue(view.etKonuCevabi.text.toString())
                                .addOnCompleteListener {
                                    Snackbar.make(tumLayout, "Başlık Değiştirildi", 1000).show()

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

                                    ref.child("Forum").child(konuKey.toString()).child("konu_acik_mi").setValue(false).addOnCompleteListener {
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

                dialogGizle()
            }
        })

        ref.child("users").child(konuAcanKey.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                tvUserName.text = p0.child("user_name").value.toString()
                konuSahibiUserName = p0.child("user_name").value.toString()


                var imgURL = p0.child("user_details/profile_picture").value.toString()
                if (imgURL != "default") {
                    Picasso.get().load(imgURL).into(imgUser)
                }
            }

        })


    }

    inner class BackgroundResimCompress() : AsyncTask<Uri, Double, ByteArray>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray? {

            var myBitmap: Bitmap? = null
            var resimBytes: ByteArray? = null

            try {
                if (Build.VERSION.SDK_INT >= 29) {
                    var source = ImageDecoder.createSource(applicationContext.contentResolver, params[0]!!)
                    myBitmap = ImageDecoder.decodeBitmap(source)
                } else myBitmap = MediaStore.Images.Media.getBitmap(contentResolver, params[0])
            } catch (e: Exception) {
                ref.child("Hatalar/KonuDetayActivity/BackgroundResimCompress").push().setValue(e.message)
            }

            for (i in 1..6) {
                resimBytes = convertBitmaptoByte(myBitmap, 50 / i)
                Log.e("Test", "sıkısmıs: " + resimBytes!!.size.toString())
            }

            return resimBytes
        }


        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
            uploadPhototoFirebase(result)
        }

    }

    private fun convertBitmaptoByte(myBitmap: Bitmap?, i: Int): ByteArray? {
        var stream = ByteArrayOutputStream()
        myBitmap?.compress(Bitmap.CompressFormat.JPEG, i, stream)
        return stream.toByteArray()
    }

    private fun uploadPhototoFirebase(result: ByteArray?) {

        FirebaseStorage.getInstance().reference.child("YorumFotolari").child(konuKey.toString()).child(cevapKey).putBytes(result!!) // burada fotografı kaydettik veritabanına.
            .addOnSuccessListener { UploadTask ->
                UploadTask.storage.downloadUrl.addOnSuccessListener { itUri ->
                    val downloadUrl = itUri.toString()
                    ref.child("Forum").child(konuKey.toString()).child("cevaplar").child(cevapKey).child("Foto").setValue(downloadUrl)
                    //  Toast.makeText(this@KonuDetayActivity, "Yorumun Gönderildi", Toast.LENGTH_SHORT).show()
                    CevaplarAdapter.notifyDataSetChanged()
                }
            }

    }


    private fun setupRecyclerViewCevap() {
        // rcCevaplar.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rcCevaplar.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        CevaplarAdapter = CevaplarAdapter(this, cevapList, userID)
        rcCevaplar.suppressLayout(true)
        rcCevaplar.setHasFixedSize(true)
        rcCevaplar.adapter = CevaplarAdapter
    }

    private fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("h:mm  E MM/dd ", Locale("tr"))
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


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (userID == konuAcanKey) {
            imgAyarlar.visibility = View.VISIBLE
            appBarLayouts.visibility = View.VISIBLE
        } else {
            imgAyarlar.visibility = View.GONE
            appBarLayouts.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESIM_SEC && resultCode == RESULT_OK && data!!.data != null) {
            yorumFotoUri = data.data
            imgFoto.visibility = View.VISIBLE
            imgFoto.setPadding(0, 4, 0, 4)
            // imgFoto.setImageURI(yorumFotoUri)
            Picasso.get().load(yorumFotoUri).resize(150, 150).into(imgFoto)


        }

    }
}
