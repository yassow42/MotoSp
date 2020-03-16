package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*


import kotlinx.android.synthetic.main.activity_main.bottomNavigationView


class HomeActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 0
    private val TAG = "HomeActivity"

    lateinit var tumModeller: ArrayList<ModelDetaylariData>
    var myRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tumModeller = ArrayList()
        markaModelGetir()
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
                    }
                    setupRecyclerView()
                }
            })
    }


    private fun setupRecyclerView() {
        rvModelListesi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        // rvModelListesi.layoutManager = LinearLayoutManager(context!!.applicationContext, LinearLayoutManager.VERTICAL, false)
        rvModelListesi.adapter = MarkaModelAdapter(this.applicationContext, tumModeller)
        rvModelListesi.setItemViewCacheSize(20)


    }

    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }


}
