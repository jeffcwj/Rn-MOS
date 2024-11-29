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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.billflx.csgo.nav.LocalSettingViewModel
import com.gtastart.common.theme.GtaStartTheme
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
                    Text("设置", modifier = modifier.padding(start = GtaStartTheme.spacing.small))
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
            .padding(GtaStartTheme.spacing.medium),
    ) {
        ScrollingContent(
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScrollingContent(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = LocalSettingViewModel.current
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.refreshGamePath()
    }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.saveSettings()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.medium)
    ) {
       Row(
           modifier = modifier.fillMaxWidth(),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
       ) {
           TextField(
               modifier = modifier.weight(1f),
               value = viewModel.gamePath.value,
               onValueChange = {
                   viewModel.gamePath.value = it
               },
               label = {
                   Text(stringResource(R.string.srceng_launcher_game_path))
               }
           )
           MButton(
               text = stringResource(R.string.select),
               onClick = {
//                   viewModel.startDirchActivity() // 傻逼东西
//                   StartActivity<DirchActivity>(context)
                   val intent = Intent(context, DirchActivity::class.java)
                   launcher.launch(intent)
               }
           )
       }

        TextField(
            modifier = modifier.fillMaxWidth(),
            value = viewModel.argv.value,
            onValueChange = {
                viewModel.argv.value = it
                viewModel.saveArgv()
            },
            label = {
                Text(stringResource(R.string.srceng_launcher_command_args))
            }
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(GtaStartTheme.spacing.normal)
        ) {
            MButton(
                text = "分辨率1280x960 4:3",
                onClick = {
                    viewModel.setResolution(width = 1280, height = 960)
                    viewModel.saveArgv()
                }
            )
            MButton(
                text = "分辨率1920x1200 16:10",
                onClick = {
                    viewModel.setResolution(width = 1920, height = 1200)
                    viewModel.saveArgv()
                }
            )

            MButton(
                text = "分辨率2560x1600 16:10",
                onClick = {
                    viewModel.setResolution(width = 2560, height = 1600)
                    viewModel.saveArgv()
                }
            )
            MButton(
                text = "恢复默认",
                onClick = {
                    viewModel.resetArgsToDefault()
                    viewModel.saveArgv()
                }
            )
        }
        TextField(
            modifier = modifier.fillMaxWidth(),
            value = viewModel.env.value,
            onValueChange = {
                viewModel.env.value = it
            },
            label = {
                Text(stringResource(R.string.srceng_launcher_env))
            }
        )
        MButton(
            text = "前往原始启动器",
            onClick = {
                StartActivity<LauncherActivity>(context)
            }
        )
    }

}
