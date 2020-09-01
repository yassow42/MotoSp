package com.creativeoffice.motosp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.creativeoffice.motosp.R
import kotlinx.android.synthetic.main.activity_etkinlik_detay.*
import kotlinx.android.synthetic.main.item_etkinlikler.*
import kotlinx.android.synthetic.main.item_etkinlikler.tvEtkinlikAdi

class EtkinlikDetayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etkinlik_detay)
        tvEtkinlikAdi.text = intent.getStringExtra("etkinlik_adi").toString()
    }
}