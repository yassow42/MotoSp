package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.GidilenProfilActivity
import com.creativeoffice.motosp.Activity.ProfileActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_yorum.view.*
import java.text.SimpleDateFormat
import java.util.*

class YorumAdapter(val myContext: Context, val yorumlar: ArrayList<ModelDetaylariData.Yorumlar>, val userID: String? = null) : RecyclerView.Adapter<YorumAdapter.YorumHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YorumHolder {

        return YorumHolder(LayoutInflater.from(myContext).inflate(R.layout.item_yorum, p0, false))
    }

    override fun getItemCount(): Int {
        return yorumlar.size
    }

    override fun onBindViewHolder(holder: YorumHolder, position: Int) {
        val currentItem = yorumlar.get(position)
        holder.setData(currentItem)
        holder.yorumCL.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))

        holder.itemView.setOnLongClickListener {

            var yorumUserID = currentItem.yorum_yapan_kisi.toString()
            if (yorumUserID.equals(userID)) {

                var alert = AlertDialog.Builder(myContext)
                    .setTitle("Yorumu Sil")
                    .setMessage("Emin Misiniz ?")
                    .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p5: Int) {
                            FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(currentItem.yorum_yapilan_model.toString()).child("yorumlar").child(currentItem.yorum_key.toString())
                                .removeValue().addOnCompleteListener {
                                    Toast.makeText(myContext, "Yorumun silindi.", Toast.LENGTH_LONG).show()
                                    yorumlar.remove(yorumlar.get(position))


                                }
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
        holder.yorumCL.setOnClickListener {


            if (userID.equals(yorumlar.get(position).yorum_yapan_kisi.toString())) {

                val intent = Intent(myContext, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                myContext.startActivity(intent)
            } else {
                val intent = Intent(myContext, GidilenProfilActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                intent.putExtra("gidilenUserID", yorumlar.get(position).yorum_yapan_kisi.toString())
                myContext.startActivity(intent)
            }


        }

    }

    inner class YorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.userName
        val comment = itemView.comment
        val date = itemView.date

        val yorumCL = itemView.yorumCL
        val rbYorumItem = itemView.rbYorumItem


        fun setData(currentItem: ModelDetaylariData.Yorumlar) {
            userName.text = currentItem.isim
            comment.text = currentItem.yorum
            date.text = formatDate(currentItem.tarih).toString()

            currentItem.yorum_yildiz!!.toFloat()?.let {
                rbYorumItem.rating = it
            }

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, MMM d, ''yy", Locale("tr"))
            return sdf.format(date)

        }


    }


}