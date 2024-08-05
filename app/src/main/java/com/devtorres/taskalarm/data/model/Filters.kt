package com.devtorres.taskalarm.data.model

import com.devtorres.taskalarm.util.TaskUtils.DateFilter
import com.devtorres.taskalarm.util.TaskUtils.StatusFilter

data class Filters(
    var status: StatusFilter = StatusFilter.ALL,
    var date: DateFilter = DateFilter.ALL
)