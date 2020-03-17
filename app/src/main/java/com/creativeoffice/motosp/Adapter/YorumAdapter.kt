package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.yorum_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class YorumAdapter(val myContext: Context, val yorumlar: ArrayList<ModelDetaylariData.Yorumlar>, val userID: String? = null) : RecyclerView.Adapter<YorumAdapter.YorumHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YorumHolder {

        return YorumHolder(LayoutInflater.from(myContext).inflate(R.layout.yorum_item, p0, false))
    }

    override fun getItemCount(): Int {
        return yorumlar.size
    }

    override fun onBindViewHolder(p0: YorumHolder, p1: Int) {
        val currentItem = yorumlar.get(p1)
        p0.setData(currentItem)
      //  p0.imgProfile.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.olusma_sol))

        p0.itemView.setOnLongClickListener {

            var yorumUserID = currentItem.yorum_yapan_kisi.toString()
            if (yorumUserID.equals(userID)){

                var alert = AlertDialog.Builder(myContext)
                    .setTitle("Yorumu Sil")
                    .setMessage("Emin Misiniz ?")
                    .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(currentItem.yorum_yapilan_model.toString()).child("yorumlar").child(currentItem.yorum_key.toString()).removeValue()
                        }

                    })
                    .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                        }
                    }).create()

                alert.show()
            }

            return@setOnLongClickListener true
        }
    }

    inner class YorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.userName
        val comment = itemView.comment
        val date = itemView.date
      //  val imgProfile = itemView.circleImageView
        val yorumCL = itemView.yorumCL


        fun setData(currentItem: ModelDetaylariData.Yorumlar) {
            userName.text = currentItem.isim
            comment.text = currentItem.yorum
            date.text = formatDate(currentItem.tarih).toString()


            FirebaseDatabase.getInstance().reference.child("users").child(currentItem.yorum_yapan_kisi.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var imgURL = p0.child("user_details").child("profile_picture").value.toString()

                    if (imgURL != "default") {
                      //  Picasso.get().load(imgURL).into(imgProfile)
                    }
                }

            })

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, MMM d, ''yy", Locale("tr"))
            return sdf.format(date)

        }


    }


}