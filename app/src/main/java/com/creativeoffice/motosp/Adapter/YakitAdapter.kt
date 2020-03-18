package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.item_yakit_tuk.view.*

class YakitAdapter(val myContext: Context, val yakitVerileri: ArrayList<ModelDetaylariData.YakitTuketimi>, val userID: String? = null) : RecyclerView.Adapter<YakitAdapter.YakitHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YakitHolder {

        return YakitHolder(LayoutInflater.from(myContext).inflate(R.layout.item_yakit_tuk, p0, false))
    }

    override fun getItemCount(): Int {
        return yakitVerileri.size
    }

    override fun onBindViewHolder(p0: YakitHolder, p1: Int) {

        p0.setData(yakitVerileri.get(p1), myContext)
        p0.yakitCL.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))


    }

    inner class YakitHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var yakitCL = itemView.yakitCL
        var userName = itemView.tvUserName
        var model = itemView.tvModel
        var yakitDegeri = itemView.tvYakitDegeri


        fun setData(oAnkiYakit: ModelDetaylariData.YakitTuketimi, myContext: Context) {
            userName.text = oAnkiYakit.kullanici_adi.toString()
            model.text = oAnkiYakit.motor_yili.toString() + " Model"
            yakitDegeri.text = oAnkiYakit.yakitTuk.toString() + " lt /100km"

        }

    }


}