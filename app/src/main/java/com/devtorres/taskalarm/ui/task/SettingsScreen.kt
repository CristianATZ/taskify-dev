package com.devtorres.taskalarm.ui.task

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devtorres.taskalarm.R
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val isDarkTheme by settingsViewModel.theme.collectAsState(initial = false)
    val isNotificationEnabled by settingsViewModel.notification.collectAsState(initial = true)

    val themeStringId = if(isDarkTheme) R.string.lblDarkTheme else R.string.lblLightTheme
    val notiStringId = if(isNotificationEnabled) R.string.lblNotiActi else R.string.lblNotiDes

    Scaffold(
        topBar = { TopBarSettings() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingsRow(
                icon = Icons.Filled.DarkMode,
                contentDescription = null,
                text = stringResource(id = themeStringId),
                isChecked = isDarkTheme,
                onCheckedChange = { settingsViewModel.saveTheme(it) }
            )

            SettingsRow(
                icon = Icons.Filled.Notifications,
                contentDescription = null,
                text = stringResource(id = notiStringId),
                isChecked = isNotificationEnabled,
                onCheckedChange = { settingsViewModel.saveNotification(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSettings() {
    TopAppBar(title = { Text(text = stringResource(id = R.string.lblSettings)) })
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    contentDescription: String?,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .border(1.dp, colorScheme.onBackground.copy(0.25f), RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.size(16.dp))

        Icon(
            imageVector = icon,
            contentDescription = contentDescription
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(text = text)

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isChecked,
            onCheckedChange = {
                coroutineScope.launch {
                    onCheckedChange(it)
                }
            }
        )

        Spacer(modifier = Modifier.size(16.dp))
    }

    Spacer(modifier = Modifier.size(16.dp))
}