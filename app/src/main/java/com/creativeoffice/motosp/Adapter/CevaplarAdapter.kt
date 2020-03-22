package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_cevaplar.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CevaplarAdapter(val myContext: Context, val cevapList: ArrayList<ForumKonuData.cevaplar>) : RecyclerView.Adapter<CevaplarAdapter.ForumCevapHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ForumCevapHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.item_cevaplar, p0, false)




        return ForumCevapHolder(view)
    }

    override fun getItemCount(): Int {

        return cevapList.size
    }

    override fun onBindViewHolder(holder: ForumCevapHolder, position: Int) {

        var gelenItem = cevapList[position]

        holder.setData(gelenItem, myContext)


    }


    inner class ForumCevapHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName = itemView.tvUserName
        var cevapZamani = itemView.tvCevapZamani
        var cevap = itemView.tvCevap
        var imgProfile = itemView.circleProfileImage
        var kullanilanMotor = itemView.tvKullanilanMotor


        var ref = FirebaseDatabase.getInstance().reference
        fun setData(gelenItemVerisi: ForumKonuData.cevaplar, myContext: Context) {

            userName.text = gelenItemVerisi.cevap_yazan
            cevapZamani.text = formatDate(gelenItemVerisi.cevap_zamani).toString()
            cevap.text = gelenItemVerisi.cevap

            var cevap_yazan_key = gelenItemVerisi.cevap_yazan_key.toString()
            ref.child("users").child(cevap_yazan_key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var imgURL = p0.child("user_details").child("profile_picture").value.toString()
                    if (imgURL != "default") {
                        Picasso.get().load(imgURL).into(imgProfile)
                        imgProfile.borderWidth = 1
                    }

                    var Marka = p0.child("user_details").child("kullanilan_motor_marka").value.toString()
                    var Model = p0.child("user_details").child("kullanilan_motor_model").value.toString()
                    kullanilanMotor.text = Marka.trim() + " " + Model.trim()
                }
            })


        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EMM/dd/yy h:mm a", Locale("tr"))
            return sdf.format(date)

        }

    }
}