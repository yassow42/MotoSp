package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_forum_konu_basliklari2.view.*
import kotlinx.coroutines.Delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList
import com.google.firebase.database.ValueEventListener as ValueEventListener1

class ForumKonuBasliklariAdapter(val myContext: Context, val konuList: ArrayList<ForumKonuData>) : RecyclerView.Adapter<ForumKonuBasliklariAdapter.ForumKonuBasligiHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ForumKonuBasligiHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_forum_konu_basliklari2, p0, false)
        return ForumKonuBasligiHolder(view)
    }

    override fun getItemCount(): Int {

        return konuList.size
    }

    override fun onBindViewHolder(holder: ForumKonuBasligiHolder, position: Int) {

        var gelenItem = konuList[position]
      //  holder.tumLayout.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))

        holder.setData(konuList[position], myContext)
        holder.tumLayout.setOnClickListener {

            val intent = Intent(myContext.applicationContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("konuBasligi", gelenItem.konu_basligi.toString())
            intent.putExtra("konuCevabi", gelenItem.konu_sahibi_cevap.toString())
            //   intent.putExtra("tarih", holder.formatDate(gelenItem.acilma_zamani).toString())
            intent.putExtra("konuKey", gelenItem.konu_key)
            intent.putExtra("konuyu_acan_key", gelenItem.konuyu_acan_key)

            myContext.applicationContext.startActivity(intent)

        }


    }


    inner class ForumKonuBasligiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var konuBasligi = itemView.tvKonuBasligi
        var konuKategori = itemView.tvKategori
        var userName = itemView.tvUserName
        var tarih = itemView.tvZaman
        var sonCevapZamani = itemView.tvSonCevapZamani
        var circleProfileImage = itemView.circleProfileImage
        var progressBar = itemView.progressBar

        //  var tvZamanCevap = itemView.tvZamanCevap
        // var tvSonYazan = itemView.tvSonYazan
        var tumLayout = itemView.clKonuBasliklari
        var cevapSayisi = itemView.tvCevapSayisi

        var ref = FirebaseDatabase.getInstance().reference

        fun setData(forumKonuData: ForumKonuData, myContext: Context) {
            konuBasligi.text = "  " + forumKonuData.konu_basligi
            konuKategori.text = "  " + forumKonuData.kategori
            tarih.text = formatDate(forumKonuData.acilma_zamani).toString()
            sonCevapZamani.text = formatDate(forumKonuData.son_cevap_zamani).toString()

            //foruma soncevap yazan verisi
            ref.child("Forum").child(forumKonuData.konu_key.toString()).addListenerForSingleValueEvent(object : ValueEventListener1 {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        val gelenData = p0.child("son_cevap").getValue(ForumKonuData.son_cevap::class.java)!!
                        var cevapZamani = gelenData.cevap_zamani
                        //   tvZamanCevap.text = formatDate(cevapZamani).toString()
                        //    tvSonYazan.text = gelenData.cevap_yazan

                        var sayi = p0.child("cevaplar").childrenCount
                        cevapSayisi.text = "Cevaplar: " + sayi.toString()


                    } else {
                        //     tvZamanCevap.text = ""
                        //     tvSonYazan.text = ""
                    }


                }


            })


            ref.child("users").child(forumKonuData.konuyu_acan_key.toString()).addListenerForSingleValueEvent(object : ValueEventListener1 {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        userName.text = p0.child("user_name").value.toString()
                        var imgURL = p0.child("user_details").child("profile_picture").value.toString()
                        if (imgURL != "default") {
                            Picasso.get().load(imgURL).placeholder(R.drawable.ic_profile2).into(circleProfileImage)
                            progressBar.visibility = View.GONE
                        }else{
                            progressBar.visibility = View.GONE
                            Picasso.get().load(R.drawable.ic_profile).placeholder(R.drawable.ic_profile2).into(circleProfileImage)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

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