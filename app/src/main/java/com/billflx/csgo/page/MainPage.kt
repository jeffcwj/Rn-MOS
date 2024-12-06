package com.billflx.csgo.page

import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.billflx.csgo.LocalMainViewModel
import com.billflx.csgo.bean.DataType
import com.billflx.csgo.bean.DownloadStatus
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.nav.LocalDownloadManagerVM
import com.billflx.csgo.nav.LocalRootNav
import com.billflx.csgo.nav.LocalSettingViewModel
import com.billflx.csgo.nav.RootDesc
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.Coroutines
import com.gtastart.common.util.MDownload
import com.gtastart.common.util.MHelpers
import com.gtastart.common.util.MToast
import com.gtastart.common.util.ZipUtils
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.common.util.compose.widget.MAlertDialog
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.umeng.commonsdk.statistics.common.HelperUtils
import com.valvesoftware.source.BuildConfig
import com.valvesoftware.source.R
import kotlinx.coroutines.launch
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity
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
            val navController = LocalRootNav.current
            TopAppBar(
                title = {
                    Text("CS:MOS ${BuildConfig.FLAVOR.replace("-", ".")} ${BuildConfig.VERSION_NAME}", modifier = modifier.padding(start = GtaStartTheme.spacing.small))
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher),
                        contentDescription = null,
                        modifier = modifier
                            .padding(start = GtaStartTheme.spacing.normal)
                            .size(36.dp)
                            .clip(MaterialTheme.shapes.medium),
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigateSingleTopTo(RootDesc.DownloadManager.route)
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = stringResource(R.string.download_manager))
                    }
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
        HasUpdateCard()
        NoticeCard()
        StatusCard()
    }
}

@Composable
private fun HasUpdateCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val link = Constants.appUpdateInfo.value?.app?.link
    val version = Constants.appUpdateInfo.value?.app?.version
    Log.d("", "HasUpdateCard: 有更新吗")
    if (Constants.appUpdateInfo.value?.app?.hasUpdate?.value == true) {
        Log.d("", "HasUpdateCard: 有的")

        ElevatedCard(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = GtaStartTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = modifier.weight(1f),
                    text = "${stringResource(R.string.has_new_version)} $version" // 有新版本
                )
                TextButton(
                    onClick = {
                        MHelpers.openBrowser(context, link?:"")
                    }
                ) {
                    Text(stringResource(R.string.update))
                }
            }
        }
    }
    AnimatedVisibility (Constants.appUpdateInfo.value?.app?.hasUpdate?.value == true) {

    }
}

/**
 * 公告卡片
 */
@Composable
private fun NoticeCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var title by rememberSaveable { mutableStateOf(context.getString(R.string.notice)) }
    var content = Constants.appUpdateInfo
    val openDialog = rememberSaveable { mutableStateOf(false) }

    when {
        openDialog.value -> {
            MAlertDialog(
                title = title,
                content = content.value?.app?.notice?: stringResource(R.string.getting),
                positiveButtonText = stringResource(R.string.ok),
                onPositiveButtonClick = { openDialog.value = false },
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
                text = content.value?.app?.notice?: stringResource(R.string.getting),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusCard(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = LocalMainViewModel.current
) {
    val openDownloadDialog = viewModel.openDownloadDialog
    val context = LocalContext.current

    when {
        openDownloadDialog.value -> {

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
                text = stringResource(R.string.usage), // 功能
                style = MaterialTheme.typography.titleMedium
            )

            FlowRow {
               val navController = LocalRootNav.current
                val downloadManagerVM = LocalDownloadManagerVM.current
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(context) {
                    downloadManagerVM.startDownloadService(context) // 需要添加下载任务之前启动下载服务，否则闪退
                }

                //下载数据包按钮
                MButton(
                    text = stringResource(viewModel.addDownloadText.value),
                    onClick = {
                        val linkList = Constants.appUpdateInfo.value?.link?.dataLink
                        lateinit var builder: AlertDialog

                        val inflateView = LayoutInflater.from(context).inflate(R.layout.layout_compose, null)
                        inflateView.layoutParams = ViewGroup.LayoutParams(-1,-1)
                        val composeView = inflateView.findViewById<ComposeView>(R.id.composeView)
                        composeView.setContent {
                            GtaStartTheme(darkTheme = true) {
                                Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh) {
                                    Column {
                                        linkList?.forEach { item ->
                                            val url = item.url
                                            val title = item.title
                                            val parentPath = LauncherActivity.getDefaultDir() + Constants.DOWNLOAD_PATH
                                            Row(modifier = Modifier.padding(GtaStartTheme.spacing.medium), verticalAlignment = Alignment.CenterVertically) {
                                                Text(title, modifier = Modifier.weight(1f))
                                                MButton(text = stringResource(R.string.download), onClick = {
                                                    coroutineScope.launch {
                                                        val addDownload = downloadManagerVM.addDownload( // 添加下载任务
                                                            url = url,
                                                            parentPath = parentPath,
                                                            dataType = DataType.GameDataPackage
                                                        )
                                                        builder.dismiss() // 关闭弹窗
                                                        navController.navigateSingleTopTo(RootDesc.DownloadManager.route)
                                                    }
                                                })
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        builder = MaterialAlertDialogBuilder(context)
                            .setTitle(context.getString(R.string.select_game_data_version))
                            .setView(inflateView)
                            .show()
//                        addDownload?.let { viewModel.addDownloadText = it.downloadStatusData?.downloadProgressStr?:viewModel.addDownloadText }
                    }
                )
            }

        }
    }
}