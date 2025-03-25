package com.billflx.csgo.page.settings.general

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.akira.tyranoemu.ui.prefs.PrefsScreen
import com.akira.tyranoemu.ui.prefs.prefs.SwitchPref
import com.akira.tyranoemu.ui.prefs.prefs.TextPrefDialogConfirm
import com.billflx.csgo.data.AppLocalDataSource
import com.gtastart.common.util.MToast
import com.heyanle.okkv2.core.OkkvDefaultProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettings() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("全局设置")
                }
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            PrefsScreen(dataStore = OkkvDefaultProvider.def()) {
                prefsGroup("常规") {
                    prefsItem {
                        var checked by rememberSaveable { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            checked = AppLocalDataSource.isExperimental()
                        }
                        TextPrefDialogConfirm(
                            title = "实验性内容",
                            summary = "是否显示开发中的内容",
                            dialogMessage = "启用后，可查看正在开发和测试中的实验性功能。这些功能可能尚不稳定，存在未完善之处，甚至可能引发未知错误，仅供测试使用",
                            onClick = {
                                AppLocalDataSource.setExperimental(!checked)
                                checked = !checked
                                context.MToast("已应用，请重启软件")
                            },
                            trailingContentWithDialog = { showDialog ->
                                Switch(
                                    checked = checked,
                                    onCheckedChange = {
                                        showDialog.value = true
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}