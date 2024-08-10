package com.devtorres.taskalarm

import android.app.Application
import com.devtorres.taskalarm.data.database.AppDataBase
import com.devtorres.taskalarm.data.repository.TaskRepository
import com.devtorres.taskalarm.data.repository.TaskRepositoryImpl

class MyApp : Application() {
    val database by lazy { AppDataBase.getInstance(this) }
    val taskRepository by lazy { TaskRepositoryImpl.getInstance(database.taskDao()) }

    override fun onCreate() {
        super.onCreate()
        // inicilizacion global aqui
    }
}