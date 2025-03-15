package com.billflx.csgo.page.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.akira.tyranoemu.ui.prefs.GroupHeader
import com.akira.tyranoemu.ui.prefs.SimpleItem
import com.billflx.csgo.constant.Constants
import com.billflx.csgo.page.settings.game.GameSettings
import com.gtastart.common.util.MHelpers
import com.gtastart.common.util.MToast
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.valvesoftware.source.R

private val mainSettingPages = listOf(
    SettingsPageChild(
        "settings_game",
        R.string.game_settings,
        Icons.Filled.SportsEsports
    ) {
        GameSettings()
    },
    SettingsPageChild(
        "settings_general",
        R.string.general_settings,
        Icons.Filled.Settings
    ) {

    },
)

private val otherSettingPages = emptyList<SettingsPageChild>()

private val rootSettingPage = SettingsPageChild(
    "settings_root",
    R.string.general_settings,
    Icons.Filled.SettingsApplications
) { MainSettings() }

private val allSettingPages = buildList {
    this.add(rootSettingPage)
    this.addAll(mainSettingPages)
    this.addAll(otherSettingPages)
}

private val allSettingPagesMap by lazy { allSettingPages.associateBy { it.route } }


private data class SettingsPageChild(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector,
    val actions: (@Composable RowScope.() -> Unit)? = null,
    val content: @Composable () -> Unit,
)

val LocalSettingPageNav = staticCompositionLocalOf<NavHostController> {
    error("LocalSettingPageNav Not Provide")
}

private val currentPage by mutableStateOf(rootSettingPage.route)

@Composable
fun MainSettingsNav(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    CompositionLocalProvider(
        LocalSettingPageNav provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = rootSettingPage.route
        ) {
            allSettingPages.forEach { page ->
                composable(route = page.route) {
                    page.content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSettings(
    modifier: Modifier = Modifier
) {
    val nav = LocalSettingPageNav.current
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.setting))
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                GroupHeader(stringResource(R.string.setting))
            }
            items(items = mainSettingPages) { page ->
                SimpleItem(
                    icon = rememberVectorPainter(page.icon),
                    text = stringResource(page.title)
                ) {
                    nav.navigateSingleTopTo(page.route)
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                GroupHeader(stringResource(R.string.more))
            }
            item {
                SimpleItem(
                    icon = painterResource(R.drawable.qq),
                    text = stringResource(R.string.join_qq_group)
                ) {
                    // 加QQ群
                    if (!MHelpers.joinQQGroup(key = Constants.RnCSQQGroupKey, context = context)) {
                        context.MToast("跳转失败")
                    }
                }
            }
        }
    }
}
