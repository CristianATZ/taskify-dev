package com.devtorres.taskalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.absoluteValue

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    var isCompleted: Boolean,
    val reminder: Boolean,
    val finishDate: LocalDateTime
)