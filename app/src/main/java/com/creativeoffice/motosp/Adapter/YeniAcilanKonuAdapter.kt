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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_forum_yeni_konular.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class YeniAcilanKonuAdapter(val myContext: Context, val yeniKonuList: ArrayList<ForumKonuData>) : RecyclerView.Adapter<YeniAcilanKonuAdapter.YeniKonuHolder>() {

    var ref = FirebaseDatabase.getInstance().reference
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

            val intent = Intent(myContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("konuBasligi", gelenItem.konu_basligi.toString())
            intent.putExtra("konuCevabi", gelenItem.konu_sahibi_cevap.toString())
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

            ref.child("users").child(gelenItemVerisi.konuyu_acan_key.toString()).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    p0?.let {
                        konuyuAcan.text = p0.value.toString()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, d MMM", Locale("tr"))
            return sdf.format(date)

        }

    }
}