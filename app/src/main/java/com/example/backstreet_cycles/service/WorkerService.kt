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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.backstreet_cycles.DTO.Dock
import com.example.backstreet_cycles.R
import com.example.backstreet_cycles.interfaces.CallbackListener
import com.example.backstreet_cycles.views.HomePageActivity
import com.example.backstreet_cycles.views.LogInActivity
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.navigation.utils.internal.NOTIFICATION_ID

class WorkerService(context: Context, userParameters: WorkerParameters) :
    Worker(context, userParameters) {

    override fun doWork(): Result {

        attemptNotification()

        return Result.success()
    }

    private fun attemptNotification()
    {
        NetworkManager.getDock(context = applicationContext,

            object : CallbackListener<MutableList<Dock>> {
                override fun getResult(objects: MutableList<Dock>) {

                    //Do whatever with shared preference...

                    if(checkUpdate(objects))
                    {
                        createNotificationChannel(context = applicationContext)
                        notificationTapAction(context = applicationContext)
                    }

                }
            }
        )
    }

    private fun checkUpdate(docks: MutableList<Dock>): Boolean
    {
        Log.i("Dock Application", docks.size.toString())

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