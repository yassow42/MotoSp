package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.creativeoffice.motosp.Adapter.ForumKonuBasliklariAdapter
import com.creativeoffice.motosp.Adapter.YeniAcilanKonuAdapter
import com.creativeoffice.motosp.Datalar.ForumKonuData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_tumkonular.*


class TumKonularActivity : AppCompatActivity() {

    lateinit var konularList: ArrayList<ForumKonuData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tumkonular)


        konularList = ArrayList()
        init()

    }

    private fun init() {
        FirebaseDatabase.getInstance().reference.child("Forum").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {


                    var gelenKonu: ForumKonuData
                    for (i in p0.children) {
                        gelenKonu = i.getValue(ForumKonuData::class.java)!!
                        konularList.add(gelenKonu)

                    }
                    konularList.sortByDescending { it.son_cevap_zamani }


                    rcTumKonular.layoutManager = LinearLayoutManager(this@TumKonularActivity,LinearLayoutManager.VERTICAL,false)
                    var tumKonularAdapter = ForumKonuBasliklariAdapter(this@TumKonularActivity,konularList)
                    rcTumKonular.adapter = tumKonularAdapter
                    rcTumKonular.setItemViewCacheSize(20)

                }
            }
        })
    }
}
