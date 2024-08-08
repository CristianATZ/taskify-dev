package com.devtorres.taskalarm.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devtorres.taskalarm.R

object NotificationHelper {
    private const val TASK_CHANNEL_ID = "task_notifications"
    private const val UPDATE_CHANNEL_ID = "update_notifications"

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannels(context: Context){
        // canal para tareas
        val taskChannelName = "Notificaciones de Tareas"
        val taskChannelDescription = "Notificaciones relacionadas con Tareas"
        val taskChannelImportance = NotificationManager.IMPORTANCE_HIGH
        val taskChannel = NotificationChannel(TASK_CHANNEL_ID, taskChannelName, taskChannelImportance).apply {
            description = taskChannelDescription
        }

        // canal para notificaciones
        val updateChannelName = "Notificaciones de Actualizaciones"
        val updateChannelDescription = "Notificaciones relacionadas con Actualizaciones"
        val updateChannelImportance = NotificationManager.IMPORTANCE_HIGH
        val updateChannel = NotificationChannel(UPDATE_CHANNEL_ID, updateChannelName, updateChannelImportance).apply {
            description = updateChannelDescription
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // registrar los canales en el sistema
        notificationManager.createNotificationChannel(taskChannel)
        notificationManager.createNotificationChannel(updateChannel)
    }

    // heads-up notification (notificacion emergente)
    @SuppressLint("MissingPermission")
    fun showTaskNotification(context: Context, title: String, content: String, requestCode: Int){
        val builder = NotificationCompat.Builder(context, TASK_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(content)
            .setVibrate(LongArray(0))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            notify(requestCode, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
    fun showUpdateNotification(context: Context, title: String, content: String){
        val builder = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(2, builder.build())
        }
    }
}