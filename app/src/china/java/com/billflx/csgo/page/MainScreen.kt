package com.billflx.csgo.page

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.billflx.csgo.data.AppLocalDataSource
import com.billflx.csgo.nav.MainPageDestination
import com.billflx.csgo.nav.MainPageNav
import com.gtastart.common.util.compose.navigateSingleTopTo

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = MainPageDestination.list(
        experimental = AppLocalDataSource.isExperimental() // 是否启用实验性页面 （也就是资源页）
    ).find { it.route == currentDestination?.route } ?: MainPageDestination.AHome

    LaunchedEffect(Unit) {
        navController.navigateSingleTopTo(
            AppLocalDataSource.getLastLaunchPage()
        )
    }
    NavigationSuiteScaffold(
        containerColor = Color.Transparent,
        navigationSuiteItems = {
            MainPageDestination.list(
                experimental = AppLocalDataSource.isExperimental()
            ).forEachIndexed { index, it ->
                item(
                    selected = it == currentScreen,
                    icon = {
                        Icon(
                            imageVector = it.getIcon(it == currentScreen),
                            contentDescription = null
                        )
                    },
                    onClick = {
                        navController.navigateSingleTopTo(it.route)
                        AppLocalDataSource.setLastLaunchPage(it.route)
                        /*if (it.route == MainPageDestination.AServer.route) {
                            navController.navigateWithoutSaveTo(it.route)
                        } else {
                            navController.navigateSingleTopTo(it.route)
                        }*/
                    },
                    label = {
                        Text(text = stringResource(it.labelRes))
                    }
                )
            }
        }
    ) {
        MainPageNav(
            navController = navController,
            rootNavController = rootNavController
        )
    }
}