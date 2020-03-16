package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_main.*

class BayiActivity : AppCompatActivity() {

    private val ACTIVITY_NO = 1
    private val TAG = "BayiActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bayi)

        setupNavigationView()
    }




    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
