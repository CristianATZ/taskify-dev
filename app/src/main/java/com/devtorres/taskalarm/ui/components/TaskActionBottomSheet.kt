package com.devtorres.taskalarm.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.SecureFlagPolicy
import com.devtorres.taskalarm.R
import com.devtorres.taskalarm.data.model.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskActionsBottomSheet(
    selectedTask: Task,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    onComplete: () -> Unit = {},
    onShare: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        properties = ModalBottomSheetProperties(
            isFocusable = true,
            securePolicy = SecureFlagPolicy.SecureOn,
            shouldDismissOnBackPress = true
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TaskItem(task = selectedTask)

            Spacer(modifier = Modifier.size(32.dp))

            ActionButtonBottomSheet(
                onClick = onUpdate,
                icon = Icons.Outlined.Edit,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.secondary,
                    contentColor = colorScheme.onSecondary
                ),
                textResId = R.string.btnUpdateTask
            )

            if(!selectedTask.isCompleted && selectedTask.reminder){
                Spacer(modifier = Modifier.size(16.dp))

                ActionButtonBottomSheet(
                    onClick = onComplete,
                    icon = Icons.Outlined.Done,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    textResId = R.string.btnDoneTask
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            ActionButtonBottomSheet(
                onClick = onShare,
                icon = Icons.Outlined.Share,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.tertiary,
                    contentColor = colorScheme.onTertiary
                ),
                textResId = R.string.btnShareTask
            )

            Spacer(modifier = Modifier.size(16.dp))

            ActionButtonBottomSheet(
                onClick = onDelete,
                icon = Icons.Outlined.Delete,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colorScheme.error,
                    contentColor = colorScheme.onError
                ),
                textResId = R.string.btnDeleteTask
            )

            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun ActionButtonBottomSheet(
    onClick: () -> Unit,
    icon: ImageVector,
    textResId: Int,
    colors: ButtonColors,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = { onClick() },
        shape = CardDefaults.shape,
        colors = colors,
        modifier = modifier
            .fillMaxWidth(0.95f)
            .height(50.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.size(16.dp))
        Text(text = stringResource(id = textResId))
    }
}