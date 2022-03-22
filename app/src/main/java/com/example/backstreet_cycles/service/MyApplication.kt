package com.example.backstreet_cycles.service

import android.app.Application
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import com.example.backstreet_cycles.utils.SharedPrefHelper
import com.google.firebase.auth.FirebaseAuth


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        SharedPrefHelper.initialiseSharedPref(this, "DOCKS_LOCATIONS")

        Handler(Looper.myLooper()!!).postDelayed({
            if(FirebaseAuth.getInstance().currentUser != null)
            {
                //Move this to appropriate place
                WorkHelper.setPeriodicallySendingLogs(context = applicationContext)

            }else
            {
                WorkHelper.cancelWork(context = applicationContext)
            }
//            WorkHelper.cancelWork(context = applicationContext)
        },5000)

    }

}