package com.billflx.csgo.nav

import android.provider.DocumentsContract.Root
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.billflx.csgo.MainActivity
import com.billflx.csgo.page.DownloadManagerScreen
import com.billflx.csgo.page.DownloadManagerViewModel
import com.billflx.csgo.page.InstallGuideScreen
import com.billflx.csgo.page.MainScreen
import com.billflx.csgo.page.SettingViewModel
import com.billflx.csgo.page.test.TestComposeRvScreen
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.ui.ServerPanel.cs.CsServerPanelScreen
import com.gtastart.ui.forum.posts.ResPostScreen
import com.gtastart.ui.forum.user.WpUserScreen

enum class RootDesc(
    val route: String
) {
    Main("main_screen"),
    InstallGuide("install_guide_screen"),
    DownloadManager("download_manager_screen"),
    CsServerPanel("cs_server_panel_screen"),
    ResPost("res_post"),
    TestComposeRv("test_compose_rv"),
    WpUserScreen("wp_user_screen")
}

val LocalSettingViewModel = staticCompositionLocalOf<SettingViewModel> {
    error("LocalSettingViewModel Not Provide")
}

val LocalRootNav = staticCompositionLocalOf<NavHostController> {
    error("LocalRootNav Not Provide")
}
val LocalDownloadManagerVM = staticCompositionLocalOf<DownloadManagerViewModel> {
    error("LocalDownloadManagerVM Not Provide")
}

@Composable
fun RootNav() {
    val navController = rememberNavController()
    val settingViewModel = hiltViewModel<SettingViewModel>(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    )
    val downloadManagerVM = hiltViewModel<DownloadManagerViewModel>()

    CompositionLocalProvider(
        LocalSettingViewModel provides settingViewModel,
        LocalRootNav provides navController,
        LocalDownloadManagerVM provides downloadManagerVM
    ) {
        RootNavHost(navController)
    }
}

@Composable
fun RootNavHost(
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = rootNavController,
        startDestination = RootDesc.WpUserScreen.route,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn()
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        }
    ) {
        composable(route = RootDesc.Main.route) {
            MainScreen(
                rootNavController = rootNavController,
            ) // 主页
        }
        composable(route = RootDesc.InstallGuide.route) {
            InstallGuideScreen() // 安装引导页
        }
        composable(route = RootDesc.DownloadManager.route) {
            DownloadManagerScreen() // 下载管理屏幕
        }
        composable(route = RootDesc.CsServerPanel.route) {
            CsServerPanelScreen() // CS服务器面板
        }
        composable(route = RootDesc.TestComposeRv.route) {
            TestComposeRvScreen() // BaseQuickAdapter融合Compose测试
        }
        composable(route = RootDesc.WpUserScreen.route) {
            WpUserScreen() // 用户主页
        }
        composable(
            route = "${RootDesc.ResPost.route}/{postId}", // 需要传入文章ID
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            ResPostScreen( // 资源帖详情页
                postId = postId.orEmpty(),
                onGoToMainScreenClick = {
                    rootNavController.popBackStack()
                }
            )
        }
    }
}