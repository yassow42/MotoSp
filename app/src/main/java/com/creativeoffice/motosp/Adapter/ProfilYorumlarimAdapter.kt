package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.GidilenProfilActivity
import com.creativeoffice.motosp.Activity.KonuDetayActivity
import com.creativeoffice.motosp.Activity.ProfileActivity
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_konu_cevap_duzenle.view.*
import kotlinx.android.synthetic.main.dialog_photo.view.*
import kotlinx.android.synthetic.main.profil_yorumlari.view.*

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProfilYorumlarimAdapter(val myContext: Context, val cevapList: ArrayList<ForumKonuData.cevaplar>, var userID: String?) : RecyclerView.Adapter<ProfilYorumlarimAdapter.ForumCevapHolder>() {

val ref =FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ForumCevapHolder {

        val view = LayoutInflater.from(myContext).inflate(R.layout.profil_yorumlari, p0, false)




        return ForumCevapHolder(view)
    }

    override fun getItemCount(): Int {
        if (cevapList.size < 5) {
            return cevapList.size
        } else return 5

    }

    override fun onBindViewHolder(holder: ForumCevapHolder, position: Int) {


        var gelenItem = cevapList[position]
        var konu_basligi = ""
        var konu_sahibi_cevap = ""
        var konuyu_acan_key = ""
       ref.child("users").child(userID.toString()).child("yorumlarim").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                konu_basligi = p0.child(gelenItem.cevap_key.toString()).child("konu_basligi").value.toString()
                konu_sahibi_cevap = p0.child(gelenItem.cevap_key.toString()).child("konu_sahibi_cevap").value.toString()
                konuyu_acan_key = p0.child(gelenItem.cevap_key.toString()).child("konuyu_acan_key").value.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        holder.setData(gelenItem, myContext)

        holder.tumLayout.setOnClickListener {
            val intent = Intent(myContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("konuBasligi", konu_basligi)
            intent.putExtra("konuCevabi", konu_sahibi_cevap)

            intent.putExtra("konuKey", gelenItem.cevap_yazilan_key)
            intent.putExtra("konuyu_acan_key", konuyu_acan_key)
            myContext.startActivity(intent)
        }


        holder.tumLayout.setOnLongClickListener {

/*
            if (gelenItem.cevap_yazan_key.equals(userID)) {

                val popup = PopupMenu(myContext, holder.tumLayout)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.popDüzenle -> {
                            var builder: AlertDialog.Builder = AlertDialog.Builder(this.myContext)

                            var view: View = inflate(myContext, R.layout.dialog_konu_cevap_duzenle, null)

                            view.etKonuCevabi.setText(gelenItem.cevap.toString())
                            builder.setView(view)
                            var dialog: Dialog = builder.create()


                            view.btnKaydet.setOnClickListener {
                                var yeniCevap = view.etKonuCevabi.text
                                FirebaseDatabase.getInstance().reference.child("Forum").child(gelenItem.cevap_yazilan_key.toString()).child("cevaplar").child(gelenItem.cevap_key.toString())
                                    .child("cevap").setValue(yeniCevap.toString()).addOnCompleteListener {
                                        Toast.makeText(myContext, "Cevanın Güncelleniyor :) Biraz Bekle ", Toast.LENGTH_SHORT).show()

                                        FirebaseDatabase.getInstance().reference.child("Forum").child(gelenItem.cevap_yazilan_key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onCancelled(p0: DatabaseError) {

                                            }

                                            override fun onDataChange(p0: DataSnapshot) {

                                                if (p0.hasChildren()) {
                                                    val konuBasligi = p0.child("konu_basligi").value.toString()
                                                    val konuKey = p0.child("konu_key").value.toString()
                                                    val konuAcanKey = p0.child("konuyu_acan_key").value.toString()


                                                    val intent = Intent(myContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                                    intent.putExtra("konuBasligi", konuBasligi)
                                                    intent.putExtra("konuKey", konuKey)
                                                    intent.putExtra("konuyu_acan_key", konuAcanKey)
                                                    myContext.startActivity(intent)
                                                }

                                            }

                                        })


                                    }
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
                                        FirebaseDatabase.getInstance().reference.child("Forum").child(gelenItem.cevap_yazilan_key.toString()).child("cevaplar")
                                            .child(gelenItem.cevap_key.toString()).removeValue().addOnCompleteListener {
                                                FirebaseDatabase.getInstance().reference.child("Forum").child(gelenItem.cevap_yazilan_key.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onCancelled(p0: DatabaseError) {
                                                    }

                                                    override fun onDataChange(p0: DataSnapshot) {
                                                        if (p0.hasChildren()) {
                                                            val konuBasligi = p0.child("konu_basligi").value.toString()
                                                            val konuKey = p0.child("konu_key").value.toString()
                                                            val konuAcanKey = p0.child("konuyu_acan_key").value.toString()


                                                            val intent = Intent(myContext, KonuDetayActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                                            intent.putExtra("konuBasligi", konuBasligi)
                                                            intent.putExtra("konuKey", konuKey)
                                                            intent.putExtra("konuyu_acan_key", konuAcanKey)
                                                            myContext.startActivity(intent)
                                                        }
                                                    }
                                                })
                                            }
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
            }*/
            return@setOnLongClickListener true
        }


    }


    inner class ForumCevapHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cevapZamani = itemView.tvCevapZamani
        var cevap = itemView.tvCevap
        var tumLayout = itemView.tumLayout


        var ref = FirebaseDatabase.getInstance().reference
        fun setData(gelenItemVerisi: ForumKonuData.cevaplar, myContext: Context) {


            cevapZamani.text = formatDate(gelenItemVerisi.cevap_zamani).toString()
            cevap.text = gelenItemVerisi.cevap

            var cevap_yazan_key = gelenItemVerisi.cevap_yazan_key.toString()


        }

        fun formatDate(miliSecond: Long?): String? {
            if (miliSecond == null) return "0"
            val date = Date(miliSecond)
            val sdf = SimpleDateFormat("EMM/dd/yy h:mm a", Locale("tr"))
            return sdf.format(date)

        }

    }
}