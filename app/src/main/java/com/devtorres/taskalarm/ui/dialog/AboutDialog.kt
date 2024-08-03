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
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.devtorres.taskalarm.R



@Composable
fun AboutDialog(
    closeDialog: () -> Unit
) {
    val context = LocalContext.current
    val version = "${stringResource(id = R.string.lblVersion)}: ${context.packageManager.getPackageInfo(context.packageName, 0).versionName}"
    val name = "${stringResource(id = R.string.app_name)} Application"

    Dialog(onDismissRequest = { closeDialog() }) {
        OutlinedCard {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    style = typography.headlineSmall
                )

                Text(
                    text = version,
                    style = typography.bodyLarge,
                    modifier = Modifier.graphicsLayer(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.size(32.dp))
                
                Text(
                    text = stringResource(id = R.string.lblCopyright),
                    style = typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer(alpha = 0.5f)
                )
                
            }
        }
    }
}