package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Datalar.HaberlerData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_konu_cevap_duzenle.view.*
import kotlinx.android.synthetic.main.item_yorum.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HaberYorumlariAdapter(val myContext: Context, val yorumlar: ArrayList<HaberlerData.Yorumlar>, val userID: String? = null) : RecyclerView.Adapter<HaberYorumlariAdapter.YorumHolder>() {
    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): YorumHolder {

        return YorumHolder(LayoutInflater.from(myContext).inflate(R.layout.item_yorum, p0, false))
    }

    override fun getItemCount(): Int {
        return yorumlar.size
    }

    override fun onBindViewHolder(holder: YorumHolder, position: Int) {
        val gelenItem = yorumlar.get(position)

        holder.setData(gelenItem)
        holder.yildizlar.visibility = View.GONE
        //     holder.yorumCL.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))
        holder.tumLayout.setOnLongClickListener {
            if (gelenItem.yorum_yapan_key.equals(userID)) {

                val popup = PopupMenu(myContext, holder.tumLayout)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.popDüzenle -> {
                            var builder: AlertDialog.Builder = AlertDialog.Builder(myContext)

                            var view: View = View.inflate(myContext, R.layout.dialog_konu_cevap_duzenle, null)

                            view.etKonuCevabi.setText(gelenItem.yorum.toString())
                            builder.setView(view)
                            var dialog: Dialog = builder.create()


                            view.btnKaydet.setOnClickListener {
                                var yeniCevap = view.etKonuCevabi.text.toString()
                                ref.child("Haberler").child(gelenItem.haber_key.toString()).child("yorumlar").child(gelenItem.yorum_key.toString()).child("yorum").setValue(yeniCevap)
                                notifyDataSetChanged()

                                dialog.dismiss()

                            }

                            view.btnIptal.setOnClickListener {

                                dialog.dismiss()
                            }
                            dialog.show()
                        }

                        R.id.popSil -> {
                            var alert = AlertDialog.Builder(myContext)
                                .setTitle("Yorumu Sil")
                                .setMessage("Emin Misiniz ?")
                                .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        ref.child("Haberler").child(gelenItem.haber_key.toString()).child("yorumlar").child(gelenItem.yorum_key.toString()).removeValue()
                                        notifyDataSetChanged()
                                    }
                                })
                                .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                    override fun onClick(p0: DialogInterface?, p1: Int) {
                                        p0!!.dismiss()
                                    }
                                }).create()

                            alert.show()
                        }
                    }
                    return@OnMenuItemClickListener true
                })
                popup.show()
            }

            return@setOnLongClickListener true
        }


    }

    inner class YorumHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tumLayout = itemView.tumLayout
        val userName = itemView.userName
        val comment = itemView.comment
        val date = itemView.date
        val yildizlar = itemView.rbYorumItem


        fun setData(oAnkiVeri: HaberlerData.Yorumlar) {
            userName.text = oAnkiVeri.isim.toString()
            comment.text = oAnkiVeri.yorum.toString()
            date.text = formatDate(oAnkiVeri.tarih).toString()
        }


        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EEE, d MMM, ''yy", Locale("tr"))
            return sdf.format(date)

        }


    }


}