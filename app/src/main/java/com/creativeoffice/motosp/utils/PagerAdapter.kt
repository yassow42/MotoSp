package com.creativeoffice.motosp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.creativeoffice.motosp.Fragmentler.HondaFragment


class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0-> {return "Honda"}
            1-> {return "Yamaha"}
           2-> {return "Bmw"}
        }
        return super.getPageTitle(position)
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return HondaFragment()
            }
            1 -> {
                return HondaFragment()
            }
            2 -> {
                return HondaFragment()
            }

            else -> {
                return HondaFragment()
            }
        }
    }


}
