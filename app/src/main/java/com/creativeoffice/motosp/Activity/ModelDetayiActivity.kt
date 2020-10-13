package com.creativeoffice.motosp.Activity

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
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
import kotlinx.android.synthetic.main.dialog_parca_ekle.view.*
import kotlinx.android.synthetic.main.dialog_yakit_tuketim.view.*
import kotlinx.android.synthetic.main.dialog_yorum.view.*

import java.text.DecimalFormat


class ModelDetayiActivity : AppCompatActivity() {
    val ref = FirebaseDatabase.getInstance().reference
    var marka: String? = null
    var model: String? = null
    var userID: String? = null


    var kullaniciAdi: String? = null
    var kullaniciKendiPuan: Int? = null


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
        //   this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        userID = FirebaseAuth.getInstance().currentUser!!.uid

        init()


    }

    override fun onResume() {
        super.onResume()
        verileriGetir()
        yorumVerileri()
        yakitVerileri()
        parcaVerileri()
        yildizVerisi()
    }

    private fun verileriGetir() {
        val ref = FirebaseDatabase.getInstance().reference
        //  var intent = Intent()
        //  val marka by lazy { intent.getStringExtra("Marka") }
        yakitVerileri()
        parcaVerileri()

        marka = intent.getStringExtra("Marka")
        model = intent.getStringExtra("Model")


        ref.child("tum_motorlar").child(model.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {


                var kategori = p0.child("kategori").value.toString()
                var silindir = p0.child("silindirHacmi").value.toString()
                var beygir = p0.child("beygir").value.toString()
                var hiz = p0.child("hiz").value.toString()
                var tork = p0.child("tork").value.toString()
                var devir = p0.child("devir").value.toString()
                var agirlik = p0.child("agirlik").value.toString()
                var yakitKap = p0.child("yakitkap").value.toString()
                var yakitTuk = p0.child("yakitTuk").value.toString()
                var tanitim = p0.child("tanitim").value.toString()
                var video = p0.child("motorVideo").value.toString()
                var fiyat = p0.child("fiyat").value.toString()


                if (p0.child("goruntulenme_sayisi").value.toString() != "null") {
                    var eskiPuan = p0.child("goruntulenme_sayisi").value.toString().toInt()
                    var yeniPuan = eskiPuan + 1
                    ref.child("tum_motorlar").child(model.toString()).child("goruntulenme_sayisi").setValue(yeniPuan)

                } else if (p0.value.toString() == "null") {
                    ref.child("tum_motorlar").child(model.toString()).child("goruntulenme_sayisi").setValue(1)
                }



                tvFiyat.visibility = View.GONE
                if (fiyat != "1") {
                    tvFiyat.text = "₺ " + fiyat
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



                when (marka.toString()) {

                    "Honda" -> imgMarka.setBackgroundResource(R.drawable.ic_honda)
                    "Kawasaki" -> imgMarka.setBackgroundResource(R.drawable.kawasaki)
                    "Yamaha" -> imgMarka.setBackgroundResource(R.drawable.yamaha)
                    "Suzuki" -> imgMarka.setBackgroundResource(R.drawable.suzuki)
                    "Triumph" -> imgMarka.setBackgroundResource(R.drawable.ic_triumph_background)
                    "Bmw" -> imgMarka.setBackgroundResource(R.drawable.bmw)

                }

                imgMotorTipi.setAnimation(AnimationUtils.loadAnimation(this@ModelDetayiActivity, R.anim.olusma_sol))
                kategori.let {
                    when (kategori) {
                        "Scooter" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_scooter)
                        "Sport" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_sport)
                        "Racing" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_sport)
                        "Touring" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_touring)
                        "Enduro" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_touring)
                        "Adventure" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_touring)
                        "Cross" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_cross)
                        "Naked" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_naked)
                        "Chopper" -> imgMotorTipi.setBackgroundResource(R.drawable.ic_chopper)
                    }
                }


            }


        })


    }

    private fun yorumVerileri() {
        var ref = FirebaseDatabase.getInstance().reference
        ////kullanıcı adı
        ref.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                kullaniciAdi = p0.child("user_name").value.toString()
                kullaniciKendiPuan = p0.child("user_details").child("puan").value.toString().toInt()
            }

        })

        ref.child("tum_motorlar").child(model.toString()).child("yorumlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                yorumListesi.clear()
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenYorumlar = ds.getValue(ModelDetaylariData.Yorumlar::class.java)!!
                        yorumListesi.add(gelenYorumlar)
                    }
                    var yorumSayisi = yorumListesi.size
                    ref.child("tum_motorlar").child(model.toString()).child("model_yorum_sayisi").setValue(yorumSayisi)
                } else {
                    yorumListesi.add(ModelDetaylariData.Yorumlar("Admin", "İlk Yorumu Yapmak İster Misin?", 5f, 1, "123", model, "Admin"))
                }

                setupYorumlarRecyclerView()


            }
        })
    }

    private fun parcaVerileri() {
        var ref = FirebaseDatabase.getInstance().reference
        ref.child("tum_motorlar").child(model.toString()).child("yy_parcalar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                parcaListesi.clear()
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenYorumlar = ds.getValue(ModelDetaylariData.Parcalar::class.java)!!
                        parcaListesi.add(gelenYorumlar)
                    }
                } else {
                    parcaListesi.add(ModelDetaylariData.Parcalar("Parça İsmi", "İlk Parçayı Eklemek İster Misin?", "2020", "Admin", model, marka))

                }
                setupParcaRecyclerView()
                parcaAdapter.notifyDataSetChanged()


            }
        })
    }

    private fun yakitVerileri() {
        var ref = FirebaseDatabase.getInstance().reference

        //yakitVerileri
        ref.child("tum_motorlar").child(model.toString()).child("yy_yakit_verileri").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                yakitListesi.clear()
                var yakitOrtList = ArrayList<Double>()
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var yakitTukVerisi = ds.getValue(ModelDetaylariData.YakitTuketimi::class.java)!!
                        yakitListesi.add(yakitTukVerisi)
                        yakitOrtList.add(yakitTukVerisi.yakitTuk!!.toDouble())
                    }

                } else {
                    yakitListesi.add(ModelDetaylariData.YakitTuketimi(0.0, "Admin", "2020"))
                }
                setupYakitRecyclerView()
                yakitAdapter.notifyDataSetChanged()
                if (yakitOrtList.size > 0) {
                    var ortTuk = "${yakitOrtList.average().toDouble()} lt/100km"
                    ref.child("tum_motorlar").child(model.toString()).child("yakitTuk").setValue(ortTuk)
                }

            }
        })


    }

    private fun yildizVerisi() {
        var ref = FirebaseDatabase.getInstance().reference
        ref.child("tum_motorlar").child(model.toString()).child("yildizlar").addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onCancelled(p0: com.google.firebase.database.DatabaseError) {

            }

            override fun onDataChange(p0: com.google.firebase.database.DataSnapshot) {
                yildizListesi.clear()
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelenVeriler = ds.getValue(ModelDetaylariData.Yildizlar::class.java)!!
                        yildizListesi.add(gelenVeriler)
                    }
                    var form = DecimalFormat("0.0")
                    var ortalamaYildiz = yildizListesi.map { it -> it?.yildiz!! }.average()
                    tvYildizKisi.text = "(" + yildizListesi.size.toString() + ")"
                    rbMotor.rating = ortalamaYildiz.toFloat()
                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("ortYildiz").setValue(ortalamaYildiz)
                    Log.e("yildizlar: ", ortalamaYildiz.toString())
                }


            }
        })
    }


    //butontıklamalarının hepsi burada
    private fun init() {
        swipeRefreshLayout.setOnRefreshListener {
            verileriGetir()
            yorumVerileri()
            yakitVerileri()
            parcaVerileri()
            yildizVerisi()
            swipeRefreshLayout.isRefreshing = false
        }


        imgGeri.setOnClickListener {
            val intent = Intent(this, Motor2Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }

        var isOpen = false
        val fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        val rotateClockWise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise)
        val rotateAntiClockWise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise)

        floatingGenel.setOnClickListener {

            if (isOpen) {
                floatingYorumYap.startAnimation(fabClose)

                floatingYakitEkle.startAnimation(fabClose)
                floatingParcaEkle.startAnimation(fabClose)

                floatingGenel.startAnimation(rotateClockWise)


                isOpen = false
            } else {
                floatingYorumYap.startAnimation(fabOpen)
                floatingYakitEkle.startAnimation(fabOpen)
                floatingParcaEkle.startAnimation(fabOpen)

                floatingGenel.startAnimation(rotateAntiClockWise)


                floatingYorumYap.isClickable
                floatingYakitEkle.isClickable
                floatingParcaEkle.isClickable

                isOpen = true
            }


            floatingYorumYap.setOnClickListener {
                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                var inflater: LayoutInflater = layoutInflater
                var view: View = inflater.inflate(R.layout.dialog_yorum, null)

                builder.setView(view)
                var dialog: Dialog = builder.create()

                var rbYorumYap = 1f
                view.rbYorumYap.setOnRatingChangeListener { ratingBar, rating, fromUser ->
                    rbYorumYap = ratingBar!!.rating
                }

                view.tvIptal.setOnClickListener {
                    dialog!!.dismiss()
                }
                view.tvGonder.setOnClickListener {
                    var yorum = view.etYorum.text.toString()
                    if (yorum.length > 4) {

                        ref.child("tum_motorlar").child(model.toString()).child("yildizlar").child(userID.toString()).child("yildiz").setValue(rbYorumYap)

                        val key = ref.child("tum_motorlar").child(model.toString()).child("yorumlar").push().key
                        var yorumlar = ModelDetaylariData.Yorumlar(kullaniciAdi, yorum, rbYorumYap, null, key, model, userID)
                        ref.child("tum_motorlar").child(model.toString()).child("yorumlar").child(key.toString()).setValue(yorumlar)
                        ref.child("tum_motorlar").child(model.toString()).child("yorumlar").child(key.toString()).child("tarih").setValue(ServerValue.TIMESTAMP)
                        //+5 yorum puan ekleme

                        var sonYorum = YorumlarData(marka, model, kullaniciAdi, null, yorum)
                        ref.child("tum_motorlar").child("yorumlar_son").child(model.toString()).setValue(sonYorum)
                        ref.child("tum_motorlar").child("yorumlar_son").child(model.toString()).child("yorum_zaman").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {
                            Toast.makeText(this@ModelDetayiActivity, "Yorumun gönderildi", Toast.LENGTH_LONG).show()
                        }


                        var yeniPuan = kullaniciKendiPuan!! + 3
                        ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)
                        dialog.dismiss()
                        yorumVerileri()


                    } else {
                        Toast.makeText(this@ModelDetayiActivity, "Yorumun çok kısa değil mi? ", Toast.LENGTH_LONG).show()
                    }

                }
                dialog.show()
            }
            floatingParcaEkle.setOnClickListener {
                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                var inflater: LayoutInflater = layoutInflater
                var view: View = inflater.inflate(R.layout.dialog_parca_ekle, null)

                builder.setView(view)

                builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                    }

                })
                builder.setPositiveButton("Ekle", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                        if ((view.etParcaİsmi.text.isNullOrEmpty() || view.etParcaİsmi.text.isNullOrEmpty()) || view.etParcaİsmi.text.isNullOrEmpty()) {
                            Toast.makeText(this@ModelDetayiActivity, "Verilerde Hata Var", Toast.LENGTH_LONG).show()
                        } else {
                            var parcaIsmi = view.etParcaİsmi.text.toString()
                            var parcaModel = view.etParcaModelYili.text.toString()
                            var parcaYorum = view.etParcaUygunlugu.text.toString()

                            var parcaKey = FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").push().key
                            var parcaVerisi = ModelDetaylariData.Parcalar(parcaIsmi, parcaYorum, parcaModel, kullaniciAdi, parcaKey, marka, model)
                            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").child(parcaKey.toString()).setValue(parcaVerisi)

                            var yeniPuan = kullaniciKendiPuan!! + 10
                            ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)
                            parcaVerileri()
                            dialog!!.dismiss()
                        }
                    }
                })
                var dialog: Dialog = builder.create()
                dialog.show()
            }
            floatingYakitEkle.setOnClickListener {
                var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                var inflater: LayoutInflater = layoutInflater
                var view: View = inflater.inflate(R.layout.dialog_yakit_tuketim,null)

                builder.setView(view)


                builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                    }

                })
                builder.setPositiveButton("Gönder", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {

                        if (view.etYakitVerisi.text.toString().isNullOrEmpty() || view.etModelYili.text.toString().isNullOrEmpty()) {
                            Toast.makeText(this@ModelDetayiActivity, "Girdigin veride hata var", Toast.LENGTH_LONG).show()


                        } else {

                            var gelenYakit = view.etYakitVerisi.text.toString().toDouble()
                            var motorYili = view.etModelYili.text.toString()
                            var yakitVerisi = ModelDetaylariData.YakitTuketimi(gelenYakit, kullaniciAdi, motorYili)

                            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_yakit_verileri").child(kullaniciAdi.toString())
                                .setValue(yakitVerisi).addOnCompleteListener {
                                    yakitVerileri()
                                    dialog!!.dismiss()
                                }
                            var yeniPuan = kullaniciKendiPuan!! + 5
                            ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)

                        }

                    }
                })

                val dialog: Dialog = builder.create()
                dialog.show()
            }
