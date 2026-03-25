package com.revakovskyi.vartovyi

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.navigation.BottomNavItem
import com.revakovskyi.vartovyi.navigation.NavGraph
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.ui.components.VartovyiBottomBar
import com.revakovskyi.vartovyi.ui.components.VartovyiTopBar
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsViewModel
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.theme.appRootBackground
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.usecase.alarm.ObserveAlarmRunningUseCase
import com.revakovskyi.vartovyi.usecase.alarm.StopAlarmUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.ObserveMonitoringStateUseCase
import com.revakovskyi.vartovyi.usecase.monitoring.SyncMonitoringRuntimeUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val permissionsViewModel: PermissionsViewModel by viewModel()
    private val observeAlarmRunningUseCase: ObserveAlarmRunningUseCase by inject()
    private val observeMonitoringStateUseCase: ObserveMonitoringStateUseCase by inject()
    private val syncMonitoringRuntimeUseCase: SyncMonitoringRuntimeUseCase by inject()
    private val stopAlarmUseCase: StopAlarmUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            VartovyiTheme {
                val permissionsState by permissionsViewModel.state.collectAsState()
                val isAlarmRunning by observeAlarmRunningUseCase().collectAsState(initial = false)
                val monitoringState by observeMonitoringStateUseCase()
                    .collectAsState(initial = MonitoringState.INACTIVE)

                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination

                val snackbarHostState = remember { SnackbarHostState() }

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

                val selectedNavItem: BottomNavItem? = when {
                    currentDestination?.hasRoute(Routes.Home::class) == true -> BottomNavItem.Home
                    currentDestination?.hasRoute(Routes.Log::class) == true -> BottomNavItem.Logs
                    currentDestination?.hasRoute(Routes.Settings::class) == true -> BottomNavItem.Settings
                    currentDestination?.hasRoute(Routes.Keywords::class) == true -> BottomNavItem.Keywords
                    else -> null
                }

                val showBars = selectedNavItem != null
                val topBarScrollBehavior = provideTopBarScrollBehavior(selectedNavItem)

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
                        .appRootBackground(monitoringState = monitoringState)
                ) {
                    Scaffold(
                        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                            .exclude(WindowInsets.ime),
                        containerColor = Color.Transparent,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
                        topBar = {
                            if (showBars) {
                                VartovyiTopBar(
                                    title = topBarTitle,
                                    hasMissingPermissions = permissionsState.hasMissingPermissions,
                                    isEmergencyStopVisible = isAlarmRunning,
                                    scrollBehavior = topBarScrollBehavior,
                                    onPermissionsClick = { navController.navigate(Routes.Permissions) },
                                    onEmergencyStopClick = {
                                        lifecycleScope.launch { stopAlarmUseCase() }
                                    },
                                )
                            }
                        },
                        bottomBar = {
                            if (showBars) {
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
                            isRequiredPermissionsGranted = permissionsState.allGranted,
                            onRefreshPermissions = ::updatePermissionsState,
                            modifier = Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            syncMonitoringRuntimeUseCase()
        }
        updatePermissionsState()
    }

    private fun updatePermissionsState() {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val listenerGranted = NotificationManagerCompat
            .getEnabledListenerPackages(this)
            .contains(packageName)

        val postNotificationsGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasRuntimePermission = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                hasRuntimePermission && NotificationManagerCompat.from(this)
                    .areNotificationsEnabled()
            } else {
                NotificationManagerCompat.from(this).areNotificationsEnabled()
            }

        val batteryOptimizationIgnored = getSystemService(PowerManager::class.java)
            .isIgnoringBatteryOptimizations(packageName)

        val doNotDisturbAccessGranted = notificationManager.isNotificationPolicyAccessGranted

        val fullScreenIntentGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                postNotificationsGranted && notificationManager.canUseFullScreenIntent()
            } else {
                postNotificationsGranted
            }

        permissionsViewModel.updatePermissionsState(
            listenerGranted = listenerGranted,
            batteryOptimizationIgnored = batteryOptimizationIgnored,
            doNotDisturbAccessGranted = doNotDisturbAccessGranted,
            postNotificationsGranted = postNotificationsGranted,
            fullScreenIntentGranted = fullScreenIntentGranted,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun provideTopBarScrollBehavior(selectedNavItem: BottomNavItem?): TopAppBarScrollBehavior =
        when (selectedNavItem) {
            BottomNavItem.Keywords,
            BottomNavItem.Logs,
            BottomNavItem.Settings,
                -> TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())

            else -> TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())
        }

}
