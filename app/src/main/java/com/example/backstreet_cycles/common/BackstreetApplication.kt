package com.example.backstreet_cycles.common

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.backstreet_cycles.service.WorkHelper
import com.example.backstreet_cycles.utils.SharedPrefHelper
import com.google.firebase.auth.FirebaseAuth


class BackstreetApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Log.i("Starting application", "...")

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