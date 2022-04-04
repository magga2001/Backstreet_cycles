package com.example.backstreet_cycles.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
import com.example.backstreet_cycles.ui.views.LoadingActivity
import com.example.backstreet_cycles.ui.views.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.geojson.Point
import com.mapbox.navigation.utils.internal.NOTIFICATION_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@HiltWorker
class WorkerService @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted userParameters: WorkerParameters,
    private val tflRepository: TflRepository,
) : Worker(context, userParameters) {

    /**
     *  Start worker service
     */
    override fun doWork(): Result {

        runBlocking { attemptNotification() }

        return Result.success()
    }

    /**
     *  Run update and notify user about the new dock
     */
    private suspend fun attemptNotification() {

        runBlocking {
            tflRepository.getDocks().onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        if (checkUpdate(result.data)) {
                            createNotificationChannel(context = applicationContext)
                            notificationTapAction(context = applicationContext)
                        }
                    }
                    is Resource.Error -> {
                    }
                    is Resource.Loading -> {
                    }
                }
            }.collect()
        }
    }

    /**
     *  Check for a new dock based on the current docks locations and number of users
     */
    private fun checkUpdate(docks: MutableList<Dock>?): Boolean {

        SharedPrefHelper.initialiseSharedPref(
            getApplication(applicationContext),
            Constants.DOCKS_LOCATIONS
        )
        val currentDocks = SharedPrefHelper.getSharedPref(Point::class.java)

        SharedPrefHelper.initialiseSharedPref(
            getApplication(applicationContext),
            Constants.NUM_CYCLISTS
        )
        val numCyclists = SharedPrefHelper.getSharedPref(String::class.java)
        numCyclists.map { it.toInt() }

        //If there is no current journey
        if(currentDocks.isEmpty() && numCyclists.isEmpty()){
            return false
        }

        return checkForNewDock(currentDocks, docks, numCyclists)
    }

    /**
     *  Check if there is a new dock with free spaces
     */
    private fun checkForNewDock(
        currentDocks: MutableList<Point>,
        docks: MutableList<Dock>?,
        numCyclists: MutableList<String>
    ): Boolean {

        val currentPoint = mutableListOf<Point>()

        for (point in currentDocks) {
            val lon = point.longitude()
            val lat = point.latitude()

            currentPoint.add(Point.fromLngLat(lon, lat))
        }

        val filteredDock = docks?.filter {
            val point = Point.fromLngLat(it.lon, it.lat)
            currentPoint.contains(point)
        }

        return checkForNewDocksAvailability(currentPoint, filteredDock!!, numCyclists)
    }

    private fun checkForNewDocksAvailability(
        currentPoint: MutableList<Point>,
        filteredDock: List<Dock>,
        numCyclists: MutableList<String>
    ): Boolean{

        for (i in filteredDock.indices)
        {
            if(i % 2 != 0){
                if (filteredDock[i].nbBikes >= numCyclists.first()
                        .toInt() && filteredDock.size == currentPoint.size
                ) {
                    return false
                }
            }else{
                if (filteredDock[i].nbSpaces >= numCyclists.first()
                        .toInt() && filteredDock.size == currentPoint.size
                ) {
                    return false
                }
            }
        }

        return true
    }

    /**
     *  Create a notification channel
     */
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

    /**
     *  Create notification for a new Journey update
     */
    private fun notificationTapAction(context: Context) {
        // Create an explicit intent for an Activity in your app

        val intent: Intent

        if (FirebaseAuth.getInstance().currentUser != null) {
            intent = Intent(context, LoadingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            intent = Intent(context, LogInActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(context, "1337")
            .setSmallIcon(R.drawable.dock_station)
            .setContentTitle("Journey update!")
            .setContentText("Open the app to see the new changes in your journey.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        showNotification(context, notificationBuilder)
    }

    /**
     *  Display notification
     */
    private fun showNotification(
        context: Context,
        notificationBuilder: NotificationCompat.Builder
    ) {

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

}
