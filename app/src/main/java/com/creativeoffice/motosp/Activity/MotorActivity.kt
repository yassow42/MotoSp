package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_motor.*


import kotlinx.android.synthetic.main.activity_main.bottomNavigationView


class MotorActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1
    private val TAG = "MotorActivity"

    lateinit var tumModeller: ArrayList<ModelDetaylariData>
    var myRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor)

        tumModeller = ArrayList()

        setupNavigationView()
    }

    private fun markaModelGetir() {
        myRef.child("tum_motorlar")//.child("Honda")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.hasChildren()) {
                        for (ds in p0.children) {
                            var modeller = ds.getValue(ModelDetaylariData::class.java)!!
                            tumModeller.add(modeller)

                        }

                     //   tumModeller.sortBy { it.marka }
                    }
                    setupRecyclerView()
                }
            })
    }


    private fun setupRecyclerView() {
        rvModelListesi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        // rvModelListesi.layoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = MarkaModelAdapter(this.applicationContext, tumModeller)

        rvModelListesi.adapter = markaAdapter
        rvModelListesi.setItemViewCacheSize(20)


    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    override fun onStart() {
        markaModelGetir()
        super.onStart()
    }


}
