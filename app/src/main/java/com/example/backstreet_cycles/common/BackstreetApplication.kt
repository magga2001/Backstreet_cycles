package com.example.backstreet_cycles.common

import android.app.Application
import android.os.Handler
import android.os.Looper
//import androidx.hilt.work.HiltWorkerFactory
//import androidx.work.Configuration
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BackstreetApplication : Application() {

    companion object
    {
        var docks = mutableListOf<Dock>()
    }

//    @Inject
//    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        SharedPrefHelper.initialiseSharedPref(this, Constants.DOCKS_LOCATIONS)

        Handler(Looper.myLooper()!!).postDelayed({
//            if(FirebaseAuth.getInstance().currentUser != null)
//            {
//                //Move this to appropriate place
//                WorkHelper.setPeriodicallySendingLogs(context = applicationContext)
//
//            }else
//            {
//                WorkHelper.cancelWork(context = applicationContext)
//            }
            WorkHelper.cancelWork(context = applicationContext)
        },5000)

    }

//    override fun getWorkManagerConfiguration() =
//        Configuration.Builder()
//            .setWorkerFactory(workerFactory)
//            .build()

}