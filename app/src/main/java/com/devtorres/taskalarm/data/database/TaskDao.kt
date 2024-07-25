package com.devtorres.taskalarm.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.devtorres.taskalarm.data.model.Task

@Dao
interface TaskDao {
    // insertar tarea
    @Insert
    suspend fun insertTask(task: Task)

    // actualizar tarea
    @Update
    suspend fun updateTask(task: Task)

    // borrar tarae
    @Delete
    suspend fun deleteTask(task: Task)

    // obtener todas las tareas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>
}