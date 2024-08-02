package com.devtorres.taskalarm.util

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult

object ShareHelper {
    fun sendTask(
        context: Context,
        information: String,
        shareLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>, ) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, information)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareLauncher.launch(shareIntent)
    }
}