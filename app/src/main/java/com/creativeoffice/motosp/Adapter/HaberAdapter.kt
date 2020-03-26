package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_haber.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HaberAdapter(val context: Context, var haberler: ArrayList<HaberlerData>) : RecyclerView.Adapter<HaberAdapter.HaberHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaberHolder {
        return HaberHolder(LayoutInflater.from(context).inflate(R.layout.item_haber, parent, false))
    }

    override fun getItemCount(): Int {
        return haberler.size
    }


    override fun onBindViewHolder(holder: HaberHolder, position: Int) {

        Picasso.get().load(makeImagePath(haberler[position].haber_video.toString())).into(holder.img)
        Log.e("sad",makeImagePath(haberler[position].haber_video.toString()))

        holder.setData(haberler[position])
    }


    inner class HaberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img = itemView.imageView7
        var haberBaslik = itemView.tvHaberBasligi

        fun setData(haberler: HaberlerData?) {
            haberBaslik.text = haberler!!.haber_baslik

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
        val sdf = SimpleDateFormat("EEE, MMM d, ''yy", Locale("tr"))
        return sdf.format(date)

    }

}