package com.billflx.csgo.page

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.billflx.csgo.bean.CSVersionInfoEnum
import com.billflx.csgo.data.ModLocalDataSource
import com.billflx.csgo.nav.LocalSettingViewModel
import com.gtastart.common.theme.GtaStartTheme
import com.gtastart.common.util.compose.matchContentWidth
import com.gtastart.common.util.compose.widget.MAlertDialog
import com.gtastart.common.util.compose.widget.MButton
import com.gtastart.common.util.extend.StartActivity
import com.valvesoftware.source.R
import me.nillerusr.DirchActivity
import me.nillerusr.LauncherActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val viewModel: SettingViewModel = LocalSettingViewModel.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.setting), modifier = modifier.padding(start = GtaStartTheme.spacing.small))
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
                }
            )
        },
    ) { innerPadding ->
        SettingContent(
            modifier = modifier.padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SettingContent(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = LocalSettingViewModel.current
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(GtaStartTheme.spacing.normal),
        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.medium)
    ) {
        ScrollingContent()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScrollingContent(
    modifier: Modifier = Modifier,

) {
    SelectVersionCard()
    VersionSettingCard()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectVersionCard(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = LocalSettingViewModel.current
) {
    ElevatedCard(

    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = GtaStartTheme.spacing.normal, horizontal = GtaStartTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.small),
                modifier = modifier.weight(1f)
            ) {
                Text(stringResource(R.string.select_game_version), style = MaterialTheme.typography.titleMedium)
//                Text("", style = MaterialTheme.typography.bodyLarge)
            }
            LaunchedEffect(Unit) {
                viewModel.selectedOption.value = ModLocalDataSource.getCurrentCSVersion()
            }
            var expanded by remember { mutableStateOf(false) }
            var selectedOption by viewModel.selectedOption
            var options = viewModel.options
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { b ->
                    expanded = !expanded
                }
            ) {
                // 触发器（TextField 样式）
                OutlinedTextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true, // 禁止手动输入，仅支持点击选择
                    label = { Text(stringResource(R.string.select_version) ) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor() // 确保菜单与 TextField 对齐
                        .defaultMinSize(minWidth = 30.dp)
                )

                // 下拉菜单内容
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = modifier.widthIn(min = 30.dp).wrapContentWidth()
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.name) },
                            onClick = {
                                ModLocalDataSource.setCurrentCSVersion(option.name) // 设置当前CS版本
                                viewModel.applySettingsToModSP(ModLocalDataSource.getCurrentCSVersion())
                                selectedOption = option.name
                                expanded = false // 关闭菜单
                            }
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VersionSettingCard(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = LocalSettingViewModel.current
) {
    viewModel.settingCacheList.forEach { item ->
        ElevatedCard(

        ) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val data = result.data?.getStringExtra("result")
                if (data?.equals("OK") == true) { // 成功设置
                    item.gamePath.value = ModLocalDataSource.getGamePath()
                }
            }
            val context = LocalContext.current

            DisposableEffect(Unit) { // 切换到其他页面时
                onDispose {
                    viewModel.saveSettingsToDB() // 保存设置
                }
            }

            Column(
                modifier = modifier.padding(GtaStartTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.medium)
            ) {
                Text(item.settingTitle) // 游戏版本标题
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                ) {
                    TextField(
                        modifier = modifier.weight(1f),
                        value = item.gamePath.value,
                        onValueChange = {
                            item.gamePath.value = it
                            viewModel.saveSettingsToDB()
                        },
                        label = {
                            Text(stringResource(R.string.srceng_launcher_game_path))
                        }
                    )
                    MButton(
                        text = stringResource(R.string.select),
                        onClick = {
                            val intent = Intent(context, DirchActivity::class.java)
                            launcher.launch(intent)
                        }
                    )
                }

                TextField(
                    modifier = modifier.fillMaxWidth(),
                    value = item.argv.value,
                    onValueChange = {
                        item.argv.value = it
                        viewModel.saveSettingsToDB()
                    },
                    label = {
                        Text(stringResource(R.string.srceng_launcher_command_args))
                    }
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
                ) {
                    MButton(
                        text = "${stringResource(R.string.resolution)} 1280x960 4:3",
                        onClick = {
                            item.argv
                            viewModel.setResolution(width = 1280, height = 960, item.argv)
                            viewModel.saveSettingsToDB()
                        }
                    )
                    MButton(
                        text = "${stringResource(R.string.resolution)} 1920x1200 16:10",
                        onClick = {
                            viewModel.setResolution(width = 1920, height = 1200, item.argv)
                            viewModel.saveSettingsToDB()
                        }
                    )

                    MButton(
                        text = "${stringResource(R.string.resolution)} 2560x1600 16:10",
                        onClick = {
                            viewModel.setResolution(width = 2560, height = 1600, item.argv)
                            viewModel.saveSettingsToDB()
                        }
                    )
                    MButton(
                        text = stringResource(R.string.restore_to_default),
                        onClick = {
                            item.argv.value = item.versionEnum.getDefaultArgs()
                            viewModel.saveSettingsToDB()
                        }
                    )
                }
                TextField(
                    modifier = modifier.fillMaxWidth(),
                    value = item.env.value,
                    onValueChange = {
                        item.env.value = it
                        viewModel.saveSettingsToDB()
                    },
                    label = {
                        Text(stringResource(R.string.srceng_launcher_env))
                    }
                )
                /*MButton(
                    text = stringResource(R.string.goto_original_launcher),
                    onClick = {
                        StartActivity<LauncherActivity>(context)
                    }
                )*/
            }
        }
    }
}
