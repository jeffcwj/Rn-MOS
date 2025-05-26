package com.billflx.csgo.page.server

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.billflx.csgo.bean.AutoExecCmdBean
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.nav.LocalMainPageNav
import com.billflx.csgo.nav.LocalServerViewModel
import com.billflx.csgo.nav.LocalSettingViewModel
import com.billflx.csgo.nav.MainPageDestination
import com.billflx.csgo.page.SettingViewModel
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.CsPayload
import com.gtastart.common.util.MOSDialog
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.isCompatHorizontal
import com.gtastart.common.util.compose.matchContentHeight
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.gtastart.common.util.compose.widget.ViewLikeIndicator
import com.gtastart.common.util.isBlank
import com.valvesoftware.ValveActivity2
import com.valvesoftware.source.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.libsdl.app.SDLActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerPage(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel = LocalServerViewModel.current
) {
    val context = LocalContext.current
    val mainPageNav = LocalMainPageNav.current

    var showDialog = rememberSaveable { mutableStateOf(false) }
    when {
        showDialog.value -> {
            EditAutoExecDialog( // 编辑自定义参数弹窗
                showDialog = showDialog
            )
        }
    }

    Scaffold(
        topBar = {
            TopBar(showDialog = showDialog)
        },
        floatingActionButton = { // FAB
            val scope = rememberCoroutineScope()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                // 游戏结束以后刷新列表数据
                Log.d("", "ServerPage: 游戏结束")
                // viewModel.refreshServerList()
            }
            val fabContainerColor = FloatingActionButtonDefaults.containerColor
            var showNickNameDialog by rememberSaveable { mutableStateOf(false) }
            when  {
                showNickNameDialog -> {
                    viewModel.loadNickName()
                    InputNickNameDialog(
                        text = viewModel.nickName,
                        onStartGameClick = { // 启动游戏
                            scope.launch {
                                viewModel.applySettingsToModSpNew() // 从数据库应用设置
                                CSMOSUtils.removeAutoConnectInfo() // 在设置应用之后执行文件操作
                                CSMOSUtils.addCustomMainServers() // 添加主服
                                val intent = Intent(context, SDLActivity::class.java)
                                launcher.launch(intent) // 启动游戏
                                showNickNameDialog = false
                            }
                        },
                        onDismiss = { showNickNameDialog = false }
                    )
                }
            }
            ElevatedCard(
                modifier = Modifier,
                colors = CardDefaults.elevatedCardColors().copy(
                    containerColor = fabContainerColor,
                    contentColor = contentColorFor(fabContainerColor)
                ),
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    val version = viewModel.getExistVersion()
                                        .firstOrNull { it.versionName == ModLocalDataSource.getCurrentCSVersion() }
                                    val argvMap =
                                        CSMOSUtils.stringToArgsMap(version?.argv.orEmpty())
                                    var csType = ""
                                    val gameArg = argvMap.get("-game")
                                    if (argvMap.isNotEmpty() && gameArg != null) {
                                        csType = gameArg
                                    } else {
                                        csType = ModLocalDataSource.getCsType().lowercase()
                                    }
                                    val result = ValveActivity2.preInit(
                                        version?.gamePath.orEmpty(),
                                        csType,
                                    )
                                    if (!viewModel.isCurrentVersionExist()) {
                                        // 选择版本
                                        selectVersionDialog(context, viewModel, scope) {
                                            mainPageNav.navigateSingleTopTo(MainPageDestination.AGameSetting.route)
                                        }
                                        /*MOSDialog.show(
                                        context,
                                        title = "提示",
                                        message = "请先前往游戏版本管理页面，下载版本基础数据和游戏数据",
                                        positiveButtonText = "确定",
                                        onPositiveButtonClick = { d, _ ->
                                            mainPageNav.navigateSingleTopTo(MainPageDestination.AGameSetting.route)
                                            d.dismiss()
                                        }
                                    )*/
                                        return@launch
                                    } else if (result != 1) {
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
                                        showNickNameDialog = true
                                    }
                                }
                            }
                            .padding(
                                end = if (isCompatHorizontal()) 0.dp else GtaStartTheme.spacing.large,
                                start = GtaStartTheme.spacing.large,
                                top = GtaStartTheme.spacing.medium,
                                bottom = GtaStartTheme.spacing.medium
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                        ) {
                            Text(
                                text = stringResource(R.string.launch_game_to_main_interface),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = viewModel.currentVersion.value,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        if (isCompatHorizontal()) {
                            IconButton(
                                onClick = {
                                    showDialog.value = true // 打开编辑autoexec.cfg弹窗
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    VerticalDivider()
                    var currentVersion by viewModel.currentVersion
                    LaunchedEffect(Unit) {
                        currentVersion = viewModel.getVersionForShow(
                            ModLocalDataSource.getCurrentCSVersion()
                        )
                    }
                    LaunchedEffect(currentVersion) {
                        if (currentVersion.isBlank()) {
                            currentVersion = "请选择游戏"
                        }
                    }
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable {
                                scope.launch {
                                    viewModel.getExistVersion()
                                    selectVersionDialog(context, viewModel, scope) {
                                        mainPageNav.navigateSingleTopTo(MainPageDestination.AGameSetting.route)
                                    }
                                }
                            }
                            .padding(horizontal = GtaStartTheme.spacing.small)) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropUp,
                            contentDescription = null,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    ) { innerPadding -> // 包含状态栏高度？
        ServerContent(
            modifier = modifier.padding(innerPadding),
            showDialog = showDialog,
            innerPadding = PaddingValues(0.dp)
        )
    }
}

private fun selectVersionDialog(
    context: Context,
    viewModel: ServerViewModel,
    scope: CoroutineScope,
    onGotoGameSettingClick: () -> Unit
) {
    scope.launch {
        viewModel.getExistVersion()
        var currentVersion by viewModel.currentVersion
        MOSDialog.show(
            context,
            title = "选择版本",
            customView = { dialog ->
                LazyColumn(
                    Modifier//.padding(top = GtaStartTheme.spacing.normal)
                ) {
                    items(viewModel.versionList) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        ModLocalDataSource.setCurrentCSVersion(
                                            it.versionName.orEmpty()
                                        ) // 先设置版本
                                        viewModel.applySettingsToModSpNew()
                                        currentVersion =
                                            viewModel.getVersionForShow(
                                                it.versionName.orEmpty()
                                            )
                                        dialog.dismiss()
                                    }
                                }
                                .padding(vertical = GtaStartTheme.spacing.medium, horizontal = GtaStartTheme.spacing.large)
                        ) {
                            Text(
                                text = it.versionNameForShow
                                    ?: it.versionName.orEmpty()
                            )
                        }
                    }
                    item {
                        if (viewModel.versionList.isEmpty()) {
                            Column (
                                modifier = Modifier.padding(horizontal = GtaStartTheme.spacing.large, vertical = GtaStartTheme.spacing.medium),
                            ) {
                                Text("空空如也，请前往设置页面下载~")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            onGotoGameSettingClick.invoke()
                                        }
                                    ) { Text("前往下载") }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun ServerContent(
    modifier: Modifier = Modifier,
    showDialog: MutableState<Boolean>,
    innerPadding: PaddingValues
) {
    val pagerState = rememberPagerState(
        pageCount = { CsPayload.entries.size }
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            //.padding(GtaStartTheme.spacing.normal)
    ) {
        ServerTabs(
            pagerState = pagerState
        )
        ServerList(
            modifier = Modifier.fillMaxSize(), // 不继承InnerPadding0
            showDialog = showDialog,
            innerPadding = innerPadding,
            pagerState = pagerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServerTabs(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel = LocalServerViewModel.current,
    settingViewModel: SettingViewModel = LocalSettingViewModel.current,
    pagerState: PagerState
) {
    val index = pagerState.currentPage
    val scope = rememberCoroutineScope()
    PrimaryTabRow (
        selectedTabIndex = index,
        indicator = {
            ViewLikeIndicator(index)
        },
        tabs = {
            CsPayload.entries.forEachIndexed { i, csPayload ->
                Tab(
                    text = { Text(csPayload.title) },
                    selected = index == i,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(i)
                        }
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    showDialog: MutableState<Boolean>
) {
    if (!isCompatHorizontal()) {
        TopAppBar(
            title = {
                Text(stringResource(R.string.server_list), modifier = modifier.padding(start = GtaStartTheme.spacing.small))
            },
            navigationIcon = {
                Image(
                    painter = painterResource(R.drawable.rn_logo),
                    contentDescription = null,
                    modifier = modifier
                        .padding(start = GtaStartTheme.spacing.normal)
                        .size(36.dp)
                        .clip(CircleShape),
                )
            },
            actions = {
                val density = LocalDensity.current

                /// 设置自定义启动命令
                Text(stringResource(R.string.custom_autoexec_cmd),
                    style = MaterialTheme.typography.bodyMedium)
                IconButton(
                    onClick = {
                        showDialog.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ServerList(
    modifier: Modifier = Modifier,
    showDialog: MutableState<Boolean>,
    innerPadding: PaddingValues,
    pagerState: PagerState,
    viewModel: ServerViewModel = LocalServerViewModel.current
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (Constants.appUpdateInfo.value != null) {
            viewModel.refreshServerList(pagerState.settledPage) // 每次重组都刷新列表
        }
    }
    LaunchedEffect(Constants.appUpdateInfo.value) {
        viewModel.refreshServerList(pagerState.settledPage)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val serverList = viewModel.serverInfoList
    var isRefreshing by viewModel.isRefreshing

    val openDialog = rememberSaveable { mutableStateOf(false) }
    val openSelectVersionDialog = rememberSaveable { mutableStateOf(false) }

    val serverDetailStr = remember { mutableStateOf(AnnotatedString("")) }
    val currentServerIP = rememberSaveable { mutableStateOf("") }
    val needPassword = rememberSaveable { mutableStateOf(false) }

    when {
        openDialog.value -> { // 服务器详情弹窗
            MCustomAlertDialog(
                title = stringResource(R.string.detail),
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
                        modifier = Modifier
                    ) {
                        Text(serverDetailStr.value)
                        LaunchedEffect(Unit) {
                            viewModel.loadNickName() // 加载昵称
                        }
                        TextField( // 昵称
                            singleLine = true,
                            maxLines = 1,
                            value = viewModel.nickName.value,
                            onValueChange = {
                                viewModel.nickName.value = it
                            },
                            label = {
                                Text(stringResource(R.string.please_input_nickname), maxLines = 1)
                            },
                            modifier = Modifier.focusable(),
                        )
                        if (needPassword.value) {
                            TextField( // 密码
                                singleLine = true,
                                maxLines = 1,
                                value = viewModel.password.value,
                                onValueChange = {
                                    viewModel.password.value = it
                                },
                                label = {
                                    Text("密码", maxLines = 1)
                                },
                                modifier = Modifier.focusable(),
                            )
                        }
                    }

                },
                positiveButtonText = stringResource(R.string.start_game),
                onPositiveButtonClick = {
                    openDialog.value = false
                    openSelectVersionDialog.value = true // 打开选择版本弹窗
                },
                onDismissRequest = { openDialog.value = false }
            )
        }
    }

    when {
        openSelectVersionDialog.value -> { // 选择版本弹窗
            SelectVersionDialog(
                viewModel = viewModel,
                currentServerIP = currentServerIP,
                hasPassword = needPassword,
                pagerState = pagerState,
                openDialog = openDialog,
                openSelectVersionDialog = openSelectVersionDialog
            )
        }
    }

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refreshServerList(pagerState.settledPage)
        }
    ) {
        var serverCsType by viewModel.serverCsType

        LaunchedEffect(pagerState.settledPage) {
            viewModel.serverPayload.value = CsPayload.entries[pagerState.settledPage].payload
            viewModel.refreshServerList(pagerState.settledPage)
            serverCsType = CsPayload.entries[pagerState.settledPage].csType
        }

        HorizontalPager(
            state = pagerState
        ) { page ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
                contentPadding = PaddingValues(GtaStartTheme.spacing.normal),
            ) {
                itemsIndexed(serverList[page], key = null) { index, item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        ServerListItemCard(
                            item = item,
                            onClick = {
                                currentServerIP.value = item.serverIP.orEmpty()
                                needPassword.value = item.hasPassword
                                serverDetailStr.value = buildAnnotatedString {
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) { // Bold失效
                                        append("${item.serverName}\n")
                                    }
                                    append("""
                                        ${context.getString(R.string.map)}：${item.serverMap}
                                        ${context.getString(R.string.player_count)}：${item.playerCountInfo}
                                        ${context.getString(R.string.ping)}：${item.ping} ms
                                        ${context.getString(R.string.password)}：${if (item.hasPassword) context.getString(R.string.yes) else context.getString(R.string.no)}
                                    """.trimIndent())
                                }
                                openDialog.value = true // 打开服务器详情弹窗
                            }
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun ServerListItemCard(
    modifier: Modifier = Modifier,
    item: SampQueryInfoBean,
    onClick: () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable { onClick.invoke() }
                .fillMaxWidth()
                .matchContentHeight()
                .padding(vertical = GtaStartTheme.spacing.medium)
                .padding(start = GtaStartTheme.spacing.normal, end = GtaStartTheme.spacing.medium)
        ) {
            Icon(
                modifier = Modifier.padding(end = GtaStartTheme.spacing.normal).size(GtaStartTheme.spacing.medium),
                imageVector = if (item.hasPassword) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = if (item.hasPassword) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.serverName?: stringResource(R.string.get_failed),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
                Text(
                    text = item.serverMap?: stringResource(R.string.get_failed),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .padding(horizontal = GtaStartTheme.spacing.normal)
                    .fillMaxHeight()
            ) {
                Text(
                    text = item.playerCountInfo?:"0 / 0",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = "" + item.ping.toString() + " ms",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )

            }
        }
    }
}