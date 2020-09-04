package com.creativeoffice.motosp.Activity

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.PopupMenu
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Adapter.SpinnerAdapter

import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.Datalar.SpinnerData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.creativeoffice.motosp.utils.LoadingDialog
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_motor.*


class MotorActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    private val TAG = "MotorActivity"

    lateinit var tumModeller: ArrayList<ModelDetaylariData>
    lateinit var secilenModeller: ArrayList<ModelDetaylariData>
    lateinit var modeller: ModelDetaylariData

    var myRef = FirebaseDatabase.getInstance().reference
    var loading: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor)


        secilenModeller = ArrayList()
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setupNavigationView()
        setupSpinner()

        dialogCalistir()
        Handler().postDelayed({ markaModelGetir() }, 800)
        Handler().postDelayed({ dialogGizle() }, 4000)


    }

    private fun setupSpinner() {
        var markaList = ArrayList<SpinnerData>()
        markaList.add(SpinnerData("Marka seçiniz", R.drawable.ic_honda))
        markaList.add(SpinnerData("Honda", R.drawable.ic_honda))
        markaList.add(SpinnerData("Yamaha", R.drawable.yamaha))
        markaList.add(SpinnerData("Kawasaki", R.drawable.kawasaki))
        markaList.add(SpinnerData("Triumph", R.drawable.ic_tr))
        markaList.add(SpinnerData("Suzuki", R.drawable.suzuki))

        val adapter = SpinnerAdapter(this, markaList)
        spMotorList.adapter = adapter

        spMotorList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (position != 0) {
                    var secilenMarka = markaList[position].marka.toString()
                    secilenModeller.clear()
                    for (modeller in tumModeller) {
                        if (modeller.marka.toString() == secilenMarka) {
                            secilenModeller.add(modeller)
                        }
                    }
                    setupRecyclerView(secilenModeller)
                } else if (position == 0) {
                    setupRecyclerView(tumModeller)
                }
            }
        }



    }

    private fun markaModelGetir() {
        myRef.child("tum_motorlar").keepSynced(true)
        myRef.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        modeller = ds.getValue(ModelDetaylariData::class.java)!!
                        tumModeller.add(modeller)
                    }
                    //  tumModeller.shuffle()
                    dialogGizle()
                    setupRecyclerView(tumModeller)
                }

            }
        })
    }


    private fun setupRecyclerView(modelList: ArrayList<ModelDetaylariData>) {
        rvModelListesi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //  rvModelListesi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = MarkaModelAdapter(this, modelList)
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
        super.onStart()
    }


    fun dialogGizle() {
        loading?.let { if (it.isShowing) it.cancel() }
    }

    fun dialogCalistir() {
        dialogGizle()
        loading = LoadingDialog.startDialog(this)
    }

}
