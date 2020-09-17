package com.creativeoffice.motosp.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.creativeoffice.motosp.Fragmentler.MotorListFragment


class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 6
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> {
                return "Honda"
            }
            1 -> {
                return "Yamaha"
            }
            2 -> {
                return "Kawasaki"
            }
            3 -> {
                return "Suzuki"
            }
            4 -> {
                return "Bmw"
            }
            5 -> {
                return "Triumph"
            }
        }
        return super.getPageTitle(position)
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return MotorListFragment("Honda")
            }
            1 -> {
                return MotorListFragment("Yamaha")
            }
            2 -> {
                return MotorListFragment("Kawasaki")
            }
            3 -> {
                return MotorListFragment("Suzuki")
            }
            4 -> {
                return MotorListFragment("Bmw")
            }
            5 -> {
                return MotorListFragment("Triumph")
            }

            else -> {
                return MotorListFragment("Honda")
            }
        }
    }


}
