package com.creativeoffice.motosp.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ForumKonuBasliklariAdapter
import com.creativeoffice.motosp.Adapter.HaberYorumlariAdapter
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.EventBusDataEvents
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_haber_detaylari.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
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

    val ref = FirebaseDatabase.getInstance().reference


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


        initGelenVeriler()
        initList()
        init()

        swHaberDetay.setOnRefreshListener {
       //     swHaberDetay.isRefreshing = false
            initList()
            init()
        }


    }

    private fun initGelenVeriler() {

        haber_altbaslik = intent.getStringExtra("haber_altbaslik")
        haber_baslik = intent.getStringExtra("haber_baslik")
        haber_eklenme = intent.getStringExtra("haber_eklenme")
        haber_icerik = intent.getStringExtra("haber_icerik")
        haber_key = intent.getStringExtra("haber_key")
        haber_video = intent.getStringExtra("haber_video")
        haber_videolumu = intent.getStringExtra("haber_videolumu")


    }


    private fun init() {
        tvHaberBaslik.text = haber_baslik.toString()
        tvHaberAltBaslik.text = haber_altbaslik.toString()
        tvHaberZamani.text = formatDate(haber_eklenme.toString().toLong()).toString()
        tvHaberIcerik.text = haber_icerik.toString()
        etYorum.addTextChangedListener(watcher)
        tvGonder.setOnClickListener {
            var yorumKey = ref.child("Haberler").child(haber_key.toString()).child("yorumlar").push().key
            var yapılanYorum = HaberlerData.Yorumlar(haber_key, userName, System.currentTimeMillis(), etYorum.text.toString(), yorumKey, userID)

            ref.child("Haberler").child(haber_key.toString()).child("yorumlar").child(yorumKey.toString()).setValue(yapılanYorum).addOnCompleteListener {
                ref.child("Haberler").child(haber_key.toString()).child("yorumlar").child(yorumKey.toString()).child("tarih").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {
                    initList()
                    etYorum.setText("")
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(getCurrentFocus()?.getWindowToken(), 0)
                    Snackbar.ANIMATION_MODE_FADE
                    Snackbar.make(constraintLayout7, "Yorumun gönderildi...", 500).show()


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

    }

    var watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length in 5..150) {

                tvGonder.visibility = View.VISIBLE
                tvGonder.isEnabled = true
                //   tvGonder.(R.color.yesil)
            } else {
                tvGonder.isEnabled = false
                //      tvGonder.setBackgroundResource(R.color.gri)

            }
        }

    }


    private fun setupHaberYorumlarRecyclerView() {
        yorumListesi.sortBy { it.tarih }
        rcHaberDetaylari.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        val yorumAdapter = HaberYorumlariAdapter(this, yorumListesi, userID)
        rcHaberDetaylari.adapter = yorumAdapter

    }

    private fun initList() {
        ref.child("Haberler").child(haber_key.toString()).child("yorumlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                yorumListesi.clear()
                if (p0.hasChildren()) {

                    for (ds in p0.children) {
                        var gelenYorumlar = ds.getValue(HaberlerData.Yorumlar::class.java)!!
                        yorumListesi.add(gelenYorumlar)

                    }
                    swHaberDetay.isRefreshing = false
                    setupHaberYorumlarRecyclerView()

                } else {
                    swHaberDetay.isRefreshing = false
                    yorumListesi.add(HaberlerData.Yorumlar("ilk", "Admin", 1, "İlk Yorumu Yapmak İster Misin?", "key", "admin"))
                    setupHaberYorumlarRecyclerView()
                }

            }

        })
    }


    @Subscribe(sticky = true)
    internal fun onUserName(gelenUserName: EventBusDataEvents.KullaniciAdi) {
        userName = gelenUserName.userName


    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("EEE, d MMM, ''yy", Locale("tr"))
        return sdf.format(date)

    }

    override fun onBackPressed() {
        startActivity(Intent(this@HaberDetaylariActivity, HomeActivity::class.java))
        super.onBackPressed()
    }
}
