package com.creativeoffice.motosp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.HaberYorumlariAdapter
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_haber_detaylari.*
import java.text.SimpleDateFormat
import java.util.*

class HaberDetaylariActivity : AppCompatActivity() {

    var haber_altbaslik: String? = null
    var haber_baslik: String? = null
    var haber_eklenme: String? = null
    var haber_icerik: String? = null
    var haber_key: String? = null
    var haber_video: String? = null
    var haber_videolumu: String? = null


    var userID: String? = null
    var userName: String? = null
    var yorumListesi = ArrayList<HaberlerData.Yorumlar>()

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haber_detaylari)
     //   this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
      //  this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid


        initVeri()
        init()


    }

    private fun initVeri() {

        haber_altbaslik = intent.getStringExtra("haber_altbaslik")
        haber_baslik = intent.getStringExtra("haber_baslik")
        haber_eklenme = intent.getStringExtra("haber_eklenme")
        haber_icerik = intent.getStringExtra("haber_icerik")
        haber_key = intent.getStringExtra("haber_key")
        haber_video = intent.getStringExtra("haber_video")
        haber_videolumu = intent.getStringExtra("haber_videolumu")

        FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                userName = p0.value.toString()
            }

        })
    }


    private fun init() {
        val ref = FirebaseDatabase.getInstance().reference
        tvHaberBaslik.text = haber_baslik.toString()
        tvHaberAltBaslik.text = haber_altbaslik.toString()
        tvHaberZamani.text = formatDate(haber_eklenme.toString().toLong()).toString()
        tvHaberIcerik.text = haber_icerik.toString()
        etYorum.addTextChangedListener(watcher)
        tvGonder.setOnClickListener {
            var yorumKey = ref.child("Haberler").child(haber_key.toString()).child("yorumlar").push().key
            var yapılanYorum = HaberlerData.Yorumlar(haber_key, userName, null, etYorum.text.toString(), yorumKey, userID)

            ref.child("Haberler").child(haber_key.toString()).child("yorumlar").child(yorumKey.toString()).setValue(yapılanYorum).addOnCompleteListener {
                ref.child("Haberler").child(haber_key.toString()).child("yorumlar").child(yorumKey.toString()).child("tarih").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {
                    setupRec()
                    etYorum.text.clear()
                }
            }

        }


        if (haber_videolumu == "true") {

            imgHaberDetay.visibility = View.GONE
            var currentSecond = 0f
            youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(haber_video.toString(), 0f)
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    currentSecond = second
                }
            })
            youtubePlayer.display
            youtubePlayer.getPlayerUiController().showVideoTitle(false)

        } else if (haber_videolumu == "false") {
            youtubePlayer.visibility = View.GONE
            Picasso.get().load(haber_video.toString()).into(imgHaberDetay)

        }



        FirebaseDatabase.getInstance().reference.child("Haberler").child(haber_key.toString()).child("yorumlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                yorumListesi.clear()
                if (p0.hasChildren()) {

                    for (ds in p0.children) {
                        var gelenYorumlar = ds.getValue(HaberlerData.Yorumlar::class.java)!!
                        yorumListesi.add(gelenYorumlar)

                    }

                    setupHaberYorumlarRecyclerView()

                } else {

                    yorumListesi.add(HaberlerData.Yorumlar("ilk", "Admin", 1, "İlk Yorumu Yapmak İster Misin?", "key", "admin"))
                    setupHaberYorumlarRecyclerView()
                }

            }

        })
    }

    var watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 5) {

                tvGonder.visibility = View.VISIBLE
                tvGonder.isEnabled = true
                tvGonder.setBackgroundResource(R.color.yesil)
            } else {
                tvGonder.isEnabled = false
                tvGonder.setBackgroundResource(R.color.gri)

            }
        }

    }

    fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("EEE, d MMM, ''yy", Locale("tr"))
        return sdf.format(date)

    }


    fun setupHaberYorumlarRecyclerView() {
        yorumListesi.sortBy { it.tarih }
        rcHaberDetaylari.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        val yorumAdapter = HaberYorumlariAdapter(this, yorumListesi, userID)
        rcHaberDetaylari.adapter = yorumAdapter

    }

    fun setupRec() {
        FirebaseDatabase.getInstance().reference.child("Haberler").child(haber_key.toString()).child("yorumlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                yorumListesi.clear()
                if (p0.hasChildren()) {

                    for (ds in p0.children) {
                        var gelenYorumlar = ds.getValue(HaberlerData.Yorumlar::class.java)!!
                        yorumListesi.add(gelenYorumlar)

                    }

                    setupHaberYorumlarRecyclerView()

                } else {

                    yorumListesi.add(HaberlerData.Yorumlar("ilk", "Admin", 1, "İlk Yorumu Yapmak İster Misin?", "key", "admin"))
                    setupHaberYorumlarRecyclerView()
                }

            }

        })
    }

    override fun onBackPressed() {
        startActivity(Intent(this@HaberDetaylariActivity, HomeActivity::class.java))
        super.onBackPressed()
    }
}
