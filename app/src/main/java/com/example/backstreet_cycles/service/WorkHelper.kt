package com.example.backstreet_cycles.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.backstreet_cycles.common.Constants
import java.util.concurrent.TimeUnit

object WorkHelper {

    /**
     *  Send notification to the user that the dock was updated
     */
    fun setPeriodicallySendingLogs(context: Context) {

        val workManager = WorkManager.getInstance(context)
        val sendingNotification = PeriodicWorkRequestBuilder<WorkerService>(15, TimeUnit.MINUTES)
            .addTag(Constants.TAG_NOTIFICATION)
            .build()

        workManager.enqueueUniquePeriodicWork(
            Constants.TAG_NOTIFICATION,
            ExistingPeriodicWorkPolicy.KEEP,
            sendingNotification
        )
    }

    /**
     * Delete all enqueued notifications
     */
    fun cancelWork(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(Constants.TAG_NOTIFICATION)
    }
}