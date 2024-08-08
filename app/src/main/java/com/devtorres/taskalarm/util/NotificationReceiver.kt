package com.devtorres.taskalarm.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver

@SuppressLint("RestrictedApi")
class NotificationReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Notificación"
        val content = intent?.getStringExtra("content") ?: "Contenido de la notificación."
        val requestCode = intent?.getStringExtra("requestCode") ?: "1"

        Log.d("REQUESTCODE", "receiver $requestCode")

        // Utiliza NotificationHelper para mostrar la notificación
        NotificationHelper.showTaskNotification(context, title, content, requestCode.toInt())
    }
}