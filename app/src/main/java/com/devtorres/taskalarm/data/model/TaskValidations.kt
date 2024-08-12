package com.devtorres.taskalarm.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
data class TaskValidations(
    var title: String = "",
    var date: LocalDate = LocalDate.now(),
    var time: LocalTime = LocalTime.now()
) {
    fun isNoAssigment(): Boolean {
        return title.isNotEmpty()
    }

    fun isDate(): Boolean {
        return date >= LocalDate.now()
    }

    fun isTime(): Boolean {
        return time >= LocalTime.now()
    }

    fun isDateTime(): Boolean {
        return date.atTime(time.hour, time.minute) >= LocalDateTime.now()
    }

    fun isValid(): Boolean {
        return isDateTime() && isNoAssigment()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class TaskValidationsBoolean(
    var title: Boolean = true,
    var date: Boolean = true,
    var time: Boolean = true
) {
    fun isValid(): Boolean {
        return title && date && time
    }

    fun isNoAssigment(title: String): Boolean {
        return title.isNotEmpty()
    }

    fun isDate(date: LocalDate): Boolean {
        return date >= LocalDate.now()
    }

    fun isTime(time: LocalTime): Boolean {
        return time >= LocalTime.now()
    }

    fun isDateTime(date: LocalDate, time: LocalTime): Boolean {
        return date.atTime(time.hour, time.minute) >= LocalDateTime.now()
    }
}