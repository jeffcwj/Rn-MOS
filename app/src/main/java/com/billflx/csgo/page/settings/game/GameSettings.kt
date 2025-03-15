package com.billflx.csgo.page.settings.game

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.akira.tyranoemu.ui.prefs.PrefsScreen
import com.akira.tyranoemu.ui.prefs.prefs.EditTextProPref
import com.akira.tyranoemu.ui.prefs.prefs.ListDialogTextPref
import com.akira.tyranoemu.ui.prefs.prefs.TextPref
import com.akira.tyranoemu.ui.prefs.prefs.TextPrefDialogConfirm
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.gtastart.common.util.MOSDialog
import com.gtastart.common.util.prefs.FolderChooserPref
import com.heyanle.okkv2.core.OkkvDefaultProvider
import me.nillerusr.DirchActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSettings(
    modifier: Modifier = Modifier,
    viewModel: GameSettingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val versionList = viewModel.pagingFlow.collectAsLazyPagingItems()
    val currentVersion by viewModel.currentVersion

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text("游戏设置")
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier.padding(innerPadding)
        ) {
            PrefsScreen(dataStore = OkkvDefaultProvider.def()) {
                prefsGroup(title = "版本") {
                    prefsItem {
                        Spacer(Modifier.height(8.dp))
                        ListDialogTextPref(
                            title = "选择版本",
                            list = versionList,
                            onItemClick = {
                                viewModel.changeVersion(it.versionName.orEmpty())
                            },
                            itemText = { it.versionNameForShow?:it.versionName.orEmpty() },
                            trailingContent = {
                                Text(
                                    text = currentVersion?.versionNameForShow?:currentVersion?.versionName.orEmpty()
                                )
                            },
                        )
                    }
                }
            }

            val isInstalled by viewModel.isInstalled
            if (isInstalled) { // 已安装，显示设置项
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    val data = result.data?.getStringExtra("result")
                    if (data?.equals("OK") == true) { // 成功设置
                        viewModel.changeSettings(CSVersionInfo(gamePath = ModLocalDataSource.getGamePath()))
                    }
                }
                PrefsScreen(dataStore = OkkvDefaultProvider.def()) {
                    prefsGroup(title = "版本设置") {
                        prefsItem {
                            FolderChooserPref(
                                key = "choose_cs_folder",
                                title = "游戏资源路径",
                                dialogTitle = "选择游戏资源路径",
                                defaultValue = currentVersion?.gamePath.orEmpty(),
                                onValueChange = {
                                    viewModel.changeSettings(CSVersionInfo(gamePath = it))
                                },
                                onSelectButtonClick = {
                                    val intent = Intent(context, DirchActivity::class.java)
                                    launcher.launch(intent)
                                },
                                trailingContent = {
                                    Column(
                                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                                        horizontalAlignment = Alignment.End,
                                    ) {
                                        Text(currentVersion?.gamePath.orEmpty(), maxLines = 3, textAlign = TextAlign.End)
                                    }
                                }
                            )
                        }
                        prefsItem {
                            EditTextProPref(
                                key = "cmdline_params",
                                title = "命令行参数",
                                dialogTitle = "命令行参数",
                                defaultValue = currentVersion?.argv.orEmpty(),
                                onValueChange = {
                                    viewModel.changeSettings(CSVersionInfo(argv = it))
                                },
                                trailingContent = {
                                    Text(text = currentVersion?.argv.orEmpty(), textAlign = TextAlign.End)
                                }
                            )
                        }
                        prefsItem {
                            EditTextProPref(
                                key = "env",
                                title = "环境变量",
                                dialogTitle = "环境变量",
                                defaultValue = currentVersion?.env.orEmpty(),
                                onValueChange = {
                                    viewModel.changeSettings(CSVersionInfo(env = it))
                                },
                                trailingContent = {
                                    Text(text = currentVersion?.env.orEmpty(), textAlign = TextAlign.End)
                                }
                            )
                        }
                    }
                    prefsGroup("更多") {
                        prefsItem {
                            TextPrefDialogConfirm(
                                title = "删除",
                                textColor = MaterialTheme.colorScheme.error,
                                dialogMessage = "确认删除此版本？",
                                onClick = {
                                    viewModel.deleteLibs()
                                }
                            )
                        }
                    }
                }

            } else { // 未安装，显示下载页面
                DownloadPanel()
            }
        }
    }
}

@Composable
fun DownloadPanel(
    modifier: Modifier = Modifier,
    viewModel: GameSettingViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ""
            )
            val progress by viewModel.downloadProgress
            CircularProgressIndicator(
                progress = progress.toFloat() / 100f
            )
            Spacer(Modifier.height(8.dp))
            var showButton by viewModel.showDownloadButton
            if (showButton) {
                Button(
                    onClick = {
                        showButton = false
                        viewModel.downloadLibs()
                    }
                ) { Text("下载") }
            }
        }
    }
}