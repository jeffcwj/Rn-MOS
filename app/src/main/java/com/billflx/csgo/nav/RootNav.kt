package com.billflx.csgo.nav

import android.provider.DocumentsContract.Root
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.billflx.csgo.page.InstallGuideScreen
import com.billflx.csgo.page.MainScreen
import com.gtastart.common.util.compose.navigateSingleTopTo

enum class RootDesc(
    val route: String
) {
    Main("main_screen"),
    InstallGuide("install_guide_screen")
}


@Composable
fun RootNav() {
    val navController = rememberNavController()
    RootNavHost(navController)
}

@Composable
fun RootNavHost(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = rootNavController,
        startDestination = RootDesc.Main.route,
        modifier = modifier
    ) {
        composable(route = RootDesc.Main.route) {
            MainScreen(
                rootNavController = rootNavController,
            ) // 主页
        }
        composable(route = RootDesc.InstallGuide.route) {
            InstallGuideScreen() // 安装引导页
        }
    }
}