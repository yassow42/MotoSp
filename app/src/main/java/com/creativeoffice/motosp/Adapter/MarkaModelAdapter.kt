package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.creativeoffice.motosp.Activity.ModelDetayiActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.tek_model_list.view.*
import java.io.IOException


class MarkaModelAdapter(val myContext: Context, val tumModeller: ArrayList<ModelDetaylariData>) : RecyclerView.Adapter<MarkaModelAdapter.MyViewHolder>() {

    var ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        var viewHolder = LayoutInflater.from(myContext).inflate(R.layout.tek_model_list, p0, false)

      //  viewHolder.scrollView.visibility = View.GONE


        return MyViewHolder(viewHolder, myContext)
    }

    override fun getItemCount(): Int {
        return tumModeller.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

        try {
            p0.tumLayout.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.scale))

            //     p0.tvModel.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.scale))
            //   p0.imgMotoripi.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sol))

            p0.setData(tumModeller.get(p1), myContext)

            p0.tumLayout.setOnClickListener {
                val intent = Intent(myContext, ModelDetayiActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                intent.putExtra("Marka", tumModeller.get(p1).marka.toString())
                intent.putExtra("Model", tumModeller.get(p1).model.toString())
                intent.putExtra("Kategori", tumModeller.get(p1).kategori.toString())
                intent.putExtra("Silindir", tumModeller.get(p1).silindirHacmi.toString())
                intent.putExtra("Beygir", tumModeller.get(p1).beygir.toString())
                intent.putExtra("Hiz", tumModeller.get(p1).hiz.toString())
                intent.putExtra("Tork", tumModeller.get(p1).tork.toString())
                intent.putExtra("Devir", tumModeller.get(p1).devir.toString())
                intent.putExtra("Agirlik", tumModeller.get(p1).agirlik.toString())
                intent.putExtra("YakitKap", tumModeller.get(p1).yakitkap.toString())
                intent.putExtra("YakitTuk", tumModeller.get(p1).yakitTuk.toString())
                intent.putExtra("tanitim", tumModeller.get(p1).tanitim.toString())
                intent.putExtra("video", tumModeller.get(p1).motorVideo.toString())
                intent.putExtra("fiyat", tumModeller.get(p1).fiyat.toString())

                myContext.startActivity(intent)


            }

        } catch (e: IOException) {
            ref.child("Hatalar/MarkaModelAdapterHatası").push().setValue(e.message.toString())
        }


    }

    class MyViewHolder(viewHolder: View?, myContext: Context) : RecyclerView.ViewHolder(viewHolder!!) {
        var tumLayout = viewHolder as ConstraintLayout
        var tvMarka = tumLayout.tvMarka
        var tvModel = tumLayout.tvModel
        var tvFiyat = tumLayout.tvFiyat
        var rbYildiz = tumLayout.rbMotorList
      //  var scrollView = tumLayout.scrollView



        var detay_silindir = tumLayout.detay_silindirhacmi
        var detay_beygir = tumLayout.detay_beygir
        var detay_hiz = tumLayout.detay_hiz
        var detay_tork = tumLayout.detay_tork
        var imgMotorTipi = tumLayout.imgMotorKategori
        var tvKategori = tumLayout.tvKategori
        var imgMarka = tumLayout.imgMarka


        fun setData(oAnkiModel: ModelDetaylariData, myContext: Context) {
            setupModelFotolari(oAnkiModel)
            setupModelYazilari(oAnkiModel)


            if (oAnkiModel.fiyat.toString() == "1") {
                //   FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(oAnkiModel.model.toString()).child("fiyat").setValue("1")
                tvFiyat.visibility = View.INVISIBLE
            } else tvFiyat.text = "₺  " + oAnkiModel.fiyat


        }

        private fun setupModelYazilari(oAnkiModel: ModelDetaylariData) {
            tvMarka.text = oAnkiModel.marka.toString()
            tvModel.text = oAnkiModel.model.toString()



            detay_silindir.text = oAnkiModel.silindirHacmi
            detay_beygir.text = oAnkiModel.beygir
            detay_hiz.text = oAnkiModel.hiz
            detay_tork.text = oAnkiModel.tork

            oAnkiModel.ortYildiz?.let {
                rbYildiz.rating = it.toFloat()
            }


        }

        private fun setupModelFotolari(oAnkiModel: ModelDetaylariData) {
            oAnkiModel.kategori?.let {
                tvKategori.text = it
                if (oAnkiModel.kategori == "Scooter") {
                    imgMotorTipi.setBackgroundResource(R.drawable.ic_scooter)

                } else if (oAnkiModel.kategori == "Sport" || oAnkiModel.kategori == "Racing") {

                    imgMotorTipi.setBackgroundResource(R.drawable.ic_sport)
                } else if (oAnkiModel.kategori == "Touring" || oAnkiModel.kategori == "Enduro" || oAnkiModel.kategori == "Adventure") {
                    imgMotorTipi.setBackgroundResource(R.drawable.ic_touring)
                } else if (oAnkiModel.kategori == "Cross") {
                    imgMotorTipi.setBackgroundResource(R.drawable.ic_cross)
                } else if (oAnkiModel.kategori == "Naked") {
                    imgMotorTipi.setBackgroundResource(R.drawable.ic_naked)
                } else if (oAnkiModel.kategori == "Chopper") {
                    imgMotorTipi.setBackgroundResource(R.drawable.ic_chopper)
                }

            }



            if (oAnkiModel.marka == "Honda") {
                imgMarka.setBackgroundResource(R.drawable.ic_honda)
            } else if (oAnkiModel.marka == "Kawasaki") {
                imgMarka.setBackgroundResource(R.drawable.kawasaki)
            } else if (oAnkiModel.marka == "Yamaha") {
                imgMarka.setBackgroundResource(R.drawable.yamaha)
            } else if (oAnkiModel.marka == "Suzuki") {
                imgMarka.setBackgroundResource(R.drawable.suzuki)
            } else if (oAnkiModel.marka == "Triumph") {
                imgMarka.setBackgroundResource(R.drawable.ic_triumph_background)
            }else if (oAnkiModel.marka == "Bmw") {
                imgMarka.setBackgroundResource(R.drawable.bmw)
            }
        }


    }


}