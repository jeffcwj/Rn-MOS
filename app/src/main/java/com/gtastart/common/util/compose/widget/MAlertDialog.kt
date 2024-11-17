package com.gtastart.common.util.compose.widget

import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gtastart.common.util.isBlank

@Composable
fun MAlertDialog(
    title: String,
    content: String,
    onDismissRequest: () -> Unit = {},
    positiveButtonText: String = "",
    onPositiveButtonClick: () -> Unit = {},
    negativeButtonText: String = "",
    onNegativeButtonClick: () -> Unit = {}
) {
    AlertDialog(
        title = {
            Text(title)
        },
        text = {
            Text(content)
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (positiveButtonText.isNotEmpty()) {
                MButton(
                    text = positiveButtonText,
                    onClick = onPositiveButtonClick
                )
            }
        },
        dismissButton = {
            if (negativeButtonText.isNotEmpty()) {
                MButton(
                    text = negativeButtonText,
                    onClick = onNegativeButtonClick
                )
            }
        }
    )
}

@Composable
fun MCustomAlertDialog(
    title: String,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit = {},
    positiveButtonText: String = "",
    onPositiveButtonClick: () -> Unit = {},
    negativeButtonText: String = "",
    onNegativeButtonClick: () -> Unit = {}
) {
    AlertDialog(
        title = {
            Text(title)
        },
        text = content,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            if (positiveButtonText.isNotEmpty()) {
                MButton(
                    text = positiveButtonText,
                    onClick = onPositiveButtonClick
                )
            }
        },
        dismissButton = {
            if (negativeButtonText.isNotEmpty()) {
                MButton(
                    text = negativeButtonText,
                    onClick = onNegativeButtonClick
                )
            }
        }
    )
}