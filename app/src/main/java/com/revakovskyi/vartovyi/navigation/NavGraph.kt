package com.revakovskyi.vartovyi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.revakovskyi.vartovyi.ui.screen.home.HomeScreen
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsScreen
import com.revakovskyi.vartovyi.ui.screen.log.LogScreen
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingScreen
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsScreen
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsScreen

private fun tabNavOptions() = navOptions {
    popUpTo<Routes.Home> { saveState = true }
    launchSingleTop = true
    restoreState = true
}

private fun logHighlightNavOptions() = navOptions {
    popUpTo<Routes.Home> { saveState = true }
    restoreState = false
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
    isRequiredPermissionsGranted: Boolean,
    onRefreshPermissions: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Routes.Onboarding> {
            OnboardingScreen(
                onClose = {
                    navController.navigate(Routes.Home) {
                        popUpTo<Routes.Onboarding> { inclusive = true }
                    }
                },
                onOpenPermissions = { navController.navigate(Routes.Permissions) },
                onOpenKeywords = { navController.navigate(Routes.Keywords) },
            )
        }

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
                        navOptions = logHighlightNavOptions()
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
            val isFromOnboarding = navController.previousBackStackEntry
                ?.destination
                ?.hasRoute(Routes.Onboarding::class) == true

            PermissionsScreen(
                isFromOnboarding = isFromOnboarding,
                onNavigateBack = { navController.navigateUp() },
                onRefreshPermissions = onRefreshPermissions,
            )
        }
    }
}
