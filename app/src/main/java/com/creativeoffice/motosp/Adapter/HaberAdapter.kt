package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.HaberDetaylariActivity
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_haber.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HaberAdapter(val myContext: Context, var haberler: ArrayList<HaberlerData>) : RecyclerView.Adapter<HaberAdapter.HaberHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaberHolder {
        return HaberHolder(LayoutInflater.from(myContext).inflate(R.layout.item_haber, parent, false))
    }

    override fun getItemCount(): Int {
        return haberler.size
    }


    override fun onBindViewHolder(holder: HaberHolder, position: Int) {

        if (haberler[position].haber_videolumu != null && haberler[position].haber_video != null){

            if (haberler[position].haber_videolumu == false) {
                Picasso.get().load(haberler[position].haber_video.toString()).into(holder.img)
            } else {
                Picasso.get().load(makeImagePath(haberler[position].haber_video.toString())).into(holder.img)
            }
        }





        holder.setData(haberler[position])
    }


    inner class HaberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img = itemView.imageView7
        var haberBaslik = itemView.tvHaberBasligi
        var haberAltBaslik = itemView.tvAltBaslik
        var tumLayout = itemView.tumLayout
        var tvZaman = itemView.tvZaman

        fun setData(haberler: HaberlerData?) {
            haberBaslik.text = haberler!!.haber_baslik
            haberAltBaslik.text = haberler.haber_altbaslik
            tvZaman.text = formatDate(haberler.haber_eklenme_zamani).toString()


            Log.e("sayi",  haberler!!.haber_baslik!!.length.toString())

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

    }


    val YOUTUBE = "https://www.youtube.com/watch?v="
    val YOUTUBE_IMAGE = "https://img.youtube.com/vi/"

    fun makeYoutubePath(videoId: String): String {
        return YOUTUBE + videoId

    }

    fun makeImagePath(videoId: String): String {
        return YOUTUBE_IMAGE + videoId + "/0.jpg"
    }

    fun formatDate(miliSecond: Long?): String? {
        if (miliSecond == null) return "0"
        val date = Date(miliSecond)
        val sdf = SimpleDateFormat("d MMM", Locale("tr"))
        return sdf.format(date)

    }

}