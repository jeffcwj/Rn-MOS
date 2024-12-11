package com.billflx.csgo.page

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.androlua.LuaEditor
import com.billflx.csgo.MainActivity
import com.billflx.csgo.bean.AutoExecCmdBean
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.bean.SampQueryPlayerBean
import com.billflx.csgo.nav.LocalServerViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MDialog
import com.gtastart.common.util.MOSDialog
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.mPlaceholder
import com.gtastart.common.util.compose.matchContentHeight
import com.gtastart.common.util.compose.matchContentWidth
import com.gtastart.common.util.compose.widget.MAlertDialog
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.gtastart.common.util.compose.widget.MIconButton
import com.gtastart.common.util.extend.StartActivity
import com.gtastart.common.util.isBlank
import com.myopicmobile.textwarrior.common.Language
import com.myopicmobile.textwarrior.common.LanguageAutoExecCmd
import com.myopicmobile.textwarrior.common.LanguageC
import com.myopicmobile.textwarrior.common.LanguageLua
import com.myopicmobile.textwarrior.common.Lexer
import com.valvesoftware.source.R
import kotlinx.coroutines.delay
import me.nillerusr.LauncherActivity
import org.libsdl.app.SDLActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerPage(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel = LocalServerViewModel.current
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.server_list), modifier = modifier.padding(start = GtaStartTheme.spacing.small))
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
                }
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal),
                horizontalAlignment = Alignment.End
            ) {
                /*FloatingActionButton(
                    onClick = {
                        viewModel.refreshServerList()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                }*/

                ExtendedFloatingActionButton (
                    onClick = {
                        CSMOSUtils.removeAutoConnectInfo()
                        if (context is MainActivity) {
                            context.startSource()
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Text(stringResource(R.string.launch_game_to_main_interface))
                }

                val density = LocalDensity.current
                var showDialog = rememberSaveable { mutableStateOf(false) }
                when {
                    showDialog.value -> {
                        EditAutoExecDialog( // 编辑自定义参数弹窗
                            showDialog = showDialog
                        )
                    }
                }
                /// 设置自定义启动命令
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.custom_autoexec_cmd),
                        style = MaterialTheme.typography.bodyMedium)
                    IconButton(
                        onClick = {
                            showDialog.value = true
//                            CustomExecCmdEditor(context = context, density = density)
                        }
                    ) {
                        Icon(
                            modifier = modifier.width(18.dp),
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        ServerContent(
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun EditAutoExecDialog(
    modifier: Modifier = Modifier,
    showDialog: MutableState<Boolean>
) {
    val context = LocalContext.current
    val showToturial = rememberSaveable { mutableStateOf(false) }
    val showCmds = rememberSaveable { mutableStateOf(false) }
    // 存储用户输入内容
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    textState.value = TextFieldValue(CSMOSUtils.removeAutoConnectInfo())
    MCustomAlertDialog( // 设置启动命令弹窗
        title = stringResource(R.string.launch_cmd),
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
            ) {
                DynamicHighlightedTextField(textState = textState)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                ) {
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(stringResource(R.string.show_toturial))
                        }
                    }, modifier = modifier.clickable {
                        showToturial.value = !showToturial.value
                    })
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append(stringResource(R.string.view_common_cmds))
                        }
                    }, modifier = modifier.clickable {
                        showCmds.value = !showCmds.value
                    })
                }

                when { // 教程区域
                    showToturial.value -> {
                        Text(stringResource(R.string.tip_one_line_one_cmd))
                        Box( // 演示边框
                            modifier = modifier
                                .border(
                                    width = 1.dp,
                                    color = colorResource(R.color.md_theme_primary),
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(GtaStartTheme.spacing.normal)
                        ) {
                            Text( // 演示文本
                                style = MaterialTheme.typography.bodySmall,
                                text = "sv_pure -1\npassword 123456"
                            )
                        }
                    }
                }

                when { // 查看常用命令弹窗
                    showCmds.value -> {
                        val viewModel = LocalServerViewModel.current
                        LaunchedEffect(Unit) {
                            viewModel.getAutoExecCmds()
                        }
                        MCustomAlertDialog(
                            title = stringResource(R.string.common_cmds),
                            content = {
                                if (viewModel.isAutoExecCmdLoading.value) {
                                    CircularProgressIndicator()
                                }
                                LazyColumn(
                                    modifier = modifier
                                ) {
                                    items(viewModel.autoExecCmdList) { item ->
                                        ExecCmdsItem(
                                            item = item,
                                            textState = textState,
                                            showCmds = showCmds
                                        )
                                    }
                                }
                            },
                            onDismissRequest = {
                                showCmds.value = false
                            }
                        )
                    }
                }
            }
        },
        positiveButtonText = stringResource(R.string.save),
        onPositiveButtonClick = {
            // 保存
            CSMOSUtils.writeAutoExecText(textState.value.text)
            MToast.show(context, context.getString(R.string.save_finished))
            showDialog.value = false
        },
        onDismissRequest = {
            showDialog.value = false
        }
    )
}

