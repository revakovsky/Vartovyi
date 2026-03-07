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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.revakovskyi.vartovyi.navigation.NavGraph
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = VartovyiTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    NavGraph(navController = navController)
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
