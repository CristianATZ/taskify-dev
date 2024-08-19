package com.devtorres.taskalarm.ui.task

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    val isDarkTheme by settingsViewModel.theme.collectAsState(initial = false)

    val themeStringId = if(isDarkTheme) R.string.lblDarkTheme else R.string.lblLightTheme
    val notificationStringId = if(isGranted) R.string.lblNotiActi else R.string.lblNotiDes

    Scaffold(
        topBar = { TopBarSettings() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            SettingsRow(
                icon = Icons.Outlined.DarkMode,
                contentDescription = null,
                text = stringResource(id = themeStringId),
                isChecked = isDarkTheme,
                onCheckedChange = { settingsViewModel.saveTheme(it) }
            )

            SettingsRow(
                icon = Icons.Outlined.Notifications,
                contentDescription = null,
                text = stringResource(id = notificationStringId),
                isChecked = isGranted,
                onCheckedChange = {  }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSettings() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.lblSettings),
                style = typography.headlineSmall,
                fontWeight = FontWeight.W900
            )
        }
    )
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    contentDescription: String?,
    text: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .border(1.dp, colorScheme.outline.copy(0.25f), RoundedCornerShape(8.dp)),
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
            enabled = enabled,
            onCheckedChange = {
                onCheckedChange(it)
            }
        )

        Spacer(modifier = Modifier.size(16.dp))
    }

    Spacer(modifier = Modifier.size(16.dp))
}