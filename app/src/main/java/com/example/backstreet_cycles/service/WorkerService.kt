package com.example.backstreet_cycles.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.common.Constants
import com.example.backstreet_cycles.common.Resource
import com.example.backstreet_cycles.domain.model.dto.Dock
import com.example.backstreet_cycles.domain.repositoryInt.TflRepository
import com.example.backstreet_cycles.domain.utils.SharedPrefHelper
import com.example.backstreet_cycles.ui.views.HomePageActivity
import com.example.backstreet_cycles.ui.views.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.geojson.Point
import com.mapbox.navigation.utils.internal.NOTIFICATION_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@HiltWorker
class WorkerService @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted userParameters: WorkerParameters,
    private val tflRepository: TflRepository,
) : Worker(context, userParameters) {

    override fun doWork(): Result {

        runBlocking { attemptNotification() }

        return Result.success()
    }

    private suspend fun attemptNotification()
    {

        Log.i("Starting attempt", "Success")

        runBlocking {
            tflRepository.getDocks().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("Notification dock", result.data?.size.toString())

                            if(checkUpdate(result.data))
                            {
                                createNotificationChannel(context = applicationContext)
                                notificationTapAction(context = applicationContext)
                            }
                    }

                    is Resource.Error -> {
                        Log.i("New dock", "Error")

                    }
                    is Resource.Loading -> {
                        Log.i("New dock", "Loading...")
                    }
                }
            }.collect()
        }
    }

    private fun checkUpdate(docks: MutableList<Dock>?): Boolean
    {
        SharedPrefHelper.initialiseSharedPref(getApplication(applicationContext),Constants.DOCKS_LOCATIONS)
        val currentDocks = SharedPrefHelper.getSharedPref(Point::class.java)
        SharedPrefHelper.initialiseSharedPref(getApplication(applicationContext),Constants.NUM_USERS)
        val numUser = SharedPrefHelper.getSharedPref(String::class.java)
        numUser.map { it.toInt() }

       return checkForNewDock(currentDocks, docks, numUser)
    }

    private fun checkForNewDock(
        currentDocks: MutableList<Point>,
        docks: MutableList<Dock>?,
        numUser: MutableList<String>
    ): Boolean{
        val currentPoint = mutableListOf<Point>()

        for(point in currentDocks)
        {
            val lon = point.longitude()
            val lat = point.latitude()

            currentPoint.add(Point.fromLngLat(lon,lat))
        }

        val filteredDock = docks?.filter {
            val point = Point.fromLngLat(it.lon, it.lat)
            currentPoint.contains(point)
        }


        for(dock in filteredDock!!)
            if(dock.nbSpaces >= numUser.first().toInt() && filteredDock.size == currentPoint.size)
            {
                return false
            }

        return true
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Backstreet cycle Notification"
            val descriptionText = "Journey update"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("1337", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notificationTapAction(context: Context)
    {
        // Create an explicit intent for an Activity in your app

        val intent : Intent

        if(FirebaseAuth.getInstance().currentUser != null)
        {
            intent = Intent(context, HomePageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }else
        {
            intent = Intent(context, LogInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

       val notificationBuilder = NotificationCompat.Builder(context, "1337")
            .setSmallIcon(R.drawable.dock_station)
            .setContentTitle("Journey update!")
            .setContentText("One or more of docking stations in your journey has been changed due to spaces available")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        showNotification(context, notificationBuilder)
    }

    private fun showNotification(context: Context, notificationBuilder: NotificationCompat.Builder)
    {
        Log.i("Sending notification", "Successful")

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

}