@Composable
private fun ExecCmdsItem(
    modifier: Modifier = Modifier,
    item: AutoExecCmdBean,
    textState: MutableState<TextFieldValue>,
    showCmds: MutableState<Boolean>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(GtaStartTheme.spacing.normal))
            .clickable {
                textState.value = TextFieldValue("${textState.value.text.trimStart()}${if (!textState.value.text.isBlank())"\n" else ""}${item.cmd}")
                showCmds.value = false
            }
            .padding(GtaStartTheme.spacing.normal)

    ) {
        Text(
            text = item.cmd?:"",
            style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = item.usage?:"",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun DynamicHighlightedTextField(
    modifier: Modifier = Modifier,
    textState: MutableState<TextFieldValue>
) {
    // 样式：key 普通样式，value 高亮样式
    val keyStyle = SpanStyle(color = colorResource(R.color.md_theme_tertiary), fontSize = 16.sp) // MaterialTheme.colorScheme.onPrimary 为啥走的是LightTheme
    val valueStyle = SpanStyle(color = colorResource(R.color.md_theme_primary), fontSize = 16.sp)

//    TextField()
    Box( // 弄个假的文本外边框
        modifier = modifier
            .border(
                width = 1.dp,
                color = colorResource(R.color.md_theme_primary),
                shape = MaterialTheme.shapes.small // 圆角形状
            )
            .padding(GtaStartTheme.spacing.medium) // 内边距与边框之间的间距
    ) {
        BasicTextField(
            value = textState.value,
            onValueChange = { newValue ->
                textState.value = newValue
            },
            modifier = modifier,
            cursorBrush = SolidColor(TextFieldDefaults.colors().cursorColor), // 光标颜色
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Transparent),
            decorationBox = { innerTextField ->
                // 创建高亮文本
                val annotatedText = buildAnnotatedString {
                    val lines = textState.value.text.split("\n") // 按行分割
                    lines.forEachIndexed { index, line ->
                        val parts = line.split(" ", limit = 2) // 按第一个空格分割成 key 和 value
                        val key = parts.getOrNull(0) ?: ""
                        val value = parts.getOrNull(1) ?: ""

                        // 为 key 部分添加样式
                        append(AnnotatedString(key, keyStyle))

                        // 为 value 部分添加样式
                        if (value.isNotEmpty()) {
                            append(" ")
                            append(AnnotatedString(value, valueStyle))
                        }

                        // 添加换行符（除最后一行）
                        if (index != lines.size - 1) {
                            append("\n")
                        }
                    }
                }

                // 显示带样式的内容
                BasicText(
                    text = annotatedText,
                    modifier = Modifier.fillMaxWidth(),
                    style = LocalTextStyle.current.copy(fontSize = 16.sp),
                )

                // 渲染原始文本编辑框（透明）以保持可编辑性
                innerTextField()
            }
        )
    }
}
private fun CustomExecCmdEditor(
    modifier: Modifier = Modifier,
    context: Context,
    density: Density
) {
/*    val luaEditor = LuaEditor(context)
    Lexer.setLanguage(LanguageAutoExecCmd.getInstance())
    luaEditor.setPanelLanguage(LanguageAutoExecCmd.getInstance())
    val topPx = with(density) { 16.dp.toPx() }
    luaEditor.setPadding(0, topPx.toInt(),0,0)
    luaEditor.layoutParams = ViewGroup.LayoutParams(-1, 200)
    MaterialAlertDialogBuilder(context)
        .setTitle(context.getString(R.string.launch_cmd))
        .setView(luaEditor)
        .show()
    */

    // 唐完了 activity ComposeView 里某Compose onClick调用AlertDialog setView
    // 套 ComposeView 套 AndroidView factory 里创建 EditText 会无法正确获取焦点，输入法无法弹出
    // 实测，dialog这个ComposeView以后，focus就不正常了，又不想老实用Compose的dialog 真tm难用
    /*MOSDialog.show(
        context = context,
        title = "启动命令",
        customView = { dialog ->
            TextField(
                value = "测试",
                onValueChange = {}
            )
            *//*AndroidView(
                modifier = modifier.heightIn(max = 300.dp),
                factory = { context ->
                    val luaEditor = LuaEditor(context)
                    luaEditor.layoutParams = ViewGroup.LayoutParams(-1, -2)
                    val et = EditText(context)
                    et.layoutParams = ViewGroup.LayoutParams(-1, -2)
                    et
                }) { view ->
                    val autoExecText = CSMOSUtils.readAutoExecText()
                    view.setText(autoExecText)
                }*//*
        }
    )*/
}

@Composable
private fun ServerContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(GtaStartTheme.spacing.normal)
    ) {
        ServerList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServerList(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel = LocalServerViewModel.current
) {
    LaunchedEffect(Unit) {
        viewModel.refreshServerList() // 每次重组都刷新列表
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 每次回到此界面都都刷新 会慢半拍
//                viewModel.refreshServerList()
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val serverList = viewModel.serverInfoList
    var isRefreshing by viewModel.isRefreshing
    val refreshState = rememberPullToRefreshState()

    val openDialog = rememberSaveable { mutableStateOf(false) }

    val serverDetailStr = rememberSaveable { mutableStateOf("") }
    val currentServerIP = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 游戏结束以后刷新列表数据
        viewModel.refreshServerList()
    }

    when {
        openDialog.value -> {
            MCustomAlertDialog(
                title = stringResource(R.string.detail),
                content = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                    ) {
                        Text(serverDetailStr.value)

                        TextField(
                            maxLines = 1,
                            value = viewModel.nickName.value,
                            onValueChange = {
                                viewModel.nickName.value = it
                                viewModel.saveNickName()
                            },
                            label = {
                                Text(stringResource(R.string.please_input_nickname), maxLines = 1)
                            },
                            modifier = modifier
                        )
                    }

                },
                positiveButtonText = stringResource(R.string.start_game),
                onPositiveButtonClick = {
                    if (!viewModel.saveNickName()) {
                        context.MToast(context.getString(R.string.nickname_cannot_empty))
                        return@MCustomAlertDialog
                    }
                    CSMOSUtils.saveNickName(viewModel.nickName.value)
                    CSMOSUtils.saveAutoConnectInfo(currentServerIP.value)
                    val intent = Intent(context, SDLActivity::class.java)
                    launcher.launch(intent) // 回调要刷新列表数据
                    /*if (context is MainActivity) {
                        context.startSource()
                    }*/
                    openDialog.value = false
                },
                onDismissRequest = {openDialog.value = false}
            )
        }
    }

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        state = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.refreshServerList()
        }
    ) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
        ) {
            item {

            }

            itemsIndexed(serverList) { index, item ->
                ServerListItemCard(
                    item = item,
                    onClick = {
                        currentServerIP.value = item.serverIP.orEmpty()

                        serverDetailStr.value = """
                            ${item.serverName}
                            ${context.getString(R.string.map)}：${item.serverMap}
                            ${context.getString(R.string.player_count)}：${item.playerCountInfo}
                            ${context.getString(R.string.ping)}：${item.ping} ms
                        """.trimIndent()
                        openDialog.value = true
                    }
                )
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
            modifier = modifier
                .clickable { onClick.invoke() }
                .fillMaxWidth()
                .matchContentHeight()
                .padding(GtaStartTheme.spacing.medium)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
                modifier = modifier.weight(1f)
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
                modifier = modifier
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