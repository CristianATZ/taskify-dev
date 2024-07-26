package com.devtorres.taskalarm.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devtorres.taskalarm.R
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel
) {
    val isDarkTheme by settingsViewModel.theme.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    val stringId = if(isDarkTheme) R.string.lblDarkTheme else R.string.lblLightTheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = stringId)
            )

            Switch(
                checked = isDarkTheme,
                onCheckedChange = {
                    coroutineScope.launch {
                        settingsViewModel.changeTheme(it)
                    }
                }
            )
        }
    }
}