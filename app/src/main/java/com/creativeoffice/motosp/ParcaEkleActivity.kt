package com.creativeoffice.motosp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_parca_ekle.*

class ParcaEkleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parca_ekle)

        var model = intent.getStringExtra("Model")
        var marka = intent.getStringExtra("Marka")

        tvMarka.text = marka
        tvModel.text = model





        btnKaydet.setOnClickListener {
            var parcaIsmi = etParcaİsmi.text.toString()
            var parcaModel = etParcaModelYili.text.toString()
            var parcaUygun = etParcaUygunlugu.text.toString()

            var parcaVerisi = ModelDetaylariData.Parcalar(parcaIsmi, parcaModel, parcaUygun)
            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(model.toString()).child("yy_parcalar").push().setValue(parcaVerisi)
        }

    }
}
