package com.creativeoffice.motosp.Fragmentler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.MarkaModelAdapter
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.creativeoffice.motosp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_motor_list.view.*

class MotorListFragment(markaa: String) : Fragment() {

    var tumModeller = ArrayList<ModelDetaylariData>()
    var myRef = FirebaseDatabase.getInstance().reference
    var modeller = ModelDetaylariData()
    var markaa = markaa
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_motor_list, container, false)
        markaModelGetir(view)


        return view
    }

    private fun markaModelGetir(view: View) {
        myRef.child("tum_motorlar").keepSynced(true)
        myRef.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        modeller = ds.getValue(ModelDetaylariData::class.java)!!
                        if (modeller.marka == markaa) {
                            tumModeller.add(modeller)
                        }
                    }

                    setupRecyclerView(tumModeller, view)
                }

            }
        })
    }


    private fun setupRecyclerView(modelList: ArrayList<ModelDetaylariData>, view: View) {
        view.rcModelListesi.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        //  rvModelListesi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val markaAdapter = MarkaModelAdapter(context!!.applicationContext, modelList)
        view.rcModelListesi.adapter = markaAdapter

        //  rvModelListesi.setHasFixedSize(true)
        // ar_indicator_motor.attachTo(rvModelListesi, true)

    }

}