package com.creativeoffice.motosp.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Datalar.EventBusDataEvents
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_motor.*


import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MotorActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1
    private val TAG = "MotorActivity"

    lateinit var tumModeller: ArrayList<ModelDetaylariData>

    var myRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor)



        setupNavigationView()

    }

    private fun markaModelGetir() {
        myRef.child("tum_motorlar")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (ds in p0.children) {
                            var modeller = ds.getValue(ModelDetaylariData::class.java)!!
                         //   if (modeller.marka =="Honda"){
                                tumModeller.add(modeller)
                        //    }


                        }

                        //tumModeller.sortBy { it.marka }
                        //rastgele sıralama.shuffle
                        // tumModeller.shuffle()
                    }
                    setupRecyclerView()
                }
            })
    }


    private fun setupRecyclerView() {
        //rvModelListesi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rvModelListesi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = MarkaModelAdapter(this.applicationContext, tumModeller)
        rvModelListesi.adapter = markaAdapter
        rvModelListesi.setHasFixedSize(true)
        //rvModelListesi.setItemViewCacheSize(20)


    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }




    override fun onStart() {

        tumModeller = ArrayList()

         markaModelGetir()
        super.onStart()
    }




}
