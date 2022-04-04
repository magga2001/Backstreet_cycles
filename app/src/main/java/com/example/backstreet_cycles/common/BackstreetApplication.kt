package com.example.backstreet_cycles.common

//import androidx.hilt.work.HiltWorkerFactory
//import androidx.work.Configuration

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.work.Configuration
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.service.WorkHelper
import com.example.backstreet_cycles.ui.views.LogInActivity
import com.google.android.gms.common.api.internal.LifecycleActivity
import com.google.android.gms.common.api.internal.LifecycleCallback
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


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