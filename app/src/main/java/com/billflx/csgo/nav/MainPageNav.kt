package com.billflx.csgo.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.billflx.csgo.page.MainPage
import com.billflx.csgo.page.ServerPage
import com.billflx.csgo.page.SettingPage
import com.billflx.csgo.page.SettingViewModel
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
    AServer(
        Icons.Default.Dns,
        Icons.Outlined.Dns,
        "a_server",
        R.string.server
    ),
    ASetting(
        Icons.Default.Settings,
        Icons.Outlined.Settings,
        "a_setting",
        R.string.setting
    );

    fun getIcon(selected: Boolean) : ImageVector = if (selected) iconSelected else iconDefault
}


@Composable
fun MainPageNav(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainPageDestination.AHome.route,
        modifier = modifier
    ) {
        composable(route = MainPageDestination.AHome.route) {
            MainPage()
        }
        composable(route = MainPageDestination.AServer.route) {
            ServerPage()
        }
        composable(route = MainPageDestination.ASetting.route) {
            SettingPage(navController = navController)
        }
    }
}