package com.creativeoffice.motosp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_parca_ekle.*

class ParcaEkleActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    var kullaniciAdi: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parca_ekle)
        mAuth = FirebaseAuth.getInstance()
        var userID = mAuth.currentUser!!.uid

        var model = intent.getStringExtra("Model")
        var marka = intent.getStringExtra("Marka")



        tvMarka.text = marka
        tvModel.text = model





        btnKaydet.setOnClickListener {
            var parcaIsmi = etParcaİsmi.text.toString()
            var parcaModel = etParcaModelYili.text.toString()
            var parcaUygun = etParcaUygunlugu.text.toString()
            FirebaseDatabase.getInstance().reference.child("users").child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    kullaniciAdi = p0.child("user_name").value.toString()
                    var parcaVerisi = ModelDetaylariData.Parcalar(parcaIsmi, parcaModel, parcaUygun, kullaniciAdi)
                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").push().setValue(parcaVerisi)
                }

            }
            )

        }

    }
}
