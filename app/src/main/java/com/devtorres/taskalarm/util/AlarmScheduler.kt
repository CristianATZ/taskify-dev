package com.devtorres.taskalarm.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

object AlarmScheduler {

    private fun getPendingIntent(context: Context, requestCode: Int, title: String, content: String): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("content", content)
            putExtra("requestCode", requestCode.toString()  )
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Programa una alarma para una fecha y hora exactas.
     */
    fun scheduleAlarmOnExactDate(
        context: Context,
        requestCode: Int,
        title: String,
        content: String,
        calendar: Calendar
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context, requestCode, title, content)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    /**
     * Cancelar una alarma
     */
    fun cancelAlarm(
        context: Context,
        title: String,
        content: String,
        requestCode: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(context, requestCode, title, content)

        alarmManager.cancel(pendingIntent)
    }
}
