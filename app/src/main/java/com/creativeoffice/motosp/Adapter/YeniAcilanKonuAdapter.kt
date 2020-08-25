package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.item_forum_yeni_konular.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class YeniAcilanKonuAdapter(val myContext: Context, val yeniKonuList: ArrayList<ForumKonuData>) : RecyclerView.Adapter<YeniAcilanKonuAdapter.YeniKonuHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YeniKonuHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_forum_yeni_konular, p0, false)




        return YeniKonuHolder(view)
    }

    override fun getItemCount(): Int {

        return yeniKonuList.size
    }

    override fun onBindViewHolder(holder: YeniKonuHolder, position: Int) {

        var gelenItem = yeniKonuList[position]

        holder.setData(gelenItem, myContext)
        holder.tumLayout.setOnClickListener {

            val intent = Intent(myContext, KonuDetayActivity::class.java)
            intent.putExtra("konuBasligi", gelenItem.konu_basligi.toString())
            intent.putExtra("konuCevabi", gelenItem.konu_sahibi_cevap.toString())
            intent.putExtra("userName", gelenItem.konuyu_acan.toString())
            intent.putExtra("tarih", holder.formatDate(gelenItem.acilma_zamani).toString())
            intent.putExtra("konuKey", gelenItem.konu_key)
            intent.putExtra("konuyu_acan_key", gelenItem.konuyu_acan_key)

            myContext.startActivity(intent)

        }

    }


    inner class YeniKonuHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var konuBasligi = itemView.tvKonuBasligi
        var acilmaZamani = itemView.tvAcilmaZamani
        var konuyuAcan = itemView.tvKonuyuAcan
        var tumLayout = itemView.clYeniKonular

        fun setData(gelenItemVerisi: ForumKonuData, myContext: Context) {

            konuBasligi.text = gelenItemVerisi.konu_basligi
            acilmaZamani.text = formatDate(gelenItemVerisi.acilma_zamani).toString()
            konuyuAcan.text = gelenItemVerisi.konuyu_acan

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, d MMM", Locale("tr"))
            return sdf.format(date)

        }

    }
}