package com.creativeoffice.motosp.Activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Adapter.SpinnerAdapter

import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.SpinnerData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_motor.*


class MotorActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    private val TAG = "MotorActivity"

    lateinit var tumModeller: ArrayList<ModelDetaylariData>
    lateinit var secilenModeller: ArrayList<ModelDetaylariData>
    lateinit var modeller: ModelDetaylariData

    var myRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor)
        secilenModeller = ArrayList()


        setupNavigationView()
        setupSpinner()

    }

    private fun setupSpinner() {
        var markaList = ArrayList<SpinnerData>()
        markaList.add(SpinnerData("Marka seçiniz", R.drawable.ic_honda))
        markaList.add(SpinnerData("Honda", R.drawable.ic_honda))
        markaList.add(SpinnerData("Yamaha",  R.drawable.yamaha))
        markaList.add(SpinnerData("Kawasaki",  R.drawable.kawasaki))
        markaList.add(SpinnerData("Triumph",  R.drawable.ic_tr))
        markaList.add(SpinnerData("Suzuki",  R.drawable.suzuki))

        val adapter = SpinnerAdapter(this, markaList)
        spMotorList.adapter = adapter

        spMotorList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position != 0) {
                    var secilenMarka = markaList[position].marka.toString()
                    secilenModeller.clear()

                    myRef.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {}
                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChildren()) {
                                for (ds in p0.children) {
                                    modeller = ds.getValue(ModelDetaylariData::class.java)!!
                                    if (modeller.marka == secilenMarka) {
                                        secilenModeller.add(modeller)
                                    }
                                }
                                if (secilenModeller.size > 0) {
                                    rvModelListesi.layoutManager = LinearLayoutManager(this@MotorActivity, LinearLayoutManager.VERTICAL, false)
                                    val markaAdapter = MarkaModelAdapter(this@MotorActivity, secilenModeller)
                                    rvModelListesi.adapter = markaAdapter
                                }


                            }

                        }
                    })

                } else if ( position ==0){
                    setupRecyclerView()
                }
            }
        }
    }

    private fun markaModelGetir() {
        myRef.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        modeller = ds.getValue(ModelDetaylariData::class.java)!!
                        //   if (modeller.marka =="Honda"){
                        tumModeller.add(modeller)
                        //    }
                    }

                    //tumModeller.sortBy { it.marka }
                    //rastgele sıralama.shuffle
                    // tumModeller.shuffle()
                    setupRecyclerView()
                }

            }
        })
    }


    private fun setupRecyclerView() {
        //rvModelListesi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        rvModelListesi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = MarkaModelAdapter(this.applicationContext, tumModeller)
        rvModelListesi.adapter = markaAdapter

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


    override fun onStart() {

        tumModeller = ArrayList()

        markaModelGetir()
        super.onStart()
    }


}
