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

/**
 * Class for sending notification to the user about the dock updates
 */
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
     *  Run update and if journey updated, notify user about the new dock
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
                }
            }.collect()
        }
    }

    /**
     *  Check for a new dock based on the current docks locations and number of users
     *
     *  @@return Boolean journey update on pick up or drop off point of dock station
     */
    private fun checkUpdate(docks: MutableList<Dock>?): Boolean {

        //Get the saved dock locations from shared preferences
        SharedPrefHelper.initialiseSharedPref(
            getApplication(applicationContext),
            Constants.DOCKS_LOCATIONS
        )
        val currentDocks = SharedPrefHelper.getSharedPref(Point::class.java)

        //Get the saved number of cyclists from shared preferences
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
     *  Filter out to check existing assigned dock station to journey
     *  has been changed
     *
     *  @return Boolean journey update on pick up or drop off point of dock station
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

        //To filter the necessary dock to check
        val filteredDock = docks?.filter {
            val point = Point.fromLngLat(it.lon, it.lat)
            currentPoint.contains(point)
        }

        return checkForNewDocksAvailability(currentPoint, filteredDock!!, numCyclists)
    }

    /**
     *  Check if there is a new dock with free spaces and free bikes
     *  If number of bikes or number of spaces to pick up and drop off respectively
     *  is less than number of cyclists, then we notify the user via notification
     *  that their journey is likely to be updated
     *
     *  @@return Boolean journey update on pick up or drop off point of dock station
     */
    private fun checkForNewDocksAvailability(
        currentPoint: MutableList<Point>,
        filteredDock: List<Dock>,
        numCyclists: MutableList<String>
    ): Boolean{

        for (i in filteredDock.indices)
        {
            //If even which means pick up point, check for number of bikes available
            if(i % 2 != 0){
                if (filteredDock[i].nbBikes >= numCyclists.first()
                        .toInt() && filteredDock.size == currentPoint.size
                ) {
                    return false
                }
            }else{

                //If even which means drop off point, check for number of spaces available
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
            val name = context.getString(R.string.backstreet_notification)
            val descriptionText = context.getString(R.string.journey_update)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(context.getString(R.string.channel_id), name, importance).apply {
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

        val notificationBuilder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.dock_station)
            .setContentTitle(context.getString(R.string.journey_update_2))
            .setContentText(context.getString(R.string.open_app_to_see_changes))
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
