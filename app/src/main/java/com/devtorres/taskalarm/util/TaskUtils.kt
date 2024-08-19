package com.devtorres.taskalarm.util

import com.devtorres.taskalarm.data.model.Task
import com.devtorres.taskalarm.data.model.TaskValidationsBoolean
import java.time.LocalDateTime

object TaskUtils {
    val emptyTask = Task(
        id = -1,
        title = "",
        isCompleted = false,
        reminder = false,
        finishDate = LocalDateTime.now(),
        expired = false
    )

    val emptyValidationsState = TaskValidationsBoolean(title = true, date = true, time = true)
}