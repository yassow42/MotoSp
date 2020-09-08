package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_bayi_yorumlari.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BayiYorumlariAdapter(var myContext: Context, var bayiYorumList: ArrayList<BayilerData.BayiYorumlari>) : RecyclerView.Adapter<BayiYorumlariAdapter.BayiYorumHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): BayiYorumHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_bayi_yorumlari, p0, false)

        // view.rcIlceler.visibility = View.GONE
        return BayiYorumHolder(view)
    }

    override fun onBindViewHolder(holder: BayiYorumHolder, position: Int) {
        var item = bayiYorumList[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return bayiYorumList.size
    }


    class BayiYorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var yorumYapan = itemView.userName
        var yorum = itemView.yorum
        var zaman = itemView.zaman
        var rbYorum = itemView.rbYorum

        fun setData(item: BayilerData.BayiYorumlari) {


            yorum.text = item.yorum
            zaman.text = formatDate(item.yorum_zamani).toString()
            rbYorum.rating = item.yildiz!!.toFloat()

            FirebaseDatabase.getInstance().reference.child("users").child(item.yorum_yapan_key.toString()).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {

                    if (item.yorum_yapan_key.toString() !="Admin"){
                        yorumYapan.text = p0.value.toString()
                    }else  yorumYapan.text = "Admin"


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("E MM/dd/yy h:mm a", Locale("tr"))
            return sdf.format(date)

        }
    }


}