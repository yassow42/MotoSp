package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.parca_item.view.*

class ParcaAdapter(val myContext: Context, val parcalar: ArrayList<ModelDetaylariData.Parcalar>, val userID: String? = null) : RecyclerView.Adapter<ParcaAdapter.ParcaHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ParcaHolder {

        return ParcaHolder(LayoutInflater.from(myContext).inflate(R.layout.parca_item, p0, false))
    }

    override fun getItemCount(): Int {
        return parcalar.size
    }

    override fun onBindViewHolder(p0: ParcaHolder, p1: Int) {

        p0.setData(parcalar.get(p1), myContext)




    }

    inner class ParcaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parcaIsmi = itemView.tvParcaismi
        val parcaUygunlugu = itemView.tvParcaUygunlugu
        val parcaModelYili = itemView.tvParcaModelYili


        fun setData(oAnkiParca: ModelDetaylariData.Parcalar, myContext: Context) {
            parcaIsmi.text = oAnkiParca.parca_ismi
            parcaUygunlugu.text = oAnkiParca.parca_model_uyumu
            parcaModelYili.text = oAnkiParca.parca_uyum_model_yili

        }

    }


}