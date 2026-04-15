package com.revakovskyi.vartovyi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.revakovskyi.vartovyi.navigation.BottomNavItem
import com.revakovskyi.vartovyi.navigation.NavGraph
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiBottomBar
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.components.VartovyiTopBar
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentScreen
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentViewModel
import com.revakovskyi.vartovyi.ui.screen.onboarding.OnboardingViewModel
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsViewModel
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.theme.appRootBackground
import com.revakovskyi.vartovyi.ui.util.checkPermissions
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.topBarScrollBehavior
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val legalConsentViewModel: LegalConsentViewModel by viewModel()
    private val onboardingViewModel: OnboardingViewModel by viewModel()
    private val permissionsViewModel: PermissionsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            VartovyiTheme {
                val mainState by mainViewModel.state.collectAsStateWithLifecycle()
                val permissionsState by permissionsViewModel.state.collectAsStateWithLifecycle()
                val legalConsentState by legalConsentViewModel.state.collectAsStateWithLifecycle()
                val onboardingState by onboardingViewModel.state.collectAsStateWithLifecycle()

                if (legalConsentState.isLoading || onboardingState.isLoading) {
                    LoadingOverlay()
                } else if (!legalConsentState.isAccepted) {
                    LegalConsentScreen(
                        viewModel = legalConsentViewModel,
                        onRefuse = { this@MainActivity.finish() },
                    )
                } else {
                    val startDestination: Any =
                        if (!onboardingState.isCompleted) Routes.Onboarding
                        else Routes.Home

                    val navController = rememberNavController()
                    val currentBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = currentBackStackEntry?.destination
                    val previousDestination = navController.previousBackStackEntry?.destination

                    val snackbarHostState = remember { SnackbarHostState() }

                    var showLogInfoDialog by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        SnackbarController.events.collectLatest { event ->
                            snackbarHostState.currentSnackbarData?.dismiss()

                            val result = snackbarHostState.showSnackbar(
                                message = event.message,
                                actionLabel = event.action?.name,
                                withDismissAction = event.action != null,
                                duration = event.duration,
                            )

                            if (result == SnackbarResult.ActionPerformed) {
                                event.action?.action?.invoke()
                            }
                        }
                    }

                    val isKeywordsFromOnboarding =
                        currentDestination?.hasRoute(Routes.Keywords::class) == true &&
                                previousDestination?.hasRoute(Routes.Onboarding::class) == true

                    val selectedNavItem: BottomNavItem? = when {
                        currentDestination?.hasRoute(Routes.Home::class) == true -> BottomNavItem.Home
                        currentDestination?.hasRoute(Routes.Log::class) == true -> BottomNavItem.Logs
                        currentDestination?.hasRoute(Routes.Settings::class) == true -> BottomNavItem.Settings
                        currentDestination?.hasRoute(Routes.Keywords::class) == true && !isKeywordsFromOnboarding -> BottomNavItem.Keywords
                        else -> null
                    }

                    val topBarScrollBehavior = topBarScrollBehavior(selectedNavItem)

                    val topBarTitle = when (selectedNavItem) {
                        BottomNavItem.Home -> stringResource(R.string.app_name)
                        BottomNavItem.Logs -> stringResource(R.string.nav_log)
                        BottomNavItem.Settings -> stringResource(R.string.nav_settings)
                        BottomNavItem.Keywords -> stringResource(R.string.nav_keywords)
                        null -> ""
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .appRootBackground(monitoringState = mainState.monitoringState)
                    ) {
                        Scaffold(
                            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                                .exclude(WindowInsets.ime),
                            containerColor = Color.Transparent,
                            modifier = Modifier
                                .fillMaxSize()
                                .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
                            topBar = {
                                if (selectedNavItem != null) {
                                    VartovyiTopBar(
                                        title = topBarTitle,
                                        hasMissingPermissions = permissionsState.hasMissingPermissions,
                                        isEmergencyStopVisible = mainState.isAlarmRunning,
                                        scrollBehavior = topBarScrollBehavior,
                                        additionalActions = if (selectedNavItem == BottomNavItem.Logs) {
                                            {
                                                FilledTonalIconButton(
                                                    onClick = { showLogInfoDialog = true },
                                                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                                                        containerColor = VartovyiTheme.colors.onSurfaceVariant.copy(
                                                            alpha = 0.35f
                                                        ),
                                                    ),
                                                    modifier = Modifier.size(VartovyiTheme.spacing.extraLarge),
                                                ) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(R.drawable.info),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(VartovyiTheme.spacing.standard),
                                                    )
                                                }
                                            }
                                        } else null,
                                        onPermissionsClick = { navController.navigate(Routes.Permissions) },
                                        onEmergencyStopClick = {
                                            mainViewModel.onAction(MainUiContract.Action.StopAlarm)
                                        },
                                    )
                                }
                            },
                            bottomBar = {
                                if (selectedNavItem != null) {
                                    VartovyiBottomBar(
                                        selectedRoute = selectedNavItem.route,
                                        onNavigate = { route ->
                                            navController.navigate(
                                                route = route,
                                                navOptions = navOptions {
                                                    popUpTo<Routes.Home> { saveState = true }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                },
                                            )
                                        },
                                    )
                                }
                            },
                            snackbarHost = {
                                SnackbarHost(hostState = snackbarHostState)
                            },
                        ) { paddingValues ->
                            NavGraph(
                                navController = navController,
                                startDestination = startDestination,
                                isRequiredPermissionsGranted = permissionsState.allGranted,
                                onRefreshPermissions = ::updatePermissionsState,
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .consumeWindowInsets(paddingValues)
                            )
                        }

                        if (showLogInfoDialog) {
                            VartovyiDialog(
                                title = stringResource(R.string.log_info_dialog_title),
                                message = stringResource(R.string.log_info_dialog_body),
                                confirmText = stringResource(R.string.ok),
                                onDismiss = { showLogInfoDialog = false },
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.onAction(MainUiContract.Action.SyncMonitoringRuntime)
        updatePermissionsState()
    }

    private fun updatePermissionsState() {
        val result = checkPermissions()
        permissionsViewModel.updatePermissionsState(
            listenerGranted = result.listenerGranted,
            batteryOptimizationIgnored = result.batteryOptimizationIgnored,
            doNotDisturbAccessGranted = result.doNotDisturbAccessGranted,
            postNotificationsGranted = result.postNotificationsGranted,
            fullScreenIntentGranted = result.fullScreenIntentGranted,
        )
    }

}
