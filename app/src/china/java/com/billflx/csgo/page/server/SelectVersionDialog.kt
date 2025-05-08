package com.billflx.csgo.page.server

import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.nav.LocalMainPageNav
import com.billflx.csgo.nav.LocalServerViewModel
import com.billflx.csgo.nav.MainPageDestination
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MOSDialog
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.valvesoftware.ValveActivity2
import com.valvesoftware.source.R
import kotlinx.coroutines.launch
import org.libsdl.app.SDLActivity

@Composable
fun SelectVersionDialog(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel = LocalServerViewModel.current,
    currentServerIP: MutableState<String>,
    hasPassword: MutableState<Boolean>,
    pagerState: PagerState,
    openDialog: MutableState<Boolean>,
    openSelectVersionDialog: MutableState<Boolean>
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 游戏结束以后刷新列表数据
        viewModel.refreshServerList(pagerState.settledPage)
    }

    var currentVersion by viewModel.currentVersion
    var serverCsType by viewModel.serverCsType
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val mainPageNav = LocalMainPageNav.current
    val intent = Intent(context, SDLActivity::class.java)

    MCustomAlertDialog(
        modifier = modifier,
        title = "选择版本",
        onDismissRequest = { openSelectVersionDialog.value = false },
        content = {
            LaunchedEffect(Unit) {
                viewModel.getExistVersion()
            }
            LazyColumn {
                items(viewModel.versionList.filter { it.csType == serverCsType }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    if (!viewModel.saveNickName()) { // 保存昵称
                                        context.MToast(context.getString(R.string.nickname_cannot_empty))
                                        return@launch
                                    }
                                    ModLocalDataSource.setCurrentCSVersion(
                                        it.versionName.orEmpty()
                                    ) // 先设置版本
                                    viewModel.applySettingsToModSpNew()
                                    currentVersion =
                                        viewModel.getVersionForShow(
                                            it.versionName.orEmpty()
                                        )
                                    val version = viewModel.getExistVersion()
                                        .firstOrNull { it.versionName == ModLocalDataSource.getCurrentCSVersion() }
                                    val argvMap =
                                        CSMOSUtils.stringToArgsMap(version?.argv.orEmpty())
                                    var csType = ""
                                    val gameArg = argvMap.get("-game")
                                    if (argvMap.isNotEmpty() && gameArg != null) {
                                        csType = gameArg
                                    } else {
                                        csType = ModLocalDataSource.getCsType()
                                            .lowercase()
                                    }
                                    val result = ValveActivity2.preInit(
                                        version?.gamePath.orEmpty(),
                                        csType,
                                    )
                                    if (result != 1) {
                                        if (result == 0) {
                                            // 没找到数据包
                                            MOSDialog.show(
                                                context,
                                                cancelable = false,
                                                title = "提示",
                                                message = "未检测到数据包，你可以：\n1. 前往游戏设置板块，点击「下载游戏数据包」选项进行下载\n2. 如果你已下载了数据包，请到下载管理解压安装\n3.如果本地存在数据包，请到游戏设置板块，并点击「游戏资源路径」进行选择",
                                                positiveButtonText = "前往设置",
                                                onPositiveButtonClick = { d, _ ->
                                                    mainPageNav.navigateSingleTopTo(
                                                        MainPageDestination.AGameSetting.route
                                                    )
                                                    d.dismiss()
                                                }
                                            )
                                        } else {
                                            // 没找到platform
                                            MOSDialog.show(
                                                context,
                                                cancelable = false,
                                                title = "提示",
                                                message = "检测到数据包存在问题，未找到platform文件夹。请前往游戏设置板块，重新下载数据包",
                                                positiveButtonText = "前往设置",
                                                onPositiveButtonClick = { d, _ ->
                                                    mainPageNav.navigateSingleTopTo(
                                                        MainPageDestination.AGameSetting.route
                                                    )
                                                    d.dismiss()
                                                }
                                            )
                                        }
                                    } else {
                                        ModLocalDataSource.setCurrentCSVersion(it.versionName.orEmpty())
                                        viewModel.applySettingsToModSpNew()
                                        currentVersion =
                                            viewModel.getVersionForShow(it.versionName.orEmpty())

                                        CSMOSUtils.saveNickName(viewModel.nickName.value)
                                        CSMOSUtils.saveAutoConnectInfo(
                                            currentServerIP.value
                                        )
                                        val password = viewModel.password.value
                                        if (hasPassword.value) {
                                            CSMOSUtils.addPassword(
                                                password = password
                                            )
                                        } else {
                                            // CSMOSUtils.removePassword() // 不移除也行
                                        }
                                        CSMOSUtils.addCustomMainServers()
                                        launcher.launch(intent) // 回调要刷新列表数据
                                    }
                                    openDialog.value = false
                                    openSelectVersionDialog.value = false
                                }
                            }
                            .padding(GtaStartTheme.spacing.medium)
                    ) {
                        Text(
                            text = it.versionNameForShow
                                ?: it.versionName.orEmpty()
                        )
                    }
                }
                item {
                    if (viewModel.versionList.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("未找到游戏版本，请前往游戏页下载")
                        }
                    }
                }
            }
        }
    )
}