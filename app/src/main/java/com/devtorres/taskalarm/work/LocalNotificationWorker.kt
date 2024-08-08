package com.devtorres.taskalarm.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devtorres.taskalarm.util.NotificationHelper

class LocalNotificationWorker(context: Context, workerParameters: WorkerParameters): Worker(context,workerParameters) {
    override fun doWork(): Result {

        val title = inputData.getString("title") ?: "Titulo prefeterminado"
        val content = inputData.getString("content") ?: "Contenido prefeterminado"
        val requestCode = inputData.getString("requestCode") ?: "1"

        NotificationHelper.showTaskNotification(applicationContext, title, content, requestCode.toInt())

        return Result.success()
    }

}