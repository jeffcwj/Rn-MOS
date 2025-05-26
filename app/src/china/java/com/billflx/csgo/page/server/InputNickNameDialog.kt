package com.billflx.csgo.page.server

import androidx.compose.foundation.focusable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.valvesoftware.source.R

@Composable
fun InputNickNameDialog(
    modifier: Modifier = Modifier,
    text: MutableState<String>,
    onStartGameClick: () -> Unit,
    onDismiss: () -> Unit
) {
    MCustomAlertDialog(
        modifier = modifier,
        title = stringResource(R.string.please_input_nickname),
        content = {
            TextField(
                modifier = Modifier.focusable(),
                value = text.value,
                onValueChange = {
                    text.value = it
                },
                maxLines = 1,
                singleLine = true,
                label = { Text(stringResource(R.string.please_input_nickname)) }
            )
        },
        positiveButtonText = stringResource(R.string.start_game),
        onPositiveButtonClick = onStartGameClick,
        onDismissRequest = onDismiss
    )
}