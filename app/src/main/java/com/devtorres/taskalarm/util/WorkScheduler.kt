package com.devtorres.taskalarm.util

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.devtorres.taskalarm.work.LocalNotificationWorker
import com.devtorres.taskalarm.work.UpdateTaskWorkerById

object WorkScheduler {
    fun scheduleInstantNotification(context: Context, title: String, content: String, requestCode: String){
        val workRequest = OneTimeWorkRequestBuilder<LocalNotificationWorker>()
            .setInputData(
                workDataOf(
                    "title" to title,
                    "content" to content,
                    "requestCode" to requestCode
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun scheduleReceiverTaskUpdate(context: Context, taskId: Int){
        val workRequest = OneTimeWorkRequestBuilder<UpdateTaskWorkerById>()
            .setInputData(
                workDataOf(
                    "taskId" to taskId
                )
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}