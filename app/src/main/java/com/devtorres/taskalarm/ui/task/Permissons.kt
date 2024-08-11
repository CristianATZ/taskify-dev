package com.devtorres.taskalarm.ui.task

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.devtorres.taskalarm.R

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    showMessage: (String) -> Unit
) {
    val context = LocalContext.current

    val lblNotificationGranted = stringResource(id = R.string.lblNotificationsPermissionsGranted)
    val lblNotificationNoGranted = stringResource(id = R.string.lblNotificationsPermissionsNoGranted)

    // verifica en que android se esta ejecutando la aplicacion
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // verifica si ya se habia dado el permiso
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            // ejecuta la peticion del permiso al ususario
            PermissionRequestEffect(permission = Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
                if(isGranted) {
                    showMessage(lblNotificationGranted)
                } else {
                    showMessage(lblNotificationNoGranted)
                }
            }
        }
    }
}

@Composable
fun PermissionRequestEffect(permission: String, onResult: (Boolean) -> Unit) {
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            onResult(it)
        }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(permission)
    }
}