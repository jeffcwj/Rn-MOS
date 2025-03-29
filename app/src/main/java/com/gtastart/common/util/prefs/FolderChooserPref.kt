package com.gtastart.common.util.prefs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.akira.tyranoemu.ui.prefs.LocalPrefsDataStore
import com.akira.tyranoemu.ui.prefs.PrefsListItem
import com.akira.tyranoemu.ui.prefs.ifNotNullThen
import com.gtastart.common.R
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.compose.widget.HtmlTextView
import com.heyanle.okkv2.core.getValue
import com.heyanle.okkv2.core.okkv

/**
 * Preference which shows a TextField in a Dialog
 *
 * @param key Key used to identify this Pref in the DataStore
 * @param title Main text which describes the Pref
 * @param modifier Modifier applied to the Text aspect of this Pref
 * @param summary Used to give some more information about what this Pref is for
 * @param dialogTitle Title shown in the dialog. No title if null.
 * @param dialogMessage Summary shown underneath [dialogTitle]. No summary if null.
 * @param defaultValue Default value that will be set in the TextField when the dialog is shown for the first time.
 * @param onValueSaved Will be called with new TextField value when the confirm button is clicked. It is NOT called every time the value changes. Use [onValueChange] for that.
 * @param onValueChange Will be called every time the TextField value is changed.
 * @param dialogBackgroundColor Color of the dropdown menu
 * @param textColor Text colour of the [title] and [summary]
 * @param enabled If false, this Pref cannot be clicked.
 */
@Composable
fun FolderChooserPref(
    key: String,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    dialogTitle: String? = title,
    defaultValue: String = "",
    label: String = "",
    onValueSaved: ((String) -> Unit) = {},
    onValueChange: ((String) -> Unit) = {},
    dialogBackgroundColor: Color = MaterialTheme.colorScheme.background,
    onImportClick: () -> Unit = {},
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true,
    valueAsSummary: Boolean = true,
    singleLine: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
    onSelectButtonClick: () -> Unit
) {

    var showDialog by rememberSaveable { mutableStateOf(false) }

    val datastore = LocalPrefsDataStore.current
    var realStore by remember { datastore.okkv(key, defaultValue) }
    val textVal by rememberUpdatedState(defaultValue)

    PrefsListItem(
        text = { Text(text = title, maxLines = 1) },
        modifier = if (enabled) modifier.clickable { showDialog = true } else modifier,
        enabled = enabled,
        darkenOnDisable = false,
        textColor = textColor,
        minimalHeight = false,
        icon = null,
        secondaryText = summary.ifNotNullThen { Text(text = summary!!) },
        trailing = trailingContent
    )

    if (showDialog) {
        AlertDialog(
            modifier = Modifier.focusable(),
            onDismissRequest = { showDialog = false },
            title = if (dialogTitle != null) {
                { Text(text = dialogTitle) }
            } else null,
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        OutlinedTextField(
                            value = textVal,
                            label = { Text(label) },
                            modifier = Modifier.weight(1f),
                            singleLine = singleLine,
                            onValueChange = {
                                //  textVal = it
                                onValueChange(it)
                            }
                        )
                        Button(
                            onClick = {
                                onSelectButtonClick()
                                realStore = textVal
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = stringResource(com.valvesoftware.source.R.string.select)
                            )
                        }
                    }
                    Text(
                        text = "从其它版本继承",//"使用其它版本的游戏资源",
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        modifier = Modifier.clickable {
                        onImportClick.invoke()
                    })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    realStore = textVal
                    onValueSaved(textVal)
                    showDialog = false
                }) {
                    Text(text = stringResource(R.string.btn_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.btn_cancel))
                }
            },
        )

    }
}
