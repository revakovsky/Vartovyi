package com.revakovskyi.vartovyi

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.revakovskyi.vartovyi.navigation.BottomNavItem
import com.revakovskyi.vartovyi.navigation.NavGraph
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.ui.components.VartovyiBottomBar
import com.revakovskyi.vartovyi.ui.components.VartovyiTopBar
import com.revakovskyi.vartovyi.ui.screen.permissions.PermissionsViewModel
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val permissionsViewModel: PermissionsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        setContent {
            VartovyiTheme {
                val navController = rememberNavController()

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStackEntry?.destination

                val selectedNavItem: BottomNavItem? = when {
                    currentDestination?.hasRoute(Routes.Home::class) == true -> BottomNavItem.Home
                    currentDestination?.hasRoute(Routes.Log::class) == true -> BottomNavItem.Logs
                    currentDestination?.hasRoute(Routes.Settings::class) == true -> BottomNavItem.Settings
                    currentDestination?.hasRoute(Routes.Keywords::class) == true -> BottomNavItem.Keywords
                    else -> null
                }

                val showBars = selectedNavItem != null

                val topBarTitle = when (selectedNavItem) {
                    BottomNavItem.Home -> stringResource(R.string.app_name)
                    BottomNavItem.Logs -> stringResource(R.string.nav_log)
                    BottomNavItem.Settings -> stringResource(R.string.nav_settings)
                    BottomNavItem.Keywords -> stringResource(R.string.nav_keywords)
                    null -> ""
                }

                Scaffold(
                    containerColor = VartovyiTheme.colors.background,
                    topBar = {
                        if (showBars) {
                            VartovyiTopBar(title = topBarTitle)
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
                                            popUpTo<Routes.Home> {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        },
                                    )
                                },
                            )
                        }
                    },
                ) { paddingValues ->
                    NavGraph(
                        navController = navController,
                        paddingValues = paddingValues,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionsState()
    }

    private fun updatePermissionsState() {
        val listenerGranted = NotificationManagerCompat
            .getEnabledListenerPackages(this)
            .contains(packageName)

        val postNotificationsGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true

        val vibrateGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.VIBRATE
        ) == PackageManager.PERMISSION_GRANTED

        val fullScreenIntentGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                getSystemService(NotificationManager::class.java).canUseFullScreenIntent()
            } else true

        permissionsViewModel.updatePermissionsState(
            listenerGranted = listenerGranted,
            postNotificationsGranted = postNotificationsGranted,
            vibrateGranted = vibrateGranted,
            fullScreenIntentGranted = fullScreenIntentGranted,
        )
    }

}
