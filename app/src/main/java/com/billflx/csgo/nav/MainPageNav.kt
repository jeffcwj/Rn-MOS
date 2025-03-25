package com.billflx.csgo.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.billflx.csgo.page.MainPage
import com.billflx.csgo.page.ServerPage
import com.billflx.csgo.page.ServerViewModel
import com.billflx.csgo.page.SettingPage
import com.billflx.csgo.page.SettingViewModel
import com.billflx.csgo.page.settings.MainSettingsNav
import com.billflx.csgo.page.settings.game.GameSettings
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.ui.forum.csmos.CsMosResPage
import com.valvesoftware.source.R

/**
 * 页面导航
 */

enum class MainPageDestination(
    val iconDefault: ImageVector,
    val iconSelected: ImageVector,
    val route: String,
    @StringRes val labelRes: Int,
) {
    AHome(
        Icons.Default.Home,
        Icons.Outlined.Home,
        "a_home",
        R.string.home
    ),
    AGameSetting(
        Icons.Default.SportsEsports,
        Icons.Outlined.SportsEsports,
        "a_game_setting",
        R.string.game
    ),
    AServer(
        Icons.Default.Dns,
        Icons.Outlined.Dns,
        "a_server",
        R.string.server
    ),
    ARes(
        Icons.Default.Forum,
        Icons.Outlined.Forum,
        "a_res",
        R.string.resource
    ),
    /*ASetting(
        Icons.Default.Settings,
        Icons.Outlined.Settings,
        "a_setting",
        R.string.setting
    ),*/
    ASettingV2(
        Icons.Default.Settings,
        Icons.Outlined.Settings,
        "a_setting_v2",
        R.string.setting
    );

    fun getIcon(selected: Boolean) : ImageVector = if (selected) iconSelected else iconDefault

    companion object {
        fun list(experimental: Boolean): List<MainPageDestination> {
            return if (!experimental) {
                entries.toList().filter { it != ARes }
            } else {
                entries.toList()
            }
        }
    }

}

@Composable
fun MainPageNav(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    rootNavController: NavHostController
) {

    CompositionLocalProvider(
    ) {
        NavHost(
            navController = navController,
            startDestination = MainPageDestination.AHome.route,
            modifier = modifier
        ) {
            composable(route = MainPageDestination.AHome.route) {
                MainPage(
                    onStartInstallGuide = {
                        rootNavController.navigateSingleTopTo(RootDesc.InstallGuide.route)
                    }
                )
            }
            composable(route = MainPageDestination.AServer.route) {
                ServerPage()
            }
            composable(route = MainPageDestination.AGameSetting.route) {
                GameSettings()
            }

            // TODO 记得添加回来
            composable(route = MainPageDestination.ARes.route) {
                val rootNav = LocalRootNav.current
                CsMosResPage(
                    onGotoResPostDetailClick = { id ->
                        rootNav.navigateSingleTopTo("${RootDesc.ResPost.route}/$id")
                    },
                    onGotoUserScreenClick = { id ->
                        rootNav.navigateSingleTopTo("${RootDesc.WpUserScreen.route}/$id")
                    }
                )
            }

            /*composable(route = MainPageDestination.ASetting.route) {
                SettingPage(navController = navController)
            }*/
            composable(route = MainPageDestination.ASettingV2.route) {
                MainSettingsNav()
            }
        }
    }
}