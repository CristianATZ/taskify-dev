package com.devtorres.taskalarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    var subtasks: List<SubTask> = emptyList(),
    var isCompleted: Boolean,
    val reminder: Boolean,
    val expired: Boolean,
    val finishDate: LocalDateTime
)