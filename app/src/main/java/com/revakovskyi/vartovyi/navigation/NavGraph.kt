package com.revakovskyi.vartovyi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.revakovskyi.vartovyi.model.PermissionsStatus
import com.revakovskyi.vartovyi.ui.screen.home.HomeScreen
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsScreen
import com.revakovskyi.vartovyi.ui.screen.log.LogScreen
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingScreen
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsScreen
import com.revakovskyi.vartovyi.ui.screen.settings.SettingsScreen

private fun tabNavOptions(): NavOptions = navOptions {
    popUpTo<Routes.Home> { saveState = true }
    launchSingleTop = true
    restoreState = true
}

private fun logHighlightNavOptions(): NavOptions = navOptions {
    popUpTo<Routes.Home> { saveState = true }
    restoreState = false
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Any,
    permissionsStatus: PermissionsStatus,
    isRecommendedGranted: Boolean,
    isLogInfoDialogVisible: Boolean,
    onRefreshPermissions: () -> Unit,
    onDismissLogInfoDialog: () -> Unit,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Routes.Onboarding> {
            OnboardingScreen(
                permissionsStatus = permissionsStatus,
                isRecommendedGranted = isRecommendedGranted,
                onClose = {
                    val navigatedUp = navController.navigateUp()
                    if (!navigatedUp) {
                        navController.navigate(Routes.Home) {
                            popUpTo<Routes.Onboarding> { inclusive = true }
                        }
                    }
                },
                onOpenPermissions = { navController.navigate(Routes.Permissions) },
                onOpenKeywords = { navController.navigate(Routes.Keywords) },
            )
        }

        composable<Routes.Home> {
            HomeScreen(
                isRequiredPermissionsGranted = permissionsStatus != PermissionsStatus.MANDATORY_MISSING,
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
            val isFromOnboarding = remember {
                navController.previousBackStackEntry
                    ?.destination
                    ?.hasRoute(Routes.Onboarding::class) == true
            }

            KeywordsScreen(
                onNavigateBack = if (isFromOnboarding) {
                    { navController.navigateUp() }
                } else {
                    null
                },
            )
        }

        composable<Routes.Log> { backStackEntry ->
            val route: Routes.Log = backStackEntry.toRoute()
            LogScreen(
                highlightedLogEntryId = route.highlightedLogEntryId,
                isInfoDialogVisible = isLogInfoDialogVisible,
                onDismissInfoDialog = onDismissLogInfoDialog,
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
                onNavigateToOnboarding = { navController.navigate(Routes.Onboarding) },
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
