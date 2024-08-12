package com.devtorres.taskalarm.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
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
        return isNoAssigment() && (date >= LocalDate.now())
    }

    fun isTime(): Boolean {
        return isNoAssigment() && (time >= LocalTime.now())
    }

    fun isValid(): Boolean {
        return isDate() && isTime()
    }
}

data class TaskValidationsBoolean(
    var title: Boolean = true,
    var date: Boolean = true,
    var time: Boolean = true
)