package com.devtorres.taskalarm.ui.task

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.devtorres.taskalarm.R

@Composable
fun TaskScreen(modifier: Modifier = Modifier) {
    Scaffold(
        bottomBar = {
            BottomBarApp()
        },
        floatingActionButton = {
            FloatingActionApp()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // title
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            )

            // description
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight(0.8f)
            )

            // accept task
            Button(onClick = { /*TODO*/ }) {
                Text(text = stringResource(id = R.string.btnAccept))
            }
        }
    }
}

@Composable
fun FloatingActionApp() {
    SmallFloatingActionButton(onClick = { /*TODO*/ }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
    }
}

@Composable
fun BottomBarApp() {
    BottomAppBar(
        actions = {
            NavigationDrawerItem(
                label = {
                    Text(text = stringResource(id = R.string.lblTask))
                },
                selected = false,
                onClick = {

                }
            )
            NavigationDrawerItem(
                label = {
                    Text(text = stringResource(id = R.string.lblTheme))
                },
                selected = false,
                onClick = {

                }
            )
        }

    )
}


@Preview
@Composable
private fun TaskPreview() {
    TaskScreen()
}
