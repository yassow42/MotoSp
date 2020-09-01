package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings.System.DATE_FORMAT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.EtkinlikDetayActivity
import com.creativeoffice.motosp.Datalar.EtkinlikData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.item_etkinlikler.view.*
import java.text.SimpleDateFormat
import java.util.*


class EtkinliklerAdapter(val myContext: Context, val etkinliklerList: ArrayList<EtkinlikData>) : RecyclerView.Adapter<EtkinliklerAdapter.MainHolder>() {

    init {


    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): EtkinliklerAdapter.MainHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_etkinlikler, p0, false)


        return MainHolder(view)
    }


    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val item = etkinliklerList[position]


        holder.setData(myContext, item)

        holder.itemView.setOnClickListener {

            val intent = Intent(myContext, EtkinlikDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("etkinlik_adi", item.etkinlik_adı)

            myContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return etkinliklerList.size
    }

    inner class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val etkinlikAdi = itemView.tvEtkinlikAdi
        val etkinlikSehir = itemView.tvEtkinlikSehir
        val etkinlikZaman = itemView.tvEtkinlikZamani
        val etkinlikZamanSaat = itemView.tvEtkinlikZamaniSaat
        val countDown = itemView.tvCountDown
        val etkinlikKatilimci = itemView.tvKatilimciSayisi

        fun setData(myContext: Context, item: EtkinlikData) {
            etkinlikAdi.text = item.etkinlik_adı
            etkinlikSehir.text = "Şehir: " + item.etkinlik_sehir.toString()
            etkinlikZaman.text = "Etkinlik Zamanı: " + formatDate(item.etkinlik_zamani).toString()
            etkinlikZamanSaat.text = "Etkinlik Saati: " + formatDateSaat(item.etkinlik_zamani).toString()
            etkinlikKatilimci.text = "Katılımcı Sayısı: " + item.etkinlik_katilimci_sayisi.toString() + "/23"
            var zamanFarki: Long? = null
            item.etkinlik_zamani?.let {

                zamanFarki = it - Calendar.getInstance().timeInMillis
                object : CountDownTimer(zamanFarki!!.toLong(), 1000) {   //Milisaniye cinsi
                    override fun onFinish() {
                        countDown.text = "Tamamlandı"
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        val gün = millisUntilFinished / 86400000
                        countDown.text = "$gün Gün Kaldı"
                    }
                }.start()
            }


        }


        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, dd MMM", Locale("tr"))
            return sdf.format(date)
        }

        fun formatDateSaat(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("HH:mm", Locale("tr"))
            return sdf.format(date)
        }

    }


}