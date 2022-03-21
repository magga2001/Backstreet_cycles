package com.example.backstreet_cycles.service

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth


class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Handler(Looper.myLooper()!!).postDelayed({
            if(FirebaseAuth.getInstance().currentUser != null)
            {
                //Move this to appropriate place
                WorkHelper.setPeriodicallySendingLogs(context = applicationContext)

            }else
            {
                WorkHelper.cancelWork(context = applicationContext)
            }
        },5000)

    }

}