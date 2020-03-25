package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Activity.ModelDetayiActivity
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kotlinx.android.synthetic.main.tek_model_list.view.*


class MarkaModelAdapter(val myContext: Context, val tumModeller: ArrayList<ModelDetaylariData>) : RecyclerView.Adapter<MarkaModelAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
        var viewHolder = LayoutInflater.from(myContext).inflate(R.layout.tek_model_list, p0, false)

        viewHolder.scrollView.visibility = View.GONE
        viewHolder.tvDetaylariGizle.visibility = View.GONE
        viewHolder.ytTekModelList.visibility = View.GONE
        viewHolder.yorumlariGor.visibility = View.GONE
        viewHolder.tvTanitim2.visibility = View.GONE

        return MyViewHolder(viewHolder, myContext)
    }

    override fun getItemCount(): Int {
        return tumModeller.size
    }

    override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {

      //  p0.tumLayout.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sol_yari))

        //  p0.tvModel.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme))

        p0.setData(tumModeller.get(p1), myContext)


        p0.yorumlariGor.setOnClickListener {


            val intent = Intent(myContext, ModelDetayiActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("Marka", tumModeller.get(p1).marka.toString())
            intent.putExtra("Model", tumModeller.get(p1).model.toString())
            intent.putExtra("Kategori", tumModeller.get(p1).kategori.toString())
            intent.putExtra("Silindir", tumModeller.get(p1).silindirHacmi.toString())
            intent.putExtra("Beygir", tumModeller.get(p1).beygir.toString())
            intent.putExtra("Hiz", tumModeller.get(p1).hiz.toString())
            intent.putExtra("Tork", tumModeller.get(p1).tork.toString())
            intent.putExtra("Devir", tumModeller.get(p1).devir.toString())
            intent.putExtra("Agirlik", tumModeller.get(p1).agirlik.toString())
            intent.putExtra("YakitKap", tumModeller.get(p1).yakitkap.toString())
            intent.putExtra("YakitTuk", tumModeller.get(p1).yakitTuk.toString())
            intent.putExtra("tanitim", tumModeller.get(p1).tanitim.toString())


            myContext.startActivity(intent)


        }


    }

    class MyViewHolder(viewHolder: View?, myContext: Context) : RecyclerView.ViewHolder(viewHolder!!) {
        var tumLayout = viewHolder as ConstraintLayout
        var tvMarka = tumLayout.tvMarka
        var tvModel = tumLayout.tvModel
        var tvDetaylariGoster = tumLayout.tvDetaylariGoster
        var tvDetaylariGizle = tumLayout.tvDetaylariGizle
        var youTubePlayer = tumLayout.ytTekModelList
        var tvTanitim = tumLayout.tvTanitim
        var tvTanitim2 = tumLayout.tvTanitim2

        var scrollView = tumLayout.scrollView
        var yorumlariGor = tumLayout.yorumlariGor


        var detay_kategori = tumLayout.detay_kategori
        var detay_silindir = tumLayout.detay_silindirhacmi
        var detay_beygir = tumLayout.detay_beygir
        var detay_hiz = tumLayout.detay_hiz
        var detay_tork = tumLayout.detay_tork
        var detay_devir = tumLayout.detay_devir
        var detay_agirlik = tumLayout.detay_agirlik
        var detay_kapasite = tumLayout.detay_yakitKap
        var detay_tuketimi = tumLayout.detay_yakitTuk

        var imgMotoripi = tumLayout.imgMotorTipi


        fun setData(oAnkiModel: ModelDetaylariData, myContext: Context) {
            val youTubeUrl = oAnkiModel.motorVideo
            tvMarka.text = oAnkiModel.marka.toString()
            tvModel.text = oAnkiModel.model.toString()
            tvTanitim.text = oAnkiModel.tanitim.toString()
            tvTanitim2.text = oAnkiModel.tanitim.toString()

            detay_kategori.text = oAnkiModel.kategori
            detay_silindir.text = oAnkiModel.silindirHacmi
            detay_beygir.text = oAnkiModel.beygir
            detay_hiz.text = oAnkiModel.hiz
            detay_tork.text = oAnkiModel.tork
            detay_devir.text = oAnkiModel.devir

            detay_agirlik.text = oAnkiModel.agirlik
            detay_kapasite.text = oAnkiModel.yakitkap
            detay_tuketimi.text = oAnkiModel.yakitTuk

            var currentSecond = 0f

            youTubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(youTubeUrl.toString(), 0f)
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    currentSecond = second
                }
            })

            if (oAnkiModel.tanitim == null || oAnkiModel.tanitim.isNullOrEmpty() || oAnkiModel.tanitim.toString().trim() == "") {
                tvTanitim.visibility = View.GONE
                tvTanitim2.visibility = View.GONE
            }


            tvDetaylariGoster.setOnClickListener {

                scrollView.visibility = View.VISIBLE
                tvDetaylariGoster.visibility = View.GONE
                tvDetaylariGizle.visibility = View.VISIBLE
                youTubePlayer.visibility = View.VISIBLE
                yorumlariGor.visibility = View.VISIBLE
                youTubePlayer!!.getPlayerUiController().setVideoTitle(oAnkiModel.marka + " " + oAnkiModel.model)
                youTubePlayer.enableBackgroundPlayback(false)

                youTubePlayer!!.getPlayerUiController().showYouTubeButton(false)
                youTubePlayer!!.getPlayerUiController().showMenuButton(false)

                youTubePlayer!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma))
                scrollView!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sol))

                tvMarka!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.kaybolma_sol))
                tvModel!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.kaybolma_sol))
                imgMotoripi!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.kaybolma_sag))



                tvMarka.visibility = View.GONE
                tvModel.visibility = View.GONE
                tvTanitim2.visibility = View.VISIBLE
                tvTanitim.visibility = View.GONE
                imgMotoripi.visibility = View.GONE


            }

            tvDetaylariGizle.setOnClickListener {
                tvMarka!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sol))
                tvModel!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sol))
                imgMotoripi!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sag))
                tvTanitim2!!.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.kaybolma_sag))


                scrollView.visibility = View.GONE
                tvDetaylariGoster.visibility = View.VISIBLE
                tvDetaylariGizle.visibility = View.GONE
                youTubePlayer.visibility = View.GONE
                yorumlariGor.visibility = View.GONE

                tvMarka.visibility = View.VISIBLE
                tvModel.visibility = View.VISIBLE
                tvTanitim2.visibility = View.GONE
                tvTanitim.visibility = View.VISIBLE
                imgMotoripi.visibility = View.VISIBLE

            }

            if (oAnkiModel.kategori == "Scooter") {
                imgMotoripi.setBackgroundResource(R.drawable.ic_scooter)
            } else if (oAnkiModel.kategori == "Sport" || oAnkiModel.kategori == "Racing") {

                imgMotoripi.setBackgroundResource(R.drawable.ic_sport)
            } else if (oAnkiModel.kategori == "Touring" || oAnkiModel.kategori == "Enduro" || oAnkiModel.kategori == "Adventure") {
                imgMotoripi.setBackgroundResource(R.drawable.ic_touring)
            } else if (oAnkiModel.kategori == "Cross") {
                imgMotoripi.setBackgroundResource(R.drawable.ic_cross)
            } else if (oAnkiModel.kategori == "Naked") {
                imgMotoripi.setBackgroundResource(R.drawable.ic_naked)
            } else if (oAnkiModel.kategori == "Chopper") {
                imgMotoripi.setBackgroundResource(R.drawable.ic_chopper)
            }


        }

    }


}