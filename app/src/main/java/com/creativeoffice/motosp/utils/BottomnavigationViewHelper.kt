package com.creativeoffice.motosp.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem


import com.creativeoffice.motosp.Activity.BayiActivity
import com.creativeoffice.motosp.Activity.HomeActivity
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.Activity.MotorActivity
import com.creativeoffice.motosp.Activity.ProfileActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class BottomnavigationViewHelper {

    companion object {

        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx) {


            bottomNavigationViewEx.enableAnimation(true)
            bottomNavigationViewEx.enableItemShiftingMode(false)
            bottomNavigationViewEx.enableShiftingMode(false)
            bottomNavigationViewEx.setTextVisibility(true)

        }

//2 şey istedik 1 context 2. ise navigationView istedik. Home Actvity de ikisini de verdik.
        fun setupNavigation(context: Context, bottomNavigationViewEx: BottomNavigationViewEx) {

            bottomNavigationViewEx.onNavigationItemSelectedListener =
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {

                        when (item.itemId) {

                            R.id.ic_home->{
                                val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                                return true
                            }

                            R.id.ic_motortanitim->{
                                val intent = Intent(context, MotorActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                                return true
                            }

                            R.id.ic_bayi -> {
                                val intent = Intent(context, BayiActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                                return true
                            }

                            R.id.ic_profile->{

                                val intent = Intent(context, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                                return true
                            }





                        }
                        return false
                    }


                }
        }
    }
}