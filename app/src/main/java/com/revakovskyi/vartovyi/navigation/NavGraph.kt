package com.revakovskyi.vartovyi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
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
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isRequiredPermissionsGranted: Boolean,
    onRefreshPermissions: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.Home,
    ) {
        composable<Routes.Home> {
            HomeScreen(
                isRequiredPermissionsGranted = isRequiredPermissionsGranted,
                onNavigateToKeywords = {
                    navController.navigate(
                        route = Routes.Keywords,
                        navOptions = tabNavOptions()
                    )
                },
                onNavigateToLog = { logEntryId ->
                    navController.navigate(
                        route = Routes.Log(highlightedLogEntryId = logEntryId),
                        navOptions = tabNavOptions()
                    )
                },
                onNavigateToPermissions = { navController.navigate(Routes.Permissions) },
            )
        }

        composable<Routes.Keywords> {
            KeywordsScreen()
        }

        composable<Routes.Log> { backStackEntry ->
            val route: Routes.Log = backStackEntry.toRoute()
            LogScreen(
                highlightedLogEntryId = route.highlightedLogEntryId,
            )
        }

        composable<Routes.Settings> {
            SettingsScreen(
                onNavigateToHome = {
                    navController.navigate(
                        route = Routes.Home,
                        navOptions = tabNavOptions()
                    )
                },
            )
        }

        composable<Routes.Permissions> {
            PermissionsScreen(
                onNavigateBack = { navController.navigateUp() },
                onRefreshPermissions = onRefreshPermissions,
            )
        }
    }
}
