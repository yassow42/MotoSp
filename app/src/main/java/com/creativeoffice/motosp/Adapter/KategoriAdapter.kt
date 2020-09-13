package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.TumKonularActivity
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_kategoriler.view.*

class KategoriAdapter(val myContext: Context, val kategoriList: MutableList<String>, var konularList: ArrayList<ForumKonuData>) : RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KategoriViewHolder {
        return KategoriViewHolder(LayoutInflater.from(myContext).inflate(R.layout.item_kategoriler, parent, false))
    }

    override fun getItemCount(): Int {
        return kategoriList.size
    }

    override fun onBindViewHolder(holder: KategoriViewHolder, position: Int) {
        val item = kategoriList.get(position)
        holder.tvKategori.text = item.toString()
        holder.itemView.setOnClickListener {
            val intent = Intent(myContext, TumKonularActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            intent.putExtra("kategori", item)
            myContext.startActivity(intent)
        }
        var ref = FirebaseDatabase.getInstance().reference
        ref.child("Sayisal_Veriler/Forum").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                holder.tvKonuSayisi.text = "Konu sayisi: " + p0.child(item).value.toString()


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    inner class KategoriViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvKategori = itemView.tvKategori
        var tvKonuSayisi = itemView.tvKonuSayisi

    }


}