package com.devtorres.taskalarm.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devtorres.taskalarm.util.NotificationHelper

class UpdateNotificationWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {

        val title = inputData.getString("title") ?: "Titulo predeterminado"
        val content = inputData.getString("content") ?: "Contenido prefeterminado"

        NotificationHelper.showUpdateNotification(applicationContext, title, content)

        return Result.success()
    }

}