package com.creativeoffice.motosp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ParcaAdapter
import com.creativeoffice.motosp.Adapter.YorumAdapter
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.Users
import com.creativeoffice.motosp.ParcaEkleActivity
import com.creativeoffice.motosp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import kotlinx.android.synthetic.main.activity_model_detayi.*


class ModelDetayiActivity : AppCompatActivity() {

    var marka: String? = null
    var model: String? = null
    var userID: String? = null
    lateinit var yorumAdapter: YorumAdapter
    lateinit var parcaAdapter: ParcaAdapter

    var parcaListesi = ArrayList<ModelDetaylariData.Parcalar>()
    var yorumListesi = ArrayList<ModelDetaylariData.Yorumlar>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_model_detayi)

        userID = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference
        setupYorumlarRecyclerView()
        init(ref)
        verileriGetir()


    }


    //butontıklamalarının hepsi burada
    private fun init(ref: DatabaseReference) {
        //   yorumGonderBtn.visibility = View.GONE
        //  etYorum.visibility = View.GONE
        tvParcaEkle.visibility = View.GONE
        etYorum.addTextChangedListener(watcher)
        imgYorum.setBackgroundResource(R.drawable.ic_yorum_mavi)



        yorumGonderBtn.setOnClickListener {

            ref.child("users").child(userID.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    val gelenUsers = p0.getValue(Users::class.java)
                    var kullaniciAdi = gelenUsers!!.user_name.toString()
                    val yorum = etYorum.text.toString()

                    //Önce key oluşturduk
                    val key = ref.child("tum_motorlar").child(model.toString()).child("yorumlar").push().key

                    var yorumlar = ModelDetaylariData.Yorumlar(kullaniciAdi, yorum, null, key, model, userID)

                    //Bu keyin altına yorumu yazdık
                    ref.child("tum_motorlar").child(model.toString()).child("yorumlar")
                        .child(key.toString()).setValue(yorumlar)

                    //Bu yorumun içine server zamanını yazdık
                    ref.child("tum_motorlar").child(model.toString()).child("yorumlar")
                        .child(key.toString()).child("tarih").setValue(ServerValue.TIMESTAMP)


                    //+5 yorum puan ekleme
                    var eskiPuan = gelenUsers.user_details!!.puan!!.toInt()
                    var yeniPuan = eskiPuan + 5
                    ref.child("users").child(userID.toString()).child("user_details").child("puan").setValue(yeniPuan)


                    etYorum.text.clear()
                    setupYorumlarRecyclerView()

                }

            })


        }
        imgYorum.setOnClickListener {
            imgYorum.setBackgroundResource(R.drawable.ic_yorum_mavi)
            imgYedek.setBackgroundResource(R.drawable.ic_yedekparca)
            etYorum.text.clear()
            yorumGonderBtn.setBackgroundResource(R.drawable.ic_send)
            etYorum.visibility = View.VISIBLE
            yorumGonderBtn.visibility = View.VISIBLE
            tvParcaEkle.visibility = View.GONE

            setupYorumlarRecyclerView()
        }
        imgYedek.setOnClickListener {
            imgYedek.setBackgroundResource(R.drawable.ic_yedekparca_mavi)
            imgYorum.setBackgroundResource(R.drawable.ic_yorum)
            etYorum.visibility = View.GONE
            yorumGonderBtn.visibility = View.GONE
            tvParcaEkle.visibility = View.VISIBLE

            setupParcalarRecyclerView()
        }

        tvParcaEkle.setOnClickListener {
            val intent = Intent(this, ParcaEkleActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            intent.putExtra("Model", model)
            intent.putExtra("Marka", marka)

            startActivity(intent)

        }
    }




    fun setupYorumlarRecyclerView() {
        FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val model = p0.getValue(ModelDetaylariData::class.java) ?: return //Cok onemli

                //yorumları alıp recyclerviewde gösterdik
                val yorumHashMap = model.yorumlar ?: return
                yorumListesi = ArrayList<ModelDetaylariData.Yorumlar>()
                for (i in yorumHashMap.values) {
                    yorumListesi.add(i)
                }
                yorumListesi.sortBy { it.tarih } //sortby tarihe göre sıralar
                rcYorumlar.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, true)
                yorumAdapter = YorumAdapter(this@ModelDetayiActivity, yorumListesi, userID)
                yorumAdapter.notifyDataSetChanged()
                rcYorumlar.setItemViewCacheSize(20)
                rcYorumlar.setHasFixedSize(true)
                rcYorumlar.adapter = yorumAdapter
                rcYorumlar.refreshDrawableState()

            }


        })


    }

    fun setupParcalarRecyclerView() {
        FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val model = p0.getValue(ModelDetaylariData::class.java) ?: return //Cok onemli

                //yorumları alıp recyclerviewde gösterdik
                val parcaHashMap = model.yy_parcalar ?: return
                parcaListesi = ArrayList<ModelDetaylariData.Parcalar>()
                for (i in parcaHashMap.values) {
                    parcaListesi.add(i)
                }

                rcYorumlar.layoutManager = LinearLayoutManager(this@ModelDetayiActivity, LinearLayoutManager.VERTICAL, true)

                parcaAdapter = ParcaAdapter(this@ModelDetayiActivity, parcaListesi, userID)
                parcaAdapter.notifyDataSetChanged()

                rcYorumlar.setHasFixedSize(true)
                rcYorumlar.adapter = parcaAdapter
                rcYorumlar.refreshDrawableState()

            }


        })


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
        setupYorumlarRecyclerView()
    }
    private var watcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (etYorum.text.toString().length >= 4) {
                yorumGonderBtn.isEnabled = true
                yorumGonderBtn.setBackgroundResource(R.drawable.ic_sendmavi)


            } else {
                yorumGonderBtn.isEnabled = false
                yorumGonderBtn.setBackgroundResource(R.drawable.ic_send)
            }

        }

    }
}
