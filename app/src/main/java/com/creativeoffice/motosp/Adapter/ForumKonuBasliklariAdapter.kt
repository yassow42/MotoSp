package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_forum_konu_basliklari.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.database.ValueEventListener as ValueEventListener1

class ForumKonuBasliklariAdapter(val myContext: Context, val konuList: ArrayList<ForumKonuData>) : RecyclerView.Adapter<ForumKonuBasliklariAdapter.ForumKonuBasligiHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ForumKonuBasligiHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_forum_konu_basliklari, p0, false)
        return ForumKonuBasligiHolder(view)
    }

    override fun getItemCount(): Int {

        return konuList.size
    }

    override fun onBindViewHolder(holder: ForumKonuBasligiHolder, position: Int) {

        var gelenItem = konuList[position]
        holder.tumLayout.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))

        holder.setData(konuList[position], myContext)
        holder.tumLayout.setOnClickListener {

            val intent = Intent(myContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("konuBasligi", gelenItem.konu_basligi.toString())
            intent.putExtra("userName", gelenItem.konuyu_acan.toString())
         //   intent.putExtra("tarih", holder.formatDate(gelenItem.acilma_zamani).toString())
            intent.putExtra("konuKey", gelenItem.konu_key)
            intent.putExtra("konuyu_acan_key", gelenItem.konuyu_acan_key)


            myContext.startActivity(intent)

        }


    }


    inner class ForumKonuBasligiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var konuBasligi = itemView.tvKonuBasligi
        var userName = itemView.tvUserName
        var tarih = itemView.tvZaman
        var tvZamanCevap = itemView.tvZamanCevap
        var tvSonYazan = itemView.tvSonYazan
        var tumLayout = itemView.clKonuBasliklari
        var cevapSayisi = itemView.tvCevapSayisi



        fun setData(forumKonuData: ForumKonuData, myContext: Context) {
            konuBasligi.text = forumKonuData.konu_basligi
            userName.text = "Konuyu açan " + forumKonuData.konuyu_acan
            tarih.text = formatDate(forumKonuData.acilma_zamani).toString()


            Log.e("sad",forumKonuData.konuyu_acan)
            //foruma soncevap yazan verisi
            FirebaseDatabase.getInstance().reference.child("Forum").child(forumKonuData.konu_key.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener1 {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChildren()) {
                            val gelenData = p0.child("son_cevap").getValue(ForumKonuData.son_cevap::class.java)!!
                            var cevapZamani = gelenData.cevap_zamani
                            tvZamanCevap.text = formatDate(cevapZamani).toString()
                            tvSonYazan.text = gelenData.cevap_yazan

                           var sayi = p0.child("cevaplar").childrenCount
                            cevapSayisi.text = "Cevaplar: " +sayi.toString()


                        } else {
                            tvZamanCevap.text = ""
                            tvSonYazan.text = ""
                        }


                    }


                })


        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat(" d MMM hh:mm ", Locale("tr"))
            return sdf.format(date)

        }

    }


}