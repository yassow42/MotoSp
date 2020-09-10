package com.creativeoffice.motosp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.creativeoffice.motosp.Fragmentler.HondaFragment


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
                return HondaFragment("Honda")
            }
            1 -> {
                return HondaFragment("Yamaha")
            }
            2 -> {
                return HondaFragment("Kawasaki")
            }
            3 -> {
                return HondaFragment("Suzuki")
            }
            4 -> {
                return HondaFragment("Bmw")
            }
            5 -> {
                return HondaFragment("Triumph")
            }

            else -> {
                return HondaFragment("Honda")
            }
        }
    }


}
