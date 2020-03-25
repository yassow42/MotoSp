package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.SehirAdapter
import com.creativeoffice.motosp.Datalar.BayilerData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_bayi.*

class BayiActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 2
    var sehirList = ArrayList<BayilerData>()
  //  var ilceList = ArrayList<BayilerData.ilcelerData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayi)

        sehirList = ArrayList()
    //    ilceList = ArrayList()



        initVeri()
        setupNavigationView()
    }

    private fun initVeri() {


        FirebaseDatabase.getInstance().reference.child("Bayiler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                for (sehir in p0.children) {
                  var gelendata =   BayilerData(sehir.key.toString())
                    sehirList.add(gelendata)
                }
                setupRecyclerViewSehir()

            }


        })
    }

    private fun setupRecyclerViewSehir() {
          rcBayi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
     //   rcBayi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = SehirAdapter(this, sehirList)

        rcBayi.adapter = markaAdapter
        rcBayi.setItemViewCacheSize(20)


    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
