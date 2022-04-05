package com.example.backstreet_cycles

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.runner.AndroidJUnitRunner
import androidx.work.Configuration
import dagger.hilt.android.testing.HiltTestApplication
import javax.inject.Inject

class BackstreetTestRunner : AndroidJUnitRunner(), Configuration.Provider{

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}