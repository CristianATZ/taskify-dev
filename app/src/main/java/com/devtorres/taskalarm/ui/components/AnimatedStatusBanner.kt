package com.devtorres.taskalarm.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedStatusBanner(
    isVisible: Boolean,
    color: Color,
    onColor: Color,
    icon: ImageVector,
    text: String
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it }, // Enter from the bottom
            animationSpec = tween(durationMillis = 600) // Duration of the slide in
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }, // Exit to the bottom
            animationSpec = tween(durationMillis = 1000) // Duration of the slide out
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize(0.25f)
                    .background(color),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = onColor,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = text,
                    style = typography.headlineSmall,
                    color = onColor
                )
            }
        }
    }
}