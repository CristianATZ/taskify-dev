package com.devtorres.taskalarm.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.devtorres.taskalarm.data.model.Task
import java.time.LocalDateTime

object TaskUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    val emptyTask = Task(
        id = -1,
        title = "",
        isCompleted = false,
        reminder = false,
        finishDate = LocalDateTime.now()
    )

    enum class StatusFilter { ALL, COMPLETED, UNCOMPLETED, NONE }

    enum class DateFilter { ALL, TODAY, WEEK, MONTH, NONE }
}