package com.billflx.csgo.page

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.billflx.csgo.nav.MainPageDestination
import com.billflx.csgo.nav.MainPageNav
import com.gtastart.common.util.compose.navigateSingleTopTo
import com.gtastart.common.util.compose.navigateWithoutSaveTo

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = MainPageDestination.entries.find { it.route == currentDestination?.route } ?: MainPageDestination.AHome

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            MainPageDestination.entries.forEach {
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