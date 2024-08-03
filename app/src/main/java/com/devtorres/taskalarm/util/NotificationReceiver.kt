package com.devtorres.taskalarm.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver

@SuppressLint("RestrictedApi")
class NotificationReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Notificación"
        val content = intent?.getStringExtra("content") ?: "Contenido de la notificación."

        // Utiliza NotificationHelper para mostrar la notificación
        NotificationHelper.showTaskNotification(context, title, content)
    }
}