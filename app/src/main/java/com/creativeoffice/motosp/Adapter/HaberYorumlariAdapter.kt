package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.item_yorum.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HaberYorumlariAdapter (val myContext: Context, val yorumlar: ArrayList<HaberlerData.Yorumlar>, val userID: String? = null) : RecyclerView.Adapter<HaberYorumlariAdapter.YorumHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YorumHolder {

        return YorumHolder(LayoutInflater.from(myContext).inflate(R.layout.item_yorum, p0, false))
    }

    override fun getItemCount(): Int {
        return yorumlar.size
    }

    override fun onBindViewHolder(holder: YorumHolder, position: Int) {
        val currentItem = yorumlar.get(position)

        holder.setData(currentItem)
       holder.yorumCL.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))




    }

    inner class YorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.userName
        val comment = itemView.comment
        val date = itemView.date
        val yorumCL = itemView.yorumCL

        fun setData(oAnkiVeri: HaberlerData.Yorumlar) {
            userName.text = oAnkiVeri.isim.toString()
            comment.text = oAnkiVeri.yorum.toString()
            date.text = formatDate(oAnkiVeri.tarih).toString()
        }


        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, d MMM, ''yy", Locale("tr"))
            return sdf.format(date)

        }



    }



}