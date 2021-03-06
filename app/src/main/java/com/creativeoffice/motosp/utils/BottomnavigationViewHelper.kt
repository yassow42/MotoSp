package com.creativeoffice.motosp.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.creativeoffice.motosp.Activity.*


import com.creativeoffice.motosp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

class BottomnavigationViewHelper {

    companion object {

        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx) {


            bottomNavigationViewEx.enableAnimation(true)
       //     bottomNavigationViewEx.enableShiftingMode(2,true)
            bottomNavigationViewEx.setTextVisibility(true)
            bottomNavigationViewEx.setLargeTextSize(14f)
            bottomNavigationViewEx.setSmallTextSize(10f)

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

                            R.id.ic_etkinlik->{
                                val intent = Intent(context, EtkinliklerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                                context.startActivity(intent)
                                return true
                            }


                            R.id.ic_motortanitim->{
                                val intent = Intent(context, Motor2Activity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

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