/*
            val popup = PopupMenu(this, it)
            popup.inflate(R.menu.popup_detay_menu)
            popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when (it.itemId) {
                    R.id.popYorumYap -> {
                        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        var inflater: LayoutInflater = layoutInflater
                        var view: View = inflater.inflate(R.layout.dialog_yorum, null)

                        builder.setView(view)
                        var dialog: Dialog = builder.create()

                        var rbYorumYap = 1f
                        view.rbYorumYap.setOnRatingChangeListener { ratingBar, rating, fromUser ->
                            rbYorumYap = ratingBar!!.rating
                        }

                        view.tvIptal.setOnClickListener {
                            dialog!!.dismiss()
                        }
                        view.tvGonder.setOnClickListener {
                            var yorum = view.etYorum.text.toString()
                            if (yorum.length > 4) {

                                ref.child("tum_motorlar").child(model.toString()).child("yildizlar").child(userID.toString()).child("yildiz").setValue(rbYorumYap)

                                val key = ref.child("tum_motorlar").child(model.toString()).child("yorumlar").push().key
                                var yorumlar = ModelDetaylariData.Yorumlar(kullaniciAdi, yorum, rbYorumYap, null, key, model, userID)
                                ref.child("tum_motorlar").child(model.toString()).child("yorumlar").child(key.toString()).setValue(yorumlar)
                                ref.child("tum_motorlar").child(model.toString()).child("yorumlar").child(key.toString()).child("tarih").setValue(ServerValue.TIMESTAMP)
                                //+5 yorum puan ekleme

                                var sonYorum = YorumlarData(marka, model, kullaniciAdi, null, yorum)
                                ref.child("tum_motorlar").child("yorumlar_son").child(model.toString()).setValue(sonYorum)
                                ref.child("tum_motorlar").child("yorumlar_son").child(model.toString()).child("yorum_zaman").setValue(ServerValue.TIMESTAMP).addOnCompleteListener {
                                    Toast.makeText(this@ModelDetayiActivity, "Yorumun gönderildi", Toast.LENGTH_LONG).show()
                                }


                                var yeniPuan = kullaniciKendiPuan!! + 3
                                ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)
                                dialog.dismiss()
                                yorumVerileri()


                            } else {
                                Toast.makeText(this@ModelDetayiActivity, "Yorumun çok kısa değil mi? ", Toast.LENGTH_LONG).show()
                            }

                        }
                        dialog.show()


                    }

                    R.id.popParca -> {

                        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
                        var inflater: LayoutInflater = layoutInflater
                        var view: View = inflater.inflate(R.layout.dialog_parca_ekle, null)

                        builder.setView(view)

                        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog!!.dismiss()
                            }

                        })
                        builder.setPositiveButton("Ekle", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {

                                if ((view.etParcaİsmi.text.isNullOrEmpty() || view.etParcaİsmi.text.isNullOrEmpty()) || view.etParcaİsmi.text.isNullOrEmpty()) {
                                    Toast.makeText(this@ModelDetayiActivity, "Verilerde Hata Var", Toast.LENGTH_LONG).show()


                                } else {

                                    var parcaIsmi = view.etParcaİsmi.text.toString()
                                    var parcaModel = view.etParcaModelYili.text.toString()
                                    var parcaYorum = view.etParcaUygunlugu.text.toString()

                                    var parcaKey = FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").push().key
                                    var parcaVerisi = ModelDetaylariData.Parcalar(parcaIsmi, parcaYorum, parcaModel, kullaniciAdi, parcaKey, marka, model)
                                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").child(parcaKey.toString()).setValue(parcaVerisi)

                                    var yeniPuan = kullaniciKendiPuan!! + 10
                                    ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)
                                    parcaVerileri()
                                    dialog!!.dismiss()
                                }
                            }
                        })
                        var dialog: Dialog = builder.create()
                        dialog.show()
                    }

                    R.id.popYakitEkle -> {

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

                                if (view.etYakitVerisi.text.toString().isNullOrEmpty() || view.etModelYili.text.toString().isNullOrEmpty()) {
                                    Toast.makeText(this@ModelDetayiActivity, "Girdigin veride hata var", Toast.LENGTH_LONG).show()


                                } else {

                                    var gelenYakit = view.etYakitVerisi.text.toString().toFloat()
                                    var motorYili = view.etModelYili.text.toString()
                                    var yakitVerisi = ModelDetaylariData.YakitTuketimi(gelenYakit, kullaniciAdi, motorYili)

                                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_yakit_verileri").child(kullaniciAdi.toString())
                                        .setValue(yakitVerisi).addOnCompleteListener {
                                            yakitVerileri()
                                            dialog!!.dismiss()
                                        }
                                    var yeniPuan = kullaniciKendiPuan!! + 5
                                    ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)

                                }

                            }
                        })

                        val dialog: Dialog = builder.create()
                        dialog.show()


                    }
                }
                return@OnMenuItemClickListener true
            })
            popup.show()
*/

        }


    }


    fun setupYorumlarRecyclerView() {

        rcYorum.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, true)
        yorumAdapter = YorumAdapter(this@ModelDetayiActivity, yorumListesi, userID)
        rcYorum.adapter = yorumAdapter

    }

    fun setupParcaRecyclerView() {


        rcParca.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, true)
        parcaAdapter = ParcaAdapter(this@ModelDetayiActivity, parcaListesi, userID)
        rcParca.adapter = parcaAdapter


    }

    fun setupYakitRecyclerView() {


        rcYakit.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, false)
        yakitAdapter = YakitAdapter(this@ModelDetayiActivity, yakitListesi, userID)
        rcYakit.adapter = yakitAdapter


    }


    override fun onStart() {
        super.onStart()
        yorumVerileri()

    }


}
