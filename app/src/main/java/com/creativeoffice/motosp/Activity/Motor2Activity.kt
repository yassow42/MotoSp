package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.PagerAdapter
import kotlinx.android.synthetic.main.activity_motor2.*

class Motor2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_motor2)


        viewPager.adapter = PagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }
}