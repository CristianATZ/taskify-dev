package com.devtorres.taskalarm.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R

@Composable
fun AboutDialog(
    closeDialog: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = { closeDialog() }) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(id = R.string.infoAboutTitle),
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.size(32.dp))

                Text(
                    text = "${stringResource(id = R.string.lblVersion)}: ${context.packageManager.getPackageInfo(context.packageName, 0).versionName}"
                )
            }
        }
    }
}