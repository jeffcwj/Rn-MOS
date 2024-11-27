package com.billflx.csgo.page

import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.billflx.csgo.bean.DownloadStatus
import com.billflx.csgo.nav.LocalRootNav
import com.billflx.csgo.nav.LocalSettingViewModel
import com.billflx.csgo.nav.RootDesc
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.MDownload
import com.gtastart.common.util.MToast
import com.gtastart.common.util.ZipUtils
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.common.util.compose.widget.MAlertDialog
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.valvesoftware.source.R
import me.nillerusr.DirchActivity
import net.lingala.zip4j.util.FileUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
    onStartInstallGuide: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("CS:MOS", modifier = modifier.padding(start = GtaStartTheme.spacing.small))
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher),
                        contentDescription = null,
                        modifier = modifier
                            .size(36.dp)
                            .clip(MaterialTheme.shapes.medium),
                    )
                }
            )
        },
    ) { innerPadding ->
        MainContent(
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(GtaStartTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
    ) {
        NoticeCard()
        StatusCard()
    }
}

@Composable
private fun NoticeCard(
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf("公告") }
    var content by rememberSaveable { mutableStateOf("加载中...") }
    val openDialog = rememberSaveable { mutableStateOf(false) }

    when {
        openDialog.value -> {
            MAlertDialog(
                title = title,
                content = content,
                positiveButtonText = "确定",
                onDismissRequest = {openDialog.value = false}
            )
        }
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                openDialog.value = true
            }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
            modifier = modifier.padding(GtaStartTheme.spacing.medium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
private fun StatusCard(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
) {
    val openDownloadDialog = viewModel.openDownloadDialog
    val context = LocalContext.current

    when {
        openDownloadDialog.value -> {
            viewModel.loadFinishList()
            var selectedPageIndex by remember { mutableStateOf(0) }
            MCustomAlertDialog(
                title = "下载资源",
                content = {
                    Column {
                        Box { // Tab标签区域
                            TabRow(
                                selectedTabIndex = selectedPageIndex,
                            ) {
                                Tab(
                                    selected = selectedPageIndex == 0,
                                    onClick = {
                                        selectedPageIndex = 0
                                    }
                                ) {
                                    Text("游戏数据")
                                }
                                Tab(
                                    selected = selectedPageIndex == 1,
                                    onClick = {
                                        selectedPageIndex = 1
                                    }
                                ) {
                                    Text("下载列表")
                                }
                                Tab(
                                    selected = selectedPageIndex == 2,
                                    onClick = {
                                        selectedPageIndex = 2
                                    }
                                ) {
                                    Text("已下载")
                                }
                            }
                        }

                        if (selectedPageIndex == 0) { // 游戏数据
                            LazyColumn {
                                items(viewModel.gameResList) { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = item.title.orEmpty(),
                                            maxLines = 1
                                        )
                                        MButton(
                                            text = "下载",
                                            onClick = {
                                                viewModel.addToDownloadList(item)
                                                selectedPageIndex = 1
                                            }
                                        )
                                    }
                                }
                            }
                        } else if (selectedPageIndex == 1) { // 下载列表
                            LazyColumn {
                                items(viewModel.mDownloadList) { item ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
                                        ) {
                                            Text(item.gameResData?.title.orEmpty())
                                            if (item.downloadStatusData?.downloadStatus?.value == DownloadStatus.Started) {
                                                Text("加载中")
                                            } else {
                                                Text(item.downloadStatusData?.downloadProgressStr?.value.orEmpty())
                                            }
                                        }
                                        var btnEnabled = remember { mutableStateOf(true) }
                                        if (item.downloadStatusData?.downloadStatus?.value == DownloadStatus.Finished) {
                                            viewModel.addToDownloadFinishList(
                                                item.mDownload?.getDownloadTask()?.filename.orEmpty(),
                                                item.mDownload?.getDownloadTask()?.file?.length().toString(),
                                                item.mDownload?.getDownloadTask()?.file?.absolutePath.orEmpty())
                                            viewModel.mDownloadList.remove(item)
                                            selectedPageIndex = 2
                                        }
                                        MButton(
                                            enabled = btnEnabled.value,
                                            text = if (item.downloadStatusData?.downloadStatus?.value == DownloadStatus.PAUSE)
                                                        "继续"
                                                    else if (item.downloadStatusData?.downloadStatus?.value == DownloadStatus.Finished)
                                                        "完成"
                                            else
                                                "暂停",
                                            onClick = {
                                                if (item.downloadStatusData?.downloadStatus?.value == DownloadStatus.PAUSE) {
                                                    item.mDownload?.start()
                                                } else {
                                                    item.mDownload?.stop()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        } else if (selectedPageIndex == 2) { // 已下载

                            LazyColumn {
                                items(viewModel.gameDownloadFinishList) { item ->
                                    var buttonEnabled = remember { mutableStateOf(true) }
                                    var btnText = remember { mutableStateOf("解压") }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
                                    ) {
                                        Column(
                                            modifier = modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
                                        ) {
                                            Text(item.name.orEmpty())
                                            Text(item.path.orEmpty(),
                                                maxLines = 1,
                                                style = MaterialTheme.typography.bodySmall)
                                        }
                                        MButton(
                                            enabled = buttonEnabled.value,
                                            text = btnText.value,
                                            onClick = {
                                                if (item.path?.endsWith(".7z") == true) {
                                                    buttonEnabled.value = false
                                                    Coroutines.ioThenMain(
                                                        work = {
                                                            ZipUtils.sevenUnZip(
                                                                pathFrom = File(item.path.orEmpty()).absolutePath,
                                                                pathTo = File(item.path.orEmpty()).parentFile.absolutePath,
                                                                listener = object : ZipUtils.Companion.ProgressListener {
                                                                    override fun onProgressUpdate(
                                                                        percent: Int
                                                                    ) {
                                                                        btnText.value = "$percent%"
                                                                    }

                                                                    override fun onCompleted() {
                                                                        btnText.value = "已完成"
                                                                        buttonEnabled.value = false
                                                                    }

                                                                    override fun onError(error: String) {
                                                                        btnText.value = "重试"
                                                                        buttonEnabled.value = true
                                                                    }

                                                                }
                                                            )
                                                        },
                                                        callback = {

                                                        }
                                                    )
                                                } else if (item.path?.endsWith(".zip") == true) {
                                                    buttonEnabled.value = false
                                                    Coroutines.ioThenMain(
                                                        work = {
                                                            ZipUtils.unZip(
                                                                pathFrom = File(item.path.orEmpty()).absolutePath,
                                                                pathTo = File(item.path.orEmpty()).parentFile.absolutePath,
                                                                listener = object : ZipUtils.Companion.ProgressListener {
                                                                    override fun onProgressUpdate(
                                                                        percent: Int
                                                                    ) {
                                                                        Log.d(
                                                                            "",
                                                                            "onProgressUpdate: $percent"
                                                                        )
                                                                        btnText.value = "$percent%"
                                                                    }

                                                                    override fun onCompleted() {
                                                                        btnText.value = "已完成"
                                                                        buttonEnabled.value = false
                                                                    }

                                                                    override fun onError(error: String) {
                                                                        btnText.value = "重试"
                                                                        buttonEnabled.value = true
                                                                    }

                                                                }
                                                            )
                                                        },
                                                        callback = {

                                                        }
                                                    )
                                                } else {
                                                    context.MToast("格式不支持，暂时无法解压")
                                                }
                                            }
                                        )

                                        MButton(
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = colorResource(R.color.md_theme_dark_errorContainer),
                                                contentColor = colorResource(R.color.md_theme_dark_onErrorContainer)
                                            ),
                                            text = "删除",
                                            onClick = {
                                                MaterialAlertDialogBuilder(context)
                                                    .setTitle("提示")
                                                    .setMessage("是否删除 ${item.name} ？")
                                                    .setPositiveButton("取消", null)
                                                    .setNegativeButton("删除") { _, _ ->
                                                        val isDone = File(item.path).delete()
                                                        if (isDone) {
                                                            viewModel.loadFinishList()
                                                            context.MToast("已删除")
                                                        } else {
                                                            context.MToast("删除失败")
                                                        }
                                                    }
                                                    .show()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                positiveButtonText = "关闭",
                onPositiveButtonClick = { openDownloadDialog.value = false },
                onDismissRequest = {}
            )
        }
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        val settingViewModel = LocalSettingViewModel.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            settingViewModel.refreshGamePath()
            viewModel.checkGameStatus()
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
            modifier = modifier.padding(GtaStartTheme.spacing.medium)
        ) {
            Text(
                text = "功能",
                style = MaterialTheme.typography.titleMedium
            )

            MButton(
                text = "选择游戏路径",
                onClick = {
                    val intent = Intent(context, DirchActivity::class.java)
                    launcher.launch(intent)
                }
            )
            MButton(
                text = "游戏数据管理",
                onClick = {
                    openDownloadDialog.value = true
                }
            )

            val navController = LocalRootNav.current
            MButton(
                text = "下载管理",
                onClick = {
                    navController.navigateSingleTopTo(RootDesc.DownloadManager.route)
                }
            )
        }
    }
}