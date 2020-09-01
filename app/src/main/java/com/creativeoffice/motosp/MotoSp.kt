package com.creativeoffice.motosp

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

public class MotoSp : Application(){
    override fun onCreate() {
        super.onCreate()

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}