package com.billflx.csgo.page

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.billflx.csgo.bean.DownloadStatus
import com.billflx.csgo.bean.MDownloadItemBean
import com.billflx.csgo.nav.LocalDownloadManagerVM
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.widget.MButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadManagerScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("下载管理")
                },

            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            DownloadManagerContent()
        }
    }
}

fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningServices = activityManager.getRunningServices(Int.MAX_VALUE)

    for (service in runningServices) {
        if (service.service.className == serviceClass.name) {
            return true
        }
    }
    return false
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DownloadManagerContent(
    modifier: Modifier = Modifier,
) {
    val viewModel: DownloadManagerViewModel = LocalDownloadManagerVM.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(context) {
        viewModel.startDownloadService(context)
    }

    Column {
        FlowRow {
            var count by remember { mutableStateOf(1) }
            MButton(
                text = "添加测试下载任务",
                onClick = {
                    coroutineScope.launch {
                        val url = "http://106.52.5.176:5244/d/GTADE2024/1.72/apk/GTA_III_1.72_Netflix%E7%BD%91%E9%A3%9E%E7%89%88(AML%2BXLog%2BMT%E6%9C%AC%E5%9C%B0%E5%AD%98%E5%82%A8).apk"
                        val parentPath = "/sdcard/Download/CSMOSDownload"
                        val fileName: String? = null //"test$count.zip"
                        viewModel.addDownload(url, parentPath, fileName)
                    }
                    coroutineScope.launch {
                        val url = "http://106.52.5.176:5244/d/GTADE2024/1.72/apk/GTA_III_1.72.42919648_R%E6%98%9F%E7%89%88(AML%2BXLog%2BMT%E6%9C%AC%E5%9C%B0%E5%AD%98%E5%82%A8).apk"
                        val parentPath = "/sdcard/Download/CSMOSDownload"
                        val fileName: String? = null //"test$count.zip"
                        viewModel.addDownload(url, parentPath, fileName)
                    }
                    coroutineScope.launch {
                        val url = "http://106.52.5.176:5244/d/GTADE2024/1.72/apk/GTA_VC_1.72_Netflix%E7%BD%91%E9%A3%9E%E7%89%88(AML%2BXLog%2BMT%E6%9C%AC%E5%9C%B0%E5%AD%98%E5%82%A8).apk"
                        val parentPath = "/sdcard/Download/CSMOSDownload"
                        val fileName: String? = null //"test$count.zip"
                        viewModel.addDownload(url, parentPath, fileName)
                    }
                    coroutineScope.launch {
                        val url = "http://106.52.5.176:5244/d/189/data/samp2.00_v7_data.zip"
                        val parentPath = "/sdcard/Download/CSMOSDownload"
                        val fileName: String? = null //"test$count.zip"
                        viewModel.addDownload(url, parentPath, fileName)
                    }

                }
            )
        }

        var selectedPageIndex by remember { mutableStateOf(0) }
        TabRow(
            modifier = modifier.fillMaxWidth(),
            selectedTabIndex = selectedPageIndex,
        ) {
            Tab(
                selected = selectedPageIndex == 0,
                onClick = {
                    selectedPageIndex = 0
                },
                text = {
                    Text("下载中")
                }
            )
            Tab(
                selected = selectedPageIndex == 1,
                onClick = {
                    selectedPageIndex = 1
                    coroutineScope.launch {
                        viewModel.loadDownloadedListDB()
                    }
                },
                text = {
                    Text("已完成")
                }
            )
        }

        if (selectedPageIndex == 0) {
            LazyColumn(
                modifier = modifier.fillMaxWidth()
            ) {
                items(viewModel.downloadList) { item ->
                    val downloadStatus = item.downloadStatusData?.downloadStatus
                    DownloadingItem(
                        item = item
                    )
                }
            }
        } else if (selectedPageIndex == 1) {
            LazyColumn(
                modifier = modifier.fillMaxWidth()
            ) {
                items(viewModel.downloadedList) { item ->
                    val downloadStatus = item.downloadStatusData?.downloadStatus
                    DownloadedItem(
                        item = item
                    )
                }
            }
        }

    }
}

@Composable
private fun DownloadedItem(
    modifier: Modifier = Modifier,
    item: MDownloadItemBean
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GtaStartTheme.spacing.normal),
        horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
        ) {
            Text(item.mDownload?.fileName.orEmpty())
            Text(item.mDownload?.parentPath.orEmpty(),
                style = MaterialTheme.typography.bodySmall)
        }
        MButton(
            text = "操作",
            onClick = {
                context.MToast("操作")
            }
        )

    }
}

@Composable
private fun DownloadingItem(
    modifier: Modifier = Modifier,
    item: MDownloadItemBean,
) {
    val downloadStatus = item.downloadStatusData?.downloadStatus
    val mDownload = item.mDownload
    var buttonDownloadText by rememberSaveable { mutableStateOf("暂停") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GtaStartTheme.spacing.normal),
        horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small)
        ) {
            Text(item.mDownload?.getDownloadTask()?.filename.orEmpty())
            Text(
                if (downloadStatus?.value == DownloadStatus.Started) {
                    buttonDownloadText = "暂停"
                    "连接中"
                } else if (downloadStatus?.value == DownloadStatus.Downloading) {
                    buttonDownloadText = "暂停"
                    item.downloadStatusData?.downloadProgressStr?.value?:"等待中"
                } else if (downloadStatus?.value == DownloadStatus.IDLE) {
                    buttonDownloadText = "继续"
                    "等待中"
                } else if (downloadStatus?.value == DownloadStatus.PAUSE) {
                    buttonDownloadText = "继续"
                    "暂停"
                } else if (downloadStatus?.value == DownloadStatus.ERROR) {
                    buttonDownloadText = "重试"
                    "错误"
                } else if (downloadStatus?.value == DownloadStatus.Finished) {
                    buttonDownloadText = "完成"
                    "完成"
                } else {
                    "QAQ"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
        MButton(
            text = buttonDownloadText,
            onClick = {
                if (downloadStatus?.value == DownloadStatus.Started) {
                    mDownload?.stop()
                    buttonDownloadText = "继续"
                } else if (downloadStatus?.value == DownloadStatus.Downloading) {
                    item.downloadStatusData?.downloadProgressStr?.value?:"等待中"
                    mDownload?.stop()
                    buttonDownloadText = "继续"
                } else if (downloadStatus?.value == DownloadStatus.IDLE) {
                    mDownload?.start()
                    buttonDownloadText = "暂停"
                } else if (downloadStatus?.value == DownloadStatus.PAUSE) {
                    mDownload?.start()
                    buttonDownloadText = "暂停"
                } else if (downloadStatus?.value == DownloadStatus.ERROR) {
                    Log.d("", "DownloadItem: 重试")
                    mDownload?.start()
                    buttonDownloadText = "暂停"
                } else if (downloadStatus?.value == DownloadStatus.Finished) {
                    buttonDownloadText = "完成"
                }
            }
        )

        IconButton(
            onClick = {

            }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null
            )
        }
    }
}