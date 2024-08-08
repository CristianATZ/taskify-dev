package com.devtorres.taskalarm.data.repository

import com.devtorres.taskalarm.data.database.TaskDao
import com.devtorres.taskalarm.data.model.Task

interface TaskRepository {
    suspend fun insertTask(task: Task): Long
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun getAllTasks(): List<Task>
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao
): TaskRepository{
    // funcion para insertar tarea
    override suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    // funcion para actualizar tarea
    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    // funcion para eliminar tarea
    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    // funcion para obtener todas las tareas
    override suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRepositoryImpl? = null

        fun getInstance(taskDao: TaskDao): TaskRepositoryImpl {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskRepositoryImpl(taskDao).also { INSTANCE = it }
            }
        }
    }
}