package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ParcaAdapter
import com.creativeoffice.motosp.Adapter.YakitAdapter
import com.creativeoffice.motosp.Adapter.YorumAdapter
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.Users
import com.creativeoffice.motosp.Datalar.YorumlarData
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

import kotlinx.android.synthetic.main.activity_model_detayi.*
import kotlinx.android.synthetic.main.activity_parca_ekle.view.*
import kotlinx.android.synthetic.main.dialog_yakit_tuketim.view.*
import kotlinx.android.synthetic.main.dialog_yorum.view.*
import java.text.DecimalFormat


class ModelDetayiActivity : AppCompatActivity() {

    var marka: String? = null
    var model: String? = null
    var userID: String? = null

    //  var ort: String? = null
    var kullaniciAdi: String? = null

    var ilkSetupOldumu: Boolean? = null

    lateinit var gelenUsers: Users
    lateinit var yorumAdapter: YorumAdapter
    lateinit var parcaAdapter: ParcaAdapter
    lateinit var yakitAdapter: YakitAdapter

    var parcaListesi = ArrayList<ModelDetaylariData.Parcalar>()
    var yorumListesi = ArrayList<ModelDetaylariData.Yorumlar>()
    var yakitListesi = ArrayList<ModelDetaylariData.YakitTuketimi>()
    var yildizListesi = ArrayList<ModelDetaylariData.Yildizlar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_detayi)

        userID = FirebaseAuth.getInstance().currentUser!!.uid
        ilkSetupOldumu = false

        init()
        setupYorumlarRecyclerView()
        initVeri("")
        verileriGetir()
        goruntulenmeSayisi()


    }

    private fun initVeri(rec: String) {


        //Kullanıcı Verilerini getirdik
        FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                gelenUsers = p0.getValue(Users::class.java)!!
                // kullaniciAdi = gelenUsers.user_name.toString()
            }
        })
        //MotorVeri listesi
        FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var modellerinVerisi = p0.getValue(ModelDetaylariData::class.java) ?: return //Cok onemli
                yorumListesi.clear()
                parcaListesi.clear()
                yakitListesi.clear()


                val yorumHashMap = modellerinVerisi.yorumlar ?: return
                yorumListesi = ArrayList()
                for (i in yorumHashMap.values) {
                    yorumListesi.add(i)
                }
                yorumListesi.sortBy { it.tarih } //sortby tarihe göre sıralar


                val parcaHashMap = modellerinVerisi.yy_parcalar ?: return
                parcaListesi = ArrayList()
                for (i in parcaHashMap.values) {
                    parcaListesi.add(i)
                }
                parcaListesi.sortBy { it.parca_uyum_model_yili } //sortby tarihe göre sıralar

                val yakitHashMap = modellerinVerisi.yy_yakit_verileri ?: return
                for (i in yakitHashMap.values) {
                    yakitListesi.add(i)
                }
                yakitListesi.sortBy { it.yakitTuk }

                //yakitin ortalamasını alıyoruz.
                var yakitGirdiSayisi = p0.child("yy_yakit_verileri").childrenCount
                var form = DecimalFormat("0.0")
                var ortalamaYakit = yakitListesi.map { it -> it?.yakitTuk!! }.average()
                var ortYakitString = form.format(ortalamaYakit).toString() + " lt /100 km"


                FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yakitTuk").setValue(ortYakitString).addOnCompleteListener {
                    detay_yakitTuk.text = ortYakitString
                }

                yildizListesi.clear()
                if (!modellerinVerisi.yildizlar.isNullOrEmpty()) {
                    val yildizHashMap = modellerinVerisi.yildizlar ?: return
                    for (i in yildizHashMap.values) {
                        yildizListesi.add(i)
                    }
                    var ortalamaYildiz = yildizListesi.map { it -> it?.yildiz!! }.average()


                    Log.e("sad", ortalamaYildiz.toString())
                    rbMotor.rating = ortalamaYildiz.toFloat()


                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("ortYildiz").setValue(form.format(ortalamaYildiz).toString())
                }







                if (rec == "yorum") {
                    setupYorumlarRecyclerView()
                } else if (rec == "parca") {
                    setupParcalarRecyclerView()
                } else if (rec == "yakit") {
                    setupYakitRecyclerView()
                } else {
                    setupYorumlarRecyclerView()
                }


            }


        })


    }

    //butontıklamalarının hepsi burada
    private fun init() {

        val ref = FirebaseDatabase.getInstance().reference
        tvParcaEkle.visibility = View.GONE
        tvYakitTukEkle.visibility = View.GONE
        imgYorum.setBackgroundResource(R.drawable.ic_yorum_mavi)

        var yildiz = ModelDetaylariData.Yildizlar()
        rbMotor.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                yildiz = ModelDetaylariData.Yildizlar(ratingBar!!.rating)
            }
        }
        imgRating.setOnClickListener {
            Log.e("saa", "burada")
            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yildizlar").child(userID.toString()).setValue(yildiz)

        }



        imgYorum.setOnClickListener {
            imgYorum.setBackgroundResource(R.drawable.ic_yorum_mavi)
            imgYedek.setBackgroundResource(R.drawable.ic_yedekparca)
            imgYakitTuk.setBackgroundResource(R.drawable.ic_yakit)

            tvYorumYap.visibility = View.VISIBLE
            tvParcaEkle.visibility = View.GONE
            tvYakitTukEkle.visibility = View.GONE

            rcYorumlar.visibility = View.VISIBLE
            rcParca.visibility = View.GONE
            rcYakit.visibility = View.GONE


            setupYorumlarRecyclerView()
        }
        imgYedek.setOnClickListener {
            imgYedek.setBackgroundResource(R.drawable.ic_yedekparca_mavi)
            imgYorum.setBackgroundResource(R.drawable.ic_yorum)
            imgYakitTuk.setBackgroundResource(R.drawable.ic_yakit)

            tvYorumYap.visibility = View.GONE
            tvParcaEkle.visibility = View.VISIBLE
            tvYakitTukEkle.visibility = View.GONE

            rcYorumlar.visibility = View.GONE
            rcParca.visibility = View.VISIBLE
            rcYakit.visibility = View.GONE

            setupParcalarRecyclerView()
        }
        imgYakitTuk.setOnClickListener {
            imgYakitTuk.setBackgroundResource(R.drawable.ic_yakittuk_mavi)
            imgYorum.setBackgroundResource(R.drawable.ic_yorum)
            imgYedek.setBackgroundResource(R.drawable.ic_yedekparca)

            tvYorumYap.visibility = View.GONE
            tvParcaEkle.visibility = View.GONE
            tvYakitTukEkle.visibility = View.VISIBLE

            rcYorumlar.visibility = View.GONE
            rcParca.visibility = View.GONE
            rcYakit.visibility = View.VISIBLE

            setupYakitRecyclerView()

        }

        tvYorumYap.setOnClickListener {


            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_yorum, null)

            builder.setView(view)
            var dialog: Dialog = builder.create()
            view.tvIptal.setOnClickListener {
                dialog!!.dismiss()
            }
            view.tvGonder.setOnClickListener {
                var yorum = view.edYorum.text.toString()
                if (yorum.length > 5) {
                    FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val key = ref.child("tum_motorlar").child(model.toString()).child("yorumlar").push().key
                            kullaniciAdi = p0.child("user_name").value.toString()
                            var yorumlar = ModelDetaylariData.Yorumlar(kullaniciAdi, yorum, null, key, model, userID)
                            ref.child("tum_motorlar").child(model.toString()).child("yorumlar").child(key.toString()).setValue(yorumlar)
                            ref.child("tum_motorlar").child(model.toString()).child("yorumlar").child(key.toString()).child("tarih").setValue(ServerValue.TIMESTAMP)
                            //+5 yorum puan ekleme

                            var sonYorum = YorumlarData(marka, model, kullaniciAdi, null, yorum)
                            ref.child("tum_motorlar").child("yorumlar_son").child(model.toString()).setValue(sonYorum)
                            ref.child("tum_motorlar").child("yorumlar_son").child(model.toString()).child("yorum_zaman").setValue(ServerValue.TIMESTAMP)


                            var eskiPuan = gelenUsers.user_details!!.puan!!.toInt()
                            var yeniPuan = eskiPuan + 3
                            ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)
                            initVeri("yorum")
                            dialog.dismiss()
                        }
                    })
                }

            }
            dialog.show()

        }
        tvParcaEkle.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.activity_parca_ekle, null)

            builder.setView(view)
            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Ekle", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    var parcaIsmi = view.etParcaİsmi.text.toString()
                    var parcaModel = view.etParcaModelYili.text.toString()
                    var parcaYorum = view.etParcaUygunlugu.text.toString()
                    FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            kullaniciAdi = p0.child("user_name").value.toString()

                            var parcaKey = FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").push().key


                            var parcaVerisi = ModelDetaylariData.Parcalar(parcaIsmi, parcaYorum, parcaModel, kullaniciAdi, parcaKey, marka, model)
                            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").child(parcaKey.toString()).setValue(parcaVerisi)
                            initVeri("parca")
                            dialog!!.dismiss()
                        }
                    })
                }
            })
            var dialog: Dialog = builder.create()
            dialog.show()


        }
        tvYakitTukEkle.setOnClickListener {

            var builder: AlertDialog.Builder = AlertDialog.Builder(this)
            var inflater: LayoutInflater = layoutInflater
            var view: View = inflater.inflate(R.layout.dialog_yakit_tuketim, null)

            builder.setView(view)
            builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }

            })
            builder.setPositiveButton("Gönder", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var gelenYakit = view.etYakitVerisi.text.toString().toFloat()
                    var motorYili = view.etModelYili.text.toString()

                    kullaniciAdi = gelenUsers.user_name.toString()

                    var yakitVerisi = ModelDetaylariData.YakitTuketimi(gelenYakit, kullaniciAdi, motorYili)
                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_yakit_verileri").child(kullaniciAdi.toString()).setValue(yakitVerisi)
                    initVeri("yakit")

                }
            })

            initVeri("yakit")
            var dialog: Dialog = builder.create()
            dialog.show()

        }

    }

    private fun goruntulenmeSayisi() {
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("tum_motorlar").child(model.toString()).child("goruntulenme_sayisi").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value.toString() != "null") {
                    var eskiPuan = p0.value.toString().toInt()

                    var yeniPuan = eskiPuan + 1
                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("goruntulenme_sayisi").setValue(yeniPuan)

                } else if (p0.value.toString() == "null") {

                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("goruntulenme_sayisi").setValue(1)
                }

            }


        })

    }


    fun setupYorumlarRecyclerView() {

        rcYorumlar.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, true)
        yorumAdapter = YorumAdapter(this@ModelDetayiActivity, yorumListesi, userID)
        yorumAdapter.notifyDataSetChanged()
        //  rcYorumlar.setItemViewCacheSize(20)
        rcYorumlar.setHasFixedSize(true)
        rcYorumlar.adapter = yorumAdapter
        // rcYorumlar.refreshDrawableState()

    }

    fun setupParcalarRecyclerView() {


        rcParca.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, true)
        parcaAdapter = ParcaAdapter(this@ModelDetayiActivity, parcaListesi, userID)
        parcaAdapter.notifyDataSetChanged()
        rcParca.setHasFixedSize(true)
        rcParca.adapter = parcaAdapter
        rcParca.refreshDrawableState()


    }

    fun setupYakitRecyclerView() {

        rcYakit.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, false)
        yakitAdapter = YakitAdapter(this@ModelDetayiActivity, yakitListesi, userID)
        yakitAdapter.notifyDataSetChanged()
        rcYakit.setHasFixedSize(true)
        rcYakit.adapter = yakitAdapter
        rcYakit.refreshDrawableState()

    }


    private fun verileriGetir() {
        //  var intent = Intent()
        //  val marka by lazy { intent.getStringExtra("Marka") }
        marka = intent.getStringExtra("Marka")
        model = intent.getStringExtra("Model")
        var kategori = intent.getStringExtra("Kategori")
        var silindir = intent.getStringExtra("Silindir")
        var beygir = intent.getStringExtra("Beygir")
        var hiz = intent.getStringExtra("Hiz")
        var tork = intent.getStringExtra("Tork")
        var devir = intent.getStringExtra("Devir")
        var agirlik = intent.getStringExtra("Agirlik")
        var yakitKap = intent.getStringExtra("YakitKap")
        var yakitTuk = intent.getStringExtra("YakitTuk")
        var tanitim = intent.getStringExtra("tanitim")
        var video = intent.getStringExtra("video")
        var fiyat = intent.getStringExtra("fiyat")

        tvFiyat.visibility = View.GONE
        if (fiyat != "null" && !fiyat.isNullOrEmpty()) {
            tvFiyat.text = "Ortalama Fiyat: " + fiyat
            tvFiyat.visibility = View.VISIBLE
        }


        var currentSecond = 0f

        ytTekModelList.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(video.toString(), 0f)
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                currentSecond = second
            }
        })


        if (tanitim == "" || tanitim.isNullOrEmpty()) {
            tvTanitim2.visibility = View.GONE
        }

        tvMarka.text = marka
        tvModel.text = model
        detay_agirlik.text = agirlik
        detay_beygir.text = beygir
        detay_devir.text = devir
        detay_hiz.text = hiz
        detay_kategori.text = kategori
        detay_silindirhacmi.text = silindir
        detay_tork.text = tork
        detay_yakitKap.text = yakitKap
        detay_yakitTuk.text = yakitTuk
        tvTanitim2.text = tanitim

        if (marka.toString() == "Bmw") {
            imgMarka.setBackgroundResource(R.mipmap.ic_bmw)
        } else if (marka.toString() == "Honda") {
            imgMarka.setBackgroundResource(R.drawable.ic_honda)
        } else if (marka.toString() == "Triumph") {
            imgMarka.setBackgroundResource(R.drawable.ic_tr)
        } else if (marka.toString() == "Yamaha") {
            imgMarka.setBackgroundResource(R.drawable.yamaha)
        } else if (marka.toString() == "Suzuki") {
            imgMarka.setBackgroundResource(R.drawable.suzuki)
        } else if (marka.toString() == "Kawasaki") {
            imgMarka.setBackgroundResource(R.drawable.kawasaki)
        }

        imgMotorTipi.setAnimation(AnimationUtils.loadAnimation(this, R.anim.olusma_sol))
        if (kategori == "Scooter") {
            imgMotorTipi.setBackgroundResource(R.drawable.ic_scooter)
        } else if (kategori == "Sport" || kategori == "Racing") {
            imgMotorTipi.setBackgroundResource(R.drawable.ic_sport)
        } else if (kategori == "Touring" || kategori == "Enduro" || kategori == "Adventure") {
            imgMotorTipi.setBackgroundResource(R.drawable.ic_touring)
        } else if (kategori == "Cross") {
            imgMotorTipi.setBackgroundResource(R.drawable.ic_cross)
        } else if (kategori == "Naked") {
            imgMotorTipi.setBackgroundResource(R.drawable.ic_naked)
        } else if (kategori == "Chopper") {
            imgMotorTipi.setBackgroundResource(R.drawable.ic_chopper)
        }


    }

    override fun onStart() {
        super.onStart()
        initVeri("yorum")

    }


}
