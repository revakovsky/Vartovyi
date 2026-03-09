package com.revakovskyi.vartovyi.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.revakovskyi.vartovyi.ui.screen.home.HomeScreen
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsScreen
import com.revakovskyi.vartovyi.ui.screen.log.LogScreen
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsScreen
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsScreen

private fun tabNavOptions() = navOptions {
    popUpTo<Routes.Home> { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = Modifier.padding(paddingValues),
    ) {
        composable<Routes.Home> {
            HomeScreen(
                onNavigateToKeywords = {
                    navController.navigate(
                        route = Routes.Keywords,
                        navOptions = tabNavOptions()
                    )
                },
                onNavigateToLog = {
                    navController.navigate(
                        route = Routes.Log,
                        navOptions = tabNavOptions()
                    )
                },
                onNavigateToSettings = {
                    navController.navigate(
                        route = Routes.Settings,
                        navOptions = tabNavOptions()
                    )
                },
                onNavigateToPermissions = { navController.navigate(Routes.Permissions) },
            )
        }

        composable<Routes.Keywords> {
            KeywordsScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Routes.Log> {
            LogScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Routes.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }

        composable<Routes.Permissions> {
            PermissionsScreen(
                onNavigateBack = { navController.navigateUp() },
            )
        }
    }
}
