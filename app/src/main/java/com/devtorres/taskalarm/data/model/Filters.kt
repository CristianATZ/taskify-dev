package com.devtorres.taskalarm.data.model

enum class TypeFilter { ALL, REMINDER, NOREMINDER }

enum class StatusFilter { NONE, COMPLETED, UNCOMPLETED }

enum class DateFilter { NONE, TODAY, WEEK, MONTH }

data class Filters(
    var type: TypeFilter = TypeFilter.ALL,
    var status: StatusFilter = StatusFilter.NONE,
    var date: DateFilter = DateFilter.NONE
)