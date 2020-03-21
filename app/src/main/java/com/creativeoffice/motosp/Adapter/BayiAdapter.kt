package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R

import kotlinx.android.synthetic.main.bayi_item_ilce_bayiler.view.*

class BayiAdapter(val myContext: Context, val bayilerList: ArrayList<BayilerData.BayiDetaylari>) : RecyclerView.Adapter<BayiAdapter.BayilerHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): BayilerHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.bayi_item_ilce_bayiler, p0, false)

        // view.rcIlceler.visibility = View.GONE
        return BayilerHolder(view)
    }

    override fun getItemCount(): Int {

        return bayilerList.size
    }

    override fun onBindViewHolder(holder: BayilerHolder, position: Int) {
        holder.setData(bayilerList[position], myContext)



        holder.tumLayoutBayi.setOnClickListener {


        }


    }


    inner class BayilerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tumLayoutBayi = itemView.clBayi
        var bayiİsmi = itemView.tvBayiAdi
        // var rcIlceler = itemView.rcIlceler


        fun setData(oankiSehir: BayilerData.BayiDetaylari, myContext: Context) {

            bayiİsmi.text = oankiSehir.bayiAdi


        }

    }
}


