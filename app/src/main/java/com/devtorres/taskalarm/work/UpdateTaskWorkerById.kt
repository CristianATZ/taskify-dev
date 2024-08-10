package com.devtorres.taskalarm.work

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devtorres.taskalarm.MyApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateTaskWorkerById(context: Context, workerParameters: WorkerParameters): Worker(context,workerParameters) {
    override fun doWork(): Result {
        val taskId = inputData.getInt("taskId", -1)
        if(taskId == -1) return Result.failure()

        val app = applicationContext as MyApp
        val repository = app.taskRepository

        CoroutineScope(Dispatchers.IO).launch {
            repository.updateTaskById(taskId)

            val intent = Intent("com.devtorres.taskalarm.TASK_RECEIVER_UDPATED")
            intent.putExtra("taskId", taskId)
            applicationContext.sendBroadcast(intent)
        }

        return Result.success()
    }
}