package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arindicatorview.ARIndicatorView
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_bayi.view.*

class SehirAdapter(val myContext: Context, val sehirList: ArrayList<BayilerData>) : RecyclerView.Adapter<SehirAdapter.SehirHolder>() {

    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): SehirHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_bayi, p0, false)

        //     view.rcIlceler.visibility = View.GONE
        //  view.tvDetayGizle.visibility = View.GONE

        return SehirHolder(view)
    }

    override fun getItemCount(): Int {

        return sehirList.size
    }

    override fun onBindViewHolder(p0: SehirHolder, position: Int) {
        p0.setData(sehirList[position], myContext)

        ref.child("Bayiler").child(sehirList[position].sehirAdi.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var ilceList = ArrayList<BayilerData.ilcelerData>()

                for (ilceler in p0.children) {
                    var gelendata = BayilerData.ilcelerData(sehirList[position].sehirAdi, ilceler.key)
                    ilceList.add(gelendata)
                }

                setupIlceRecyclerView(ilceList)

            }

            private fun setupIlceRecyclerView(ilceList: ArrayList<BayilerData.ilcelerData>) {

                //    p0.rcIlceler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
                p0.rcIlceler.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.HORIZONTAL, false)

                val markaAdapter = ilceAdapter(myContext, ilceList)
                p0.rcIlceler.adapter = markaAdapter
                p0.rcIlceler.setHasFixedSize(false)
                p0.rcIlceler.setItemViewCacheSize(50)
                p0.arIndicator.removeIndicators()

                p0.rcIlceler.setOnFlingListener(null)
                p0.arIndicator.attachTo(p0.rcIlceler, true)
                p0.arIndicator.isScrubbingEnabled = true


            }


        })
/*
        p0.tumLayoutSehir.setOnClickListener {

            p0.rcIlceler.visibility = View.VISIBLE
            p0.tvDetayGizle.visibility = View.VISIBLE
            p0.tvDetayGoster.visibility = View.GONE


        }

        p0.tvDetayGizle.setOnClickListener {
            p0.rcIlceler.visibility = View.GONE
            p0.tvDetayGizle.visibility = View.GONE
            p0.tvDetayGoster.visibility = View.VISIBLE

        }
*/

    }

    inner class SehirHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tumLayoutSehir = itemView.clSehir

        var sehirİsmi = itemView.tvSehirAdi

        //  var tvDetayGoster = itemView.tvDetay
        //    var tvDetayGizle = itemView.tvDetayGizle
        var rcIlceler = itemView.rcIlceler
        var arIndicator = itemView.ar_indicator_ilce




        fun setData(oankiSehir: BayilerData, myContext: Context) {
            sehirİsmi.text = oankiSehir.sehirAdi


        }

    }


}