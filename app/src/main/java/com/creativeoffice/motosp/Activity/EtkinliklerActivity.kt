package com.creativeoffice.motosp.Activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.creativeoffice.motosp.Adapter.EtkinliklerAdapter
import com.creativeoffice.motosp.Datalar.EtkinlikData
import com.creativeoffice.motosp.R
import com.creativeoffice.motosp.utils.BottomnavigationViewHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_etkinlikler.*
import kotlinx.android.synthetic.main.activity_etkinlikler.bottomNavigationView
import kotlinx.android.synthetic.main.add_etkinlik.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class EtkinliklerActivity : AppCompatActivity() {
    private val ACTIVITY_NO = 1
    var etkinliklerList = ArrayList<EtkinlikData>()

    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_etkinlikler)
        // this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
       // this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setupNavigationView()
        setupRecyclerView()

        mAuth = FirebaseAuth.getInstance()
        userID = mAuth.currentUser!!.uid

        butonlar()
    }

    private fun butonlar() {
        imgEkle.setOnClickListener {

            val dialogView = LayoutInflater.from(this).inflate(R.layout.add_etkinlik, null)

            dialogView.tvEtkinlikZamani.text = "Etkinlik Zamanı: " + SimpleDateFormat("HH:mm dd.MM.yyyy").format(System.currentTimeMillis())
            var cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "HH:mm dd.MM.yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale("tr"))
                dialogView.tvEtkinlikZamani.text = sdf.format(cal.time)
            }
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
            }
            dialogView.tvEtkinlikZamani.setOnClickListener {

                DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

            }

            val builder = AlertDialog.Builder(this).setView(dialogView).setTitle("Etkinlik Oluştur")
                .setPositiveButton("Oluştur", DialogInterface.OnClickListener { dialog, which ->
                    var etkinlikKey = ref.child("Etkinlikler").push().key
                    var data = EtkinlikData(dialogView.etEtkinlikAdi.text.toString(),dialogView.etEtkinlikDetaylari.text.toString(), cal.timeInMillis, System.currentTimeMillis(), dialogView.etEtkinlikSehri.text.toString(),
                        dialogView.etEtkinlikNotu.text.toString(), dialogView.etKatilimciSayisi.text.toString().toInt(), userID, etkinlikKey,1)

                    ref.child("Etkinlikler").child(etkinlikKey.toString()).setValue(data)

                })

                .setNegativeButton("İptal", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })


            val alertDialog = builder.show()


        }
    }


    private fun setupRecyclerView() {

        ref.child("Etkinlikler").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChildren()) {
                    for (ds in p0.children) {
                        var gelendata = ds.getValue(EtkinlikData::class.java)
                        etkinliklerList.add(gelendata!!)
                    }
                    Log.e("sad", etkinliklerList.size.toString())
                    rcEtkinlikler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    //  rvModelListesi.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    val markaAdapter = EtkinliklerAdapter(this@EtkinliklerActivity, etkinliklerList)
                    rcEtkinlikler.adapter = markaAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


        //  rvModelListesi.setHasFixedSize(true)
        // ar_indicator_motor.attachTo(rvModelListesi, true)

    }


    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)
        finish()
        super.onBackPressed()
    }
    fun setupNavigationView() {

        BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this, bottomNavigationView) // Bottomnavhelper içinde setupNavigationda context ve nav istiyordu verdik...
        var menu = bottomNavigationView.menu
        var menuItem = menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }
}