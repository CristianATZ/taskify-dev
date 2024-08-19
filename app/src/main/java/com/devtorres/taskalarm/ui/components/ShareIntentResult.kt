package com.devtorres.taskalarm.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
fun shareIntentResult(
    context: Context,
    taskSuccess: String,
    taskFailure: String,
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, taskSuccess, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, taskFailure, Toast.LENGTH_SHORT).show()
        }
    }
}