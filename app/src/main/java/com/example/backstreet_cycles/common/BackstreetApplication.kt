package com.example.backstreet_cycles.common

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
//import androidx.hilt.work.HiltWorkerFactory
//import androidx.work.Configuration
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.model.dto.Locations
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BackstreetApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Creates the application
     */
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        SharedPrefHelper.initialiseSharedPref(this, Constants.DOCKS_LOCATIONS)

        Handler(Looper.myLooper()!!).postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) {
                //Move this to appropriate place
                WorkHelper.setPeriodicallySendingLogs(context = applicationContext)
            } else {
                WorkHelper.cancelWork(context = applicationContext)
            }
//            WorkHelper.cancelWork(context = applicationContext)
        }, 5000)
    }

    /**
     * Set up configuration for work manager
     */
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}