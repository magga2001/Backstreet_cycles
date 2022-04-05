package com.example.backstreet_cycles.common

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * Custom application for the whole application
 */
@HiltAndroidApp
class BackstreetApplication : Application(), Configuration.Provider{

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Creates the application
     */
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        SharedPrefHelper.initialiseSharedPref(this, Constants.DOCKS_LOCATIONS)

//      check if there is a current user to turn on the notification
        Handler(Looper.myLooper()!!).postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) {
                WorkHelper.setPeriodicallySendingLogs(context = applicationContext)
            } else {
                WorkHelper.cancelWork(context = applicationContext)
            }
        }, Constants.WAITING_TIME)
    }

    /**
     * Set up configuration for work manager
     */
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}