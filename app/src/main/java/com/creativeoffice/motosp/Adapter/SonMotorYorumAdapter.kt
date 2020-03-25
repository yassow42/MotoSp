package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.ModelDetayiActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.YorumlarData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.item_forum_konu_basliklari.view.*
import kotlinx.android.synthetic.main.item_son_model_mesajlari.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SonMotorYorumAdapter(val myContext: Context, val sonYorumlar: ArrayList<YorumlarData>, val tumModeller: ArrayList<ModelDetaylariData>) : RecyclerView.Adapter<SonMotorYorumAdapter.YorumHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YorumHolder {

        return YorumHolder(LayoutInflater.from(myContext).inflate(R.layout.item_son_model_mesajlari, p0, false))
    }

    override fun getItemCount(): Int {
        return sonYorumlar.size
    }

    override fun onBindViewHolder(p0: YorumHolder, p1: Int) {
        val currentItem = sonYorumlar.get(p1)

        p0.setData(currentItem)

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


            myContext.startActivity(intent)


        }

    }

    inner class YorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var marka = itemView.tvMarka
        var model = itemView.tvModel
        var sonYazanZaman = itemView.tvSonYazanTarih
        var yorum = itemView.tvSonMesaj
        var tumLayout = itemView.tumLayout


        fun setData(currentItem: YorumlarData) {

            marka.text = currentItem.yorum_marka
            model.text = currentItem.yorum_model
            yorum.text = currentItem.yorum
            sonYazanZaman.text = "Son yazan: " + currentItem.yorum_yapan + "  " + formatDate(currentItem.yorum_zaman).toString()

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)

        }


    }


}