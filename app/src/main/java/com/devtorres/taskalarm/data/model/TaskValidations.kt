package com.devtorres.taskalarm.data.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class TaskValidationsBoolean(
    var title: Boolean = true,
    var date: Boolean = true,
    var time: Boolean = true,
    var subtask: Boolean = true
) {
    fun isValid(): Boolean {
        return title && date && time && subtask
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