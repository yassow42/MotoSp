package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.Adapter.HomePagerAdapter
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.activity_home2.*

class HomeActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)

        viewPager2.adapter = HomePagerAdapter(this)

    }
}