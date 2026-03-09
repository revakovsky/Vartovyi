package com.revakovskyi.vartovyi.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.revakovskyi.vartovyi.ui.screen.home.HomeScreen
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsScreen
import com.revakovskyi.vartovyi.ui.screen.log.LogScreen
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsScreen
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsScreen

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
                onNavigateToKeywords = { navController.navigate(Routes.Keywords) },
                onNavigateToLog = { navController.navigate(Routes.Log) },
                onNavigateToSettings = { navController.navigate(Routes.Settings) },
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
