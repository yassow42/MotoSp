package com.creativeoffice.motosp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.creativeoffice.motosp.Activity.ModelDetayiActivity
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.parca_item.view.*

class ParcaAdapter(val myContext: Context, val parcalar: ArrayList<ModelDetaylariData.Parcalar>, val userID: String? = null) : RecyclerView.Adapter<ParcaAdapter.ParcaHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ParcaHolder {

        return ParcaHolder(LayoutInflater.from(myContext).inflate(R.layout.parca_item, p0, false))
    }

    override fun getItemCount(): Int {
        return parcalar.size
    }

    override fun onBindViewHolder(p0: ParcaHolder, p1: Int) {

        var oAnkiParca = parcalar.get(p1)
        p0.setData(oAnkiParca, myContext)
        p0.parcaCL.setAnimation(AnimationUtils.loadAnimation(myContext, R.anim.ustten_inme_anti))

        p0.itemView.setOnLongClickListener {

            var parcaUserName = parcalar.get(p1).kullanici_adi
            FirebaseDatabase.getInstance().reference.child("users").child(userID.toString()).child("user_name").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                var userName = p0.value.toString()
                    if (parcaUserName.equals(userName)){

                        var alert = AlertDialog.Builder(myContext)
                            .setTitle("Parçayı Sil")
                            .setMessage("Emin Misiniz ?")
                            .setPositiveButton("Sil", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    FirebaseDatabase.getInstance().reference.child("tum_motorlar").child(oAnkiParca.model.toString()).child("yy_parcalar").child(oAnkiParca.parca_key.toString()).removeValue()
                                }

                            })
                            .setNegativeButton("İptal", object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                }
                            }).create()

                        alert.show()
                    }
                }


            })


            return@setOnLongClickListener true
        }


    }

    inner class ParcaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parcaCL = itemView.parcaCL
        val parcaIsmi = itemView.tvParcaismi
        val parcaYorumu = itemView.tvParcaYorumu
        val parcaModelYili = itemView.tvParcaModelYili
        val userName = itemView.tvParcauser


        fun setData(oAnkiParca: ModelDetaylariData.Parcalar, myContext: Context) {
            parcaIsmi.text = oAnkiParca.parca_ismi
            parcaYorumu.text = oAnkiParca.parca_model_yorumu
            parcaModelYili.text = oAnkiParca.parca_uyum_model_yili
            userName.text = oAnkiParca.kullanici_adi

        }

    }


}