package com.gtastart.common.util.prefs

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.akira.tyranoemu.ui.prefs.LocalPrefsDataStore
import com.akira.tyranoemu.ui.prefs.PrefsListItem
import com.akira.tyranoemu.ui.prefs.ifNotNullThen
import com.gtastart.common.R
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MHelpers
import com.gtastart.common.util.extend.getActivityFromDialog
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
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditTextArgvPref(
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
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true,
    valueAsSummary: Boolean = true,
    singleLine: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
) {

    var showDialog by rememberSaveable { mutableStateOf(false) }

    val datastore = LocalPrefsDataStore.current
    var realStore by remember { datastore.okkv(key, defaultValue) }
    val textVal by rememberUpdatedState(defaultValue)

    PrefsListItem(
        text = { androidx.compose.material3.Text(text = title, maxLines = 1) },
        modifier = if (enabled) modifier.clickable { showDialog = true } else modifier,
        enabled = enabled,
        darkenOnDisable = false,
        textColor = textColor,
        minimalHeight = false,
        icon = null,
        secondaryText = summary.ifNotNullThen { androidx.compose.material3.Text(text = summary!!) },
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
                Column {
                    OutlinedTextField(
                        value = textVal,
                        label = { Text(label) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = singleLine,
                        onValueChange = {
                            // textVal = it
                            onValueChange(it)
                        }
                    )
                    val context = LocalContext.current
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
                    ) {
                        Button(
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                val activity = context.getActivityFromDialog()
                                val screenSizePx = MHelpers.getScreenSizePx(activity?:context as Activity)
                                onValueChange(CSMOSUtils.setResolution(
                                    argv = textVal,
                                    width = maxOf(screenSizePx.widthPx, screenSizePx.heightPx),
                                    height = minOf(screenSizePx.widthPx, screenSizePx.heightPx)
                                ))
                            }
                        ) { Text("原生比例分辨率") }
                        Button(
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                onValueChange(CSMOSUtils.setResolution(
                                    argv = textVal,
                                    width = 1280,
                                    height = 960
                                ))
                            }
                        ) { Text("4:3 960p") }
                        Button(
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                onValueChange(CSMOSUtils.setResolution(
                                    argv = textVal,
                                    width = 1920,
                                    height = 1200
                                ))
                            }
                        ) { Text("16:10 1k") }
                        Button(
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                onValueChange(CSMOSUtils.setResolution(
                                    argv = textVal,
                                    width = 2560,
                                    height = 1600
                                ))
                            }
                        ) { Text("16:10 2k") }
                    }
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
