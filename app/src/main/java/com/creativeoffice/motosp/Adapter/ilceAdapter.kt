package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_bayi_ilce.view.*

class ilceAdapter(val myContext: Context, val ilceList: ArrayList<BayilerData.ilcelerData>) : RecyclerView.Adapter<ilceAdapter.IlceHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): IlceHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_bayi_ilce, p0, false)

        view.rcBayiler.visibility = View.GONE
        return IlceHolder(view)
    }

    override fun getItemCount(): Int {

        return ilceList.size
    }

    override fun onBindViewHolder(holder: IlceHolder, position: Int) {
        holder.setData(ilceList[position], myContext)

        var bayiList = ArrayList<BayilerData.BayiDetaylari>()
        holder.initVeri(bayiList)
        holder.setupBayiRecyclerView(bayiList)


        var ilkTik = false

        holder.tumLayoutIlce.setOnClickListener {

            //  holder.rcBayiler.visibility = View.VISIBLE
            if (ilkTik == false) {

                ilkTik = true


                holder.rcBayiler.visibility = View.VISIBLE
            } else {
                ilkTik = false


                holder.rcBayiler.visibility = View.GONE
            }

        }


    }


    inner class IlceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tumLayoutIlce = itemView.clTumLayoutIlce
        var ilceIsmi = itemView.tvIlceAdi
        var rcBayiler = itemView.rcBayiler


        fun setData(oankiSehir: BayilerData.ilcelerData, myContext: Context) {

            ilceIsmi.text = oankiSehir.ilceAdi


        }

        fun initVeri(bayiList: ArrayList<BayilerData.BayiDetaylari>) {
            var ref = FirebaseDatabase.getInstance().reference
            ref.child("Bayiler").child(ilceList[position].sehirAdi.toString()).child(ilceList[position].ilceAdi.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.hasChildren()) {


                        for (bayiler in p0.children) {
                            var gelendata = bayiler.getValue(BayilerData.BayiDetaylari::class.java)!!
                            bayiList.add(gelendata)
                        }


                    }
                }
            })


        }

        fun setupBayiRecyclerView(bayiList: ArrayList<BayilerData.BayiDetaylari>) {

            rcBayiler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            // rcBayiler.layoutManager = LinearLayoutManager(myContext, LinearLayoutManager.VERTICAL, false)
            val markaAdapter = BayiAdapter(myContext, bayiList)
            rcBayiler.adapter = markaAdapter
            rcBayiler.setItemViewCacheSize(20)


        }

    }


}
