package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.creativeoffice.motosp.Adapter.PagerAdapter
import com.creativeoffice.motosp.Datalar.ModelDetaylariData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_motor2.*
import kotlinx.android.synthetic.main.activity_motor2.bottomNavigationView

class Motor2Activity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    private val TAG = "MotorActivity"

    var tumModeller = ArrayList<ModelDetaylariData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor2)
        setupNavigationView()
        viewPager.adapter = PagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)


        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_honda)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.yamaha)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.kawasaki)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.suzuki)
        tabLayout.getTabAt(4)!!.setIcon(R.drawable.bmw)
        tabLayout.getTabAt(5)!!.setIcon(R.drawable.ic_triumph_background)

        //Goruntulenme Sayisi Güncelleme
        /*
        FirebaseDatabase.getInstance().reference.child("tum_motorlar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    var goruntulenmeSayisi = 0
                    for (ds in p0.children) {
                        var modeller = ds.getValue(ModelDetaylariData::class.java)!!
                        if (modeller.goruntulenme_sayisi.toString()!="null") goruntulenmeSayisi += modeller.goruntulenme_sayisi.toString().toInt()

                        tumModeller.add(modeller)
                    }

                    FirebaseDatabase.getInstance().reference.child("Sayisal_Veriler").child("motor_sayisi").setValue(p0.childrenCount)
                    FirebaseDatabase.getInstance().reference.child("Sayisal_Veriler").child("motor_goruntulenme_sayisi").setValue(goruntulenmeSayisi)

                }

            }
        })
*/

    }


    fun setupNavigationView() {
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

}