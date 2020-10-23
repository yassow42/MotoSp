package com.creativeoffice.motosp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.creativeoffice.motosp.Fragmentler.MotorListFragment


class HomePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
              //  return ForumFragment()
                return MotorListFragment("Yamaha")
            }
            1 -> {
                return MotorListFragment("Yamaha")
            }
            2 -> {
                return MotorListFragment("Kawasaki")
            }

            else -> {
                return MotorListFragment("Honda")
            }


        }
    }

    override fun getItemCount(): Int {
        return 3
    }


}