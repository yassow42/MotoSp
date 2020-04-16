package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.creativeoffice.motosp.ProfileEditFragment
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.SignOutFragment
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper

import kotlinx.android.synthetic.main.activity_profile_setting.*

class ProfileSettingActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 3
    private val TAG = "ProfileSettingActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)

        setupNavigationView()
        setupToolbar()
        fragmentNavigation()
    }


    private fun fragmentNavigation() {


        tvCikisYap.setOnClickListener {
            //Frame layout içinde fragment cagırma yapıyoruz. transaction
            profileSettingRoot.visibility = View.GONE

            var dialog = SignOutFragment()
            dialog.show(supportFragmentManager,"cikisDiyalogGoster")

        }

    }
    override fun onBackPressed() {

        profileSettingRoot.visibility = View.VISIBLE

        super.onBackPressed()
    }

    private fun setupToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }


    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationContainer)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationContainer) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationContainer.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}
