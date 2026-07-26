package com.revakovskyi.vartovyi.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.PermissionsStatus
import com.revakovskyi.vartovyi.navigation.BottomNavItem
import com.revakovskyi.vartovyi.navigation.NavGraph
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiBottomBar
import com.revakovskyi.vartovyi.ui.components.VartovyiTopBar
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsUiContract
import com.revakovskyi.vartovyi.ui.screen.keywords.KeywordsViewModel
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsTopBarActionsIcon
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentScreen
import com.revakovskyi.vartovyi.ui.screen.legal.LegalConsentViewModel
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
    private val permissionsViewModel: PermissionsViewModel by viewModel()
    private val keywordsViewModel: KeywordsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            VartovyiTheme {
                MainActivityContent(
                    onFinish = { this@MainActivity.finish() },
                    onRefreshPermissions = ::updatePermissionsState,
                )
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

    @Composable
    private fun MainActivityContent(
        onFinish: () -> Unit,
        onRefreshPermissions: () -> Unit,
    ) {
        val mainState by mainViewModel.state.collectAsStateWithLifecycle()
        val permissionsState by permissionsViewModel.state.collectAsStateWithLifecycle()
        val legalConsentState by legalConsentViewModel.state.collectAsStateWithLifecycle()

        if (legalConsentState.isLoading || mainState.isOnboardingLoading) {
            LoadingOverlay()
        } else if (!legalConsentState.isAccepted) {
            LegalConsentScreen(
                viewModel = legalConsentViewModel,
                onRefuse = onFinish,
            )
        } else {
            val startDestination: Any =
                if (!mainState.isOnboardingCompleted) Routes.Onboarding()
                else Routes.Home

            MainAppScaffold(
                startDestination = startDestination,
                isAlarmRunning = mainState.isAlarmRunning,
                monitoringState = mainState.monitoringState,
                permissionsStatus = permissionsState.permissionsStatus,
                keywordsViewModel = keywordsViewModel,
                onRefreshPermissions = onRefreshPermissions,
                onStopAlarm = { mainViewModel.onAction(MainUiContract.Action.StopAlarm) },
            )
        }
    }

    @Composable
    private fun MainAppScaffold(
        startDestination: Any,
        isAlarmRunning: Boolean,
        monitoringState: com.revakovskyi.vartovyi.model.MonitoringState,
        permissionsStatus: PermissionsStatus,
        keywordsViewModel: KeywordsViewModel,
        onRefreshPermissions: () -> Unit,
        onStopAlarm: () -> Unit,
    ) {
        val navController = rememberNavController()
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        val keywordsState by keywordsViewModel.state.collectAsStateWithLifecycle()

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

        val isOnboarding = currentDestination?.hasRoute(Routes.Onboarding::class) == true

        val selectedNavItem: BottomNavItem? = when {
            currentDestination?.hasRoute(Routes.Home::class) == true -> BottomNavItem.Home
            currentDestination?.hasRoute(Routes.Log::class) == true -> BottomNavItem.Logs
            currentDestination?.hasRoute(Routes.Settings::class) == true -> BottomNavItem.Settings
            currentDestination?.hasRoute(Routes.Keywords::class) == true -> BottomNavItem.Keywords
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
                    if (selectedNavItem != null) {
                        VartovyiTopBar(
                            title = topBarTitle,
                            permissionsStatus = permissionsStatus,
                            isEmergencyStopVisible = isAlarmRunning,
                            scrollBehavior = topBarScrollBehavior,
                            trailingActions = when (selectedNavItem) {
                                BottomNavItem.Keywords -> {
                                    {
                                        KeywordsTopBarActionsIcon(
                                            isExportEnabled = keywordsState.canExport,
                                            isClearEnabled = keywordsState.hasKeywordDataToClear,
                                            onExportClick = {
                                                keywordsViewModel.onAction(
                                                    KeywordsUiContract.Action.RequestExport
                                                )
                                            },
                                            onImportClick = {
                                                keywordsViewModel.onAction(
                                                    KeywordsUiContract.Action.RequestImport
                                                )
                                            },
                                            onClearClick = {
                                                keywordsViewModel.onAction(
                                                    KeywordsUiContract.Action.OpenClearKeywordsDialog
                                                )
                                            },
                                        )
                                    }
                                }

                                BottomNavItem.Logs -> {
                                    {
                                        IconButton(onClick = { showLogInfoDialog = true }) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(VartovyiTheme.spacing.extraSmall)
                                                    .background(
                                                        shape = CircleShape,
                                                        color = VartovyiTheme.colors.onSurfaceVariant
                                                            .copy(alpha = 0.35f),
                                                    )
                                            ) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.info),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(VartovyiTheme.spacing.standard),
                                                )
                                            }
                                        }
                                    }
                                }

                                else -> null
                            },
                            onPermissionsClick = { navController.navigate(Routes.Permissions) },
                            onEmergencyStopClick = onStopAlarm,
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
                    permissionsStatus = permissionsStatus,
                    onRefreshPermissions = onRefreshPermissions,
                    isLogInfoDialogVisible = showLogInfoDialog,
                    onDismissLogInfoDialog = { showLogInfoDialog = false },
                    keywordsViewModel = keywordsViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isOnboarding) Modifier
                            else Modifier
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues)
                        )
                )
            }
        }
    }

}
