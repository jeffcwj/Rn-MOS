package com.gtastart.common.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.gtastart.common.theme.GtaStartTheme

class MOSDialog {

    companion object {
        fun show(
            context: Context,
            title: String? = null,
            customView: (@Composable (dialog: AlertDialog) -> Unit)? = null,
            message: String? = null,
            positiveButtonText: String? = null,
            negativeButtonText: String? = null,
            neutralButtonText: String? = null,
            onPositiveButtonClick: ((DialogInterface, Int) -> Unit)? = null,
            onNegativeButtonClick: ((DialogInterface, Int) -> Unit)? = null,
            onNeutralButtonClick: ((DialogInterface, Int) -> Unit)? = null,
            cancelable: Boolean = true
        ) {
            MDialog.show(
                context = context,
                title = title,
                customView = customView?.let {{ dialog ->
                    GtaStartTheme(darkTheme = true) {
                        Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                            customView(dialog)
                        }
                    }
                }},
                message = message,
                positiveButtonText = positiveButtonText,
                negativeButtonText =negativeButtonText,
                neutralButtonText = neutralButtonText,
                onPositiveButtonClick = onPositiveButtonClick,
                onNegativeButtonClick = onNegativeButtonClick,
                onNeutralButtonClick = onNeutralButtonClick,
                cancelable = cancelable
            )
        }
    }
}