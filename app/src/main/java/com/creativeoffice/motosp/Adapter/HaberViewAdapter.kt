package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.creativeoffice.motosp.Activity.HaberDetaylariActivity
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_haber.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HaberViewAdapter(val myContext: Context, var haberler: ArrayList<HaberlerData>) : RecyclerView.Adapter<HaberViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(myContext)
        val view = inflater.inflate(R.layout.item_haber, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = haberler[position]


        if (item.haber_videolumu != null && item.haber_video != null) {

            if (item.haber_videolumu == false) {
                Picasso.get().load(item.haber_video.toString()).into(holder.img)
            } else {
                Picasso.get().load(makeImagePath(item.haber_video.toString())).into(holder.img)
            }
        }
        holder.setData(item,myContext)

    }

    override fun getItemCount(): Int {
        return haberler.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var img = itemView.imgHaber
        var haberBaslik = itemView.tvHaberBasligi
        var haberAltBaslik = itemView.tvAltBaslik
        var tumLayout = itemView.tumLayout
        var tvZaman = itemView.tvZaman


        fun setData(haberler: HaberlerData?, myContext: Context) {
            haberBaslik.text = haberler!!.haber_baslik
            haberAltBaslik.text = haberler.haber_altbaslik
            tvZaman.text = formatDate(haberler.haber_eklenme_zamani).toString()




            tumLayout.setOnClickListener {
                var intent = Intent(myContext, HaberDetaylariActivity::class.java)

                intent.putExtra("haber_altbaslik", haberler.haber_altbaslik.toString())
                intent.putExtra("haber_baslik", haberler.haber_baslik.toString())
                intent.putExtra("haber_eklenme", haberler.haber_eklenme_zamani.toString())
                intent.putExtra("haber_icerik", haberler.haber_icerik.toString())
                intent.putExtra("haber_key", haberler.haber_key.toString())
                intent.putExtra("haber_video", haberler.haber_video.toString())
                intent.putExtra("haber_videolumu", haberler.haber_videolumu.toString())


                myContext.startActivity(intent)

            }


        }


        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("d MMM", Locale("tr"))
            return sdf.format(date)

        }

    }


    val YOUTUBE = "https://www.youtube.com/watch?v="
    val YOUTUBE_IMAGE = "https://img.youtube.com/vi/"

    fun makeYoutubePath(videoId: String): String {
        return YOUTUBE + videoId

    }

    fun makeImagePath(videoId: String): String {
        return YOUTUBE_IMAGE + videoId + "/0.jpg"
    }



}