package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater

import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R

import kotlinx.android.synthetic.main.bayi_item_ilce_bayiler.view.*
import kotlinx.android.synthetic.main.bayi_item_ilce_bayiler.view.tvBayiAdi
import kotlinx.android.synthetic.main.dialog_bayi_detay.view.*

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

            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)

            var view: View = inflate(myContext, R.layout.dialog_bayi_detay, null)

            view.tvBayiAdi.text = bayilerList[position].bayiAdi
            view.tvAdres.setText("Adres: " + bayilerList[position].adres)
            view.tvNumara.setText("Numara: " + bayilerList[position].numara)

            view.tvNumara.setOnClickListener {

                val arama = Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                arama.data = Uri.parse("tel:" + bayilerList[position].numara.toString())
                myContext.startActivity(arama)

            }
            view.tvAra.setOnClickListener {
                val arama = Intent(Intent.ACTION_DIAL)//Bu kod satırımız bizi rehbere telefon numarası ile yönlendiri.
                arama.data = Uri.parse("tel:" + bayilerList[position].numara.toString())
                myContext.startActivity(arama)
            }
            view.tvAdreseGit.setOnClickListener {
                val adres = bayilerList[position].adres
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=an " + adres.toString())

                )
                myContext.startActivity(intent)
            }

            view.tvAdres.setOnClickListener {
                val adres = bayilerList[position].adres
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=an " + adres.toString())

                )

                myContext.startActivity(intent)
            }


            builder.setView(view)


            var dialog: Dialog = builder.create()
            dialog.show()

        }
    }


    inner class BayilerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tumLayoutBayi = itemView.clBayi
        var bayiIsmi = itemView.tvBayiAdi
        // var rcIlceler = itemView.rcIlceler


        fun setData(oankiSehir: BayilerData.BayiDetaylari, myContext: Context) {

            bayiIsmi.text = oankiSehir.bayiAdi


        }

    }
}


