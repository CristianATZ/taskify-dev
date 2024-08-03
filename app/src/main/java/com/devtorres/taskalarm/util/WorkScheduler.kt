package com.devtorres.taskalarm.util

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.devtorres.taskalarm.work.LocalNotificationWorker

object WorkScheduler {
    fun scheduleInstantNotification(context: Context, title: String, content: String){
        val workRequest = OneTimeWorkRequestBuilder<LocalNotificationWorker>()
            .setInputData(
                workDataOf(
                    "title" to title,
                    "content" to content
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}