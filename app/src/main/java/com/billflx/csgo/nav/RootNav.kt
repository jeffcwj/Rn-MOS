package com.billflx.csgo.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.billflx.csgo.page.MainScreen
import com.gtastart.common.util.compose.navigateSingleTopTo


const val MAIN_SCREEN = "main_screen"


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
        startDestination = MAIN_SCREEN,
        modifier = modifier
    ) {
        composable(route = MAIN_SCREEN) {
            MainScreen() // 主页
        }
        // 添加更多页
/*        composable(route = MAIN_SCREEN) {
            MainScreen()
        }*/
    }
}