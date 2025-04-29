package com.billflx.csgo.page.settings.game

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.paging.compose.collectAsLazyPagingItems
import com.akira.tyranoemu.ui.prefs.NoScrollPrefsScreen
import com.akira.tyranoemu.ui.prefs.prefs.EditTextProPref
import com.akira.tyranoemu.ui.prefs.prefs.ListDialogHtmlTextPref
import com.akira.tyranoemu.ui.prefs.prefs.ListDialogTextPref
import com.akira.tyranoemu.ui.prefs.prefs.TextPref
import com.akira.tyranoemu.ui.prefs.prefs.TextPrefDialogConfirm
import com.billflx.csgo.bean.DataType
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.data.db.CSVersionInfo
import com.billflx.csgo.nav.LocalDownloadManagerVM
import com.billflx.csgo.nav.LocalGameSettingViewModel
import com.billflx.csgo.nav.LocalRootNav
import com.billflx.csgo.nav.RootDesc
import com.billflx.csgo.page.server.ServerViewModel
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.MOSDialog
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.common.util.prefs.EditTextArgvPref
import com.gtastart.common.util.prefs.FolderChooserPref
import com.heyanle.okkv2.core.OkkvDefaultProvider
import com.valvesoftware.source.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameSettings(
    modifier: Modifier = Modifier,
    viewModel: GameSettingViewModel = LocalGameSettingViewModel.current,
    focusGameResPathItem: Boolean = false
) {
    val context = LocalContext.current
    val versionList = viewModel.pagingFlow.collectAsLazyPagingItems()
    val currentVersion by viewModel.currentVersion
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentVersion) {
        scope.launch(Dispatchers.Main) {
            versionList.refresh()
            currentVersion?.let {
                it.versionName?.let {
                    viewModel.changeVersion(versionName = it)
                }
            }
        }
    }
    LaunchedEffect(versionList.itemSnapshotList) {
        Log.d("", "GameSettings: versionList")
    }


    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text("游戏设置")
                },
                actions = {
                    if (!focusGameResPathItem) {
                        val rootNav = LocalRootNav.current
                        IconButton(onClick = {
                            rootNav.navigateSingleTopTo(RootDesc.DownloadManager.route)
                        }) {
                            Icon(Icons.Default.FileDownload, contentDescription = stringResource(R.string.download_manager))
                        }
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            NoScrollPrefsScreen (dataStore = OkkvDefaultProvider.def()) {
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
                NoScrollPrefsScreen (dataStore = OkkvDefaultProvider.def()) {

                    prefsGroup(title = "数据包设置") {
                        if (!focusGameResPathItem) {
                            prefsItem {
                                val downloadManagerVM = LocalDownloadManagerVM.current
                                val rootNav = LocalRootNav.current
                                ListDialogHtmlTextPref(
                                    title = "下载游戏数据包",
                                    list = currentVersion?.dataLink?: emptyList(),
                                    itemText = { it.title },
                                    itemLeadingIcon = {
                                        Icon(imageVector = Icons.Default.Download, contentDescription = null)
                                    },
                                    itemTrailingContent = { item, showDialog ->
                                        Button(
                                            contentPadding = PaddingValues(horizontal = GtaStartTheme.spacing.normal),
                                            onClick = {
                                                val url = item.url
                                                val title = item.title
                                                val type = item.type
                                                val parentPath = LauncherActivity.getDefaultDir() + Constants.DOWNLOAD_PATH
                                                scope.launch {
                                                    showDialog.value = false
                                                    val addDownload = downloadManagerVM.addDownload( // 添加下载任务
                                                        url = url,
                                                        parentPath = parentPath,
                                                        dataType = type ?: DataType.GameDataPackage
                                                    )
                                                    rootNav.navigateSingleTopTo(RootDesc.DownloadManager.route)
                                                }
                                            }
                                        ) {
                                            Text("下载")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    prefsGroup(title = "版本设置") {
                        prefsItem {
                            val transition = rememberInfiniteTransition()
                            val hue by transition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f, // 360° 颜色循环
                                animationSpec = infiniteRepeatable(
                                    animation = tween(durationMillis = 5000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                ), label = "hueAnimation"
                            )
                            val bgColor = Color.hsv(hue, 1f, 1f).copy(alpha = 0.3f)
                            FolderChooserPref(
                                modifier = if (focusGameResPathItem) Modifier
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
                                    .background(bgColor) else Modifier,
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
                                onImportClick = {

                                    MOSDialog.show(
                                        context,
                                        title = "选择要使用的版本",
                                        customView = { dialog ->
                                            val vm = hiltViewModel<ServerViewModel>(
                                            viewModelStoreOwner = LocalViewModelStoreOwner.current!!
                                        )
                                            Column {
                                                vm.versionList.forEach {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth().clickable {
                                                            viewModel.changeSettings(
                                                                CSVersionInfo(gamePath = it.gamePath)
                                                            )
                                                            context.MToast("应用完成")
                                                            dialog.dismiss()
                                                        }.padding(horizontal = 16.dp, vertical = 8.dp)
                                                    ) {
                                                        Text(text = it.versionNameForShow
                                                            ?:it.versionName.orEmpty())
                                                    }
                                                }
                                            }
                                        },
                                        positiveButtonText = "取消",
                                        onPositiveButtonClick = {d,_ -> d.dismiss()}
                                    )
                                },
                                trailingContent = {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                        horizontalAlignment = Alignment.End,
                                    ) {
                                        Text(currentVersion?.gamePath.orEmpty(), maxLines = 3, textAlign = TextAlign.End)
                                    }
                                }
                            )

                        }
                        prefsItem {
                            EditTextArgvPref(
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
                            val isVerify by viewModel.isFilesMd5Passed
                            val errorColor = MaterialTheme.colorScheme.error
                            val passColor = MaterialTheme.colorScheme.primary
                            val color = if (isVerify == 0)
                                Color.Unspecified to "校验中"
                            else if (isVerify == 1) {
                                passColor to "通过"
                            } else if (isVerify == -1) {
                                errorColor to "失败，若遇到问题请删除重下"
                            } else if (isVerify == 2) {
                                Color.Unspecified to "内置版本无需检测"
                            } else {
                                Color.Unspecified to "无法加载联网数据"
                            }
                            LaunchedEffect(currentVersion) {
                                viewModel.verifyAllFile()
                            }
                            TextPref(
                                title = "校验核心库完整性",
                                onClick = {
                                    viewModel.verifyAllFile()
                                },
                                trailingContent = {
                                    Text(
                                        text = color.second,
                                        color = color.first
                                    )
                                }
                            )
                        }
                        prefsItem {
                            TextPref(
                                title = "检测游戏资源特征",
                                onClick = {
                                    viewModel.checkSourceDataDialog(context)
                                }
                            )
                        }
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
                DownloadPanel(viewModel = viewModel) // 往里传
            }
        }
    }
}

@Composable
fun DownloadPanel(
    modifier: Modifier = Modifier,
    viewModel: GameSettingViewModel = LocalGameSettingViewModel.current
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val progress by viewModel.downloadProgress
            CircularProgressIndicator(
                progress = {
                    progress.toFloat() / 100f
                }
            )
            Spacer(Modifier.height(8.dp))
            var showButton by viewModel.showDownloadButton
            val context = LocalContext.current
            if (showButton) {
                Button(
                    onClick = {
                        showButton = false
                        viewModel.downloadLibs(context)
                    }
                ) { Text("下载") }
            }
        }
    }
}