package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.HaberDetaylariActivity
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_haber_detay.view.*
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

        Picasso.get().load(makeImagePath(haberler[position].haber_video.toString())).into(holder.img)



        holder.setData(haberler[position])
    }


    inner class HaberHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img = itemView.imageView7
        var haberBaslik = itemView.tvHaberBasligi
        var haberAltBaslik = itemView.tvAltBaslik
        var tumLayout = itemView.tumLayout

        fun setData(haberler: HaberlerData?) {
            haberBaslik.text = haberler!!.haber_baslik
            haberAltBaslik.text = haberler!!.haber_altbaslik



            tumLayout.setOnClickListener {
                var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)

                var viewDialog: View = inflate(myContext, R.layout.dialog_haber_detay, null)

                viewDialog.tvHaberBaslik.text = haberler.haber_baslik.toString()
                viewDialog.tvHaberIcerik.text = haberler.haber_icerik.toString()

                var currentSecond = 0f
                viewDialog.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo(haberler.haber_video.toString(), 0f)
                    }

                    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                        currentSecond = second
                    }
                })
                viewDialog.youtubePlayer.display
                viewDialog.youtubePlayer.getPlayerUiController().showVideoTitle(false)

                viewDialog.tvHaberDetayları.setOnClickListener {
                    var intent = Intent(myContext, HaberDetaylariActivity::class.java)

                    intent.putExtra("haber_altbaslik",haberler.haber_altbaslik.toString())
                    intent.putExtra("haber_baslik",haberler.haber_baslik.toString())
                    intent.putExtra("haber_eklenme",haberler.haber_eklenme_zamani.toString())
                    intent.putExtra("haber_icerik",haberler.haber_icerik.toString())
                    intent.putExtra("haber_key",haberler.haber_key.toString())
                    intent.putExtra("haber_video",haberler.haber_video.toString())


                    myContext.startActivity(intent)
                }


                builder.setView(viewDialog)


                var dialog: Dialog = builder.create()
                dialog.show()


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
        val sdf = SimpleDateFormat("EEE, MMM d, ''yy", Locale("tr"))
        return sdf.format(date)

    }

}