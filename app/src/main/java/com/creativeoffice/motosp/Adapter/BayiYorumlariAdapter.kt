package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_konu_cevap_duzenle.view.*
import kotlinx.android.synthetic.main.item_bayi_yorumlari.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BayiYorumlariAdapter(var myContext: Context, var bayiYorumList: ArrayList<BayilerData.BayiYorumlari>, var userID: String) : RecyclerView.Adapter<BayiYorumlariAdapter.BayiYorumHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): BayiYorumHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.item_bayi_yorumlari, p0, false)

        // view.rcIlceler.visibility = View.GONE
        return BayiYorumHolder(view)
    }


    override fun getItemCount(): Int {
        return bayiYorumList.size
    }

    override fun onBindViewHolder(holder: BayiYorumHolder, position: Int) {
        var item = bayiYorumList[position]
        holder.setData(item)
        var ref = FirebaseDatabase.getInstance().reference
        var bayiRef = ref.child("Bayiler").child(item.sehirAdi.toString()).child(item.ilceAdi.toString()).child(item.bayiAdi.toString())

        holder.itemView.setOnLongClickListener {
            if (item.yorum_yapan_key.toString().equals(userID)) {
                val popup = PopupMenu(myContext, it)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.popDüzenle -> {
                            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)

                            var view: View = View.inflate(myContext, R.layout.dialog_konu_cevap_duzenle, null)

                            view.etKonuCevabi.setText(item.yorum.toString())
                            builder.setView(view)
                            var dialog: Dialog = builder.create()


                            view.btnKaydet.setOnClickListener {
                                var yeniCevap = view.etKonuCevabi.text
                                bayiRef.child("yorumlar").child(item.yorum_key.toString()).child("yorum").setValue(yeniCevap.toString())
                                Toast.makeText(myContext, "Cevanın Güncelleniyor :) Biraz Bekle ", Toast.LENGTH_SHORT).show()

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
                                        bayiYorumList.removeAt(position)
                                        notifyDataSetChanged()
                                        bayiRef.child("yorumlar").child(item.yorum_key.toString()).removeValue()
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

                    if (item.yorum_yapan_key.toString() != "Admin") {
                        yorumYapan.text = p0.value.toString()
                    } else yorumYapan.text = "Admin"


                }

                override fun onCancelled(error: DatabaseError) {

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