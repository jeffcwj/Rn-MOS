package com.billflx.csgo.page

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.billflx.csgo.MainActivity
import com.billflx.csgo.bean.SampQueryInfoBean
import com.billflx.csgo.bean.SampQueryPlayerBean
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.CSMOSUtils
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.matchContentHeight
import com.gtastart.common.util.compose.widget.MAlertDialog
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.compose.widget.MCustomAlertDialog
import com.gtastart.common.util.extend.StartActivity
import com.valvesoftware.source.R
import kotlinx.coroutines.delay
import me.nillerusr.LauncherActivity
import org.libsdl.app.SDLActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerPage(
    modifier: Modifier = Modifier,
    viewModel: ServerViewModel = hiltViewModel()
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
            }
        }
    ) { innerPadding ->
        ServerContent(
            modifier = modifier.padding(innerPadding)
        )
    }
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
    viewModel: ServerViewModel = hiltViewModel()
) {

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
                            modifier = modifier,
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