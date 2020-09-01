package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.EtkinliklerAdapter
import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Datalar.EtkinlikData
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_etkinlikler.*
import kotlinx.android.synthetic.main.activity_etkinlikler.bottomNavigationView


class EtkinliklerActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etkinlikler)
        setupNavigationView()
        setupRecyclerView()
    }


    private fun setupRecyclerView() {

        var etkinliklerList = ArrayList<EtkinlikData>()
        etkinliklerList.add(EtkinlikData("İstanbul Kadıköy Gezisi",1600451691000,"İstanbul",150))
        etkinliklerList.add(EtkinlikData("Kırklareli Camping ",1880451691000,"Kırklareli",50))
        etkinliklerList.add(EtkinlikData("Edirne Turu",1599092294000,"Edirne",450))

        rcEtkinlikler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //  rvModelListesi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = EtkinliklerAdapter(this, etkinliklerList)
        rcEtkinlikler.adapter = markaAdapter

        //  rvModelListesi.setHasFixedSize(true)
        // ar_indicator_motor.attachTo(rvModelListesi, true)

    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}