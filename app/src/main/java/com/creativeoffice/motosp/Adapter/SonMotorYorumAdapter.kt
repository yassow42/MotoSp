package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.ModelDetayiActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.YorumlarData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.item_son_model_mesajlari.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SonMotorYorumAdapter(val myContext: Context, val yorumlar: ArrayList<YorumlarData>, val tumModeller: ArrayList<ModelDetaylariData>) : RecyclerView.Adapter<SonMotorYorumAdapter.YorumHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YorumHolder {

        return YorumHolder(LayoutInflater.from(myContext).inflate(R.layout.item_son_model_mesajlari, p0, false))
    }

    override fun getItemCount(): Int {
        return yorumlar.size
    }

    override fun onBindViewHolder(holder: YorumHolder, position: Int) {

        val yorumData = yorumlar.get(position)
        val modelData = tumModeller.get(position)
        holder.setData(yorumData, modelData)
        holder.yorumCL.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))

        holder.itemView.setOnClickListener {

            val intent = Intent(myContext, ModelDetayiActivity::class.java)
            intent.putExtra("Marka", yorumData.yorum_marka)
            intent.putExtra("Model", yorumData.yorum_model)

            myContext.startActivity(intent)
        }


    }

    inner class YorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val marka = itemView.tvMarka
        val model = itemView.tvModel
        val mesaj = itemView.tvSonMesaj
        val date = itemView.tvSonYazanTarih

        val yorumCL = itemView.tumLayout


        fun setData(yorumData: YorumlarData, modelData: ModelDetaylariData) {

            marka.text = yorumData.yorum_marka
            model.text = yorumData.yorum_model
            mesaj.text = yorumData.yorum

            date.text = formatDate(yorumData.yorum_zaman).toString()

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, MMM d, ''yy", Locale("tr"))
            return sdf.format(date)

        }


    }


}