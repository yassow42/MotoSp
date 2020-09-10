package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.creativeoffice.motosp.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_motor.*
import kotlinx.android.synthetic.main.activity_motor2.*
import kotlinx.android.synthetic.main.activity_motor2.bottomNavigationView

class Motor2Activity : AppCompatActivity() {
    private val ACTIVITY_NO = 2
    private val TAG = "MotorActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor2)


        viewPager.adapter = PagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        setupNavigationView()


    tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_honda)
    tabLayout.getTabAt(1)!!.setIcon(R.drawable.yamaha)
    tabLayout.getTabAt(2)!!.setIcon(R.drawable.kawasaki)
    tabLayout.getTabAt(3)!!.setIcon(R.drawable.suzuki)
    tabLayout.getTabAt(4)!!.setIcon(R.drawable.bmw)
    tabLayout.getTabAt(5)!!.setIcon(R.drawable.ic_triumph_background)

    }


    fun setupNavigationView() {
        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

}