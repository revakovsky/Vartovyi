package com.revakovskyi.vartovyi.ui.screen.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiBackTopBar
import com.revakovskyi.vartovyi.ui.screen.permissions.components.PermissionItemCard
import com.revakovskyi.vartovyi.ui.screen.permissions.components.PermissionsWarningCard
import com.revakovskyi.vartovyi.ui.screen.permissions.utils.buildPermissionItems
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.compose.viewmodel.koinActivityViewModel

private const val ANDROID_PACKAGE_SCHEME = "package"
private const val ACTION_MANAGE_SPECIAL_APP_ACCESSES =
    "android.settings.MANAGE_SPECIAL_APP_ACCESSES"

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel = koinActivityViewModel(),
    onNavigateBack: () -> Unit,
    onRefreshPermissions: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    val postNotificationsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val activity = context as? Activity
            val isPermanentlyDenied = activity != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.POST_NOTIFICATIONS,
                    )

            if (isPermanentlyDenied) {
                openSystemSettings(
                    context = context,
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                )
            }
        }
        onRefreshPermissions()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onRefreshPermissions()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onRefreshPermissions()

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is PermissionsUiContract.Event.NavigateBack -> onNavigateBack()
            is PermissionsUiContract.Event.LaunchPostNotificationsPermissionRequest -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    postNotificationsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    openSystemSettings(
                        context = context,
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                    )
                }
            }
            is PermissionsUiContract.Event.NavigateToSystemSettings -> {
                openSystemSettings(
                    context = context,
                    action = event.action,
                )
            }
        }
    }

    if (state.isLoading) {
        LoadingOverlay()
    } else {
        PermissionsContent(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun PermissionsContent(
    modifier: Modifier = Modifier,
    state: PermissionsUiContract.State,
    onAction: (action: PermissionsUiContract.Action) -> Unit,
) {
    val permissionItems = buildPermissionItems(state = state)

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            contentPadding = PaddingValues(
                top = VartovyiTheme.spacing.medium,
                bottom = VartovyiTheme.spacing.medium,
            ),
            modifier = Modifier
                .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                .fillMaxSize()
        ) {
            item(contentType = "header") {
                VartovyiBackTopBar(
                    title = stringResource(R.string.permissions_title),
                    backContentDescription = stringResource(R.string.permissions_back),
                    onNavigateBack = { onAction(PermissionsUiContract.Action.NavigateBack) },
                )
            }

            if (state.hasMissingPermissions) {
                item(contentType = "warning") {
                    PermissionsWarningCard(
                        modifier = Modifier.padding(horizontal = VartovyiTheme.spacing.standard)
                    )
                }
            }

            items(permissionItems) { permissionItem ->
                PermissionItemCard(
                    title = permissionItem.title,
                    description = permissionItem.description,
                    isRequired = permissionItem.isRequired,
                    isGranted = permissionItem.isGranted,
                    onAction = onAction,
                    onSwitchToggle = permissionItem.onSwitchToggle,
                    modifier = Modifier.padding(horizontal = VartovyiTheme.spacing.standard)
                )
            }
        }
    }
}

@SuppressLint("BatteryLife")
private fun openSystemSettings(
    context: Context,
    action: String,
) {
    when (action) {
        Settings.ACTION_APP_NOTIFICATION_SETTINGS -> {
            val appNotificationsIntent = Intent(action).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }

            if (tryOpenIntentOrError(context, appNotificationsIntent)) return
            else openAppDetailsSettings(context)
        }

        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> {
            val requestIntent = Intent(action).apply {
                data = Uri.fromParts(ANDROID_PACKAGE_SCHEME, context.packageName, null)
            }

            if (tryOpenIntentOrError(context, requestIntent)) return
            else {
                val optimizationSettingsIntent = Intent(
                    Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                )

                if (tryOpenIntentOrError(context, optimizationSettingsIntent)) return
                else openAppDetailsSettings(context)
            }
        }

        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT -> {
            val fullScreenIntent = Intent(action).apply {
                data = Uri.fromParts(ANDROID_PACKAGE_SCHEME, context.packageName, null)
            }

            if (tryOpenIntentOrError(context, fullScreenIntent)) return
            else {
                val specialAccessIntent = Intent(ACTION_MANAGE_SPECIAL_APP_ACCESSES)
                if (tryOpenIntentOrError(context, specialAccessIntent)) return
                else openAppDetailsSettings(context)
            }
        }

        else -> {
            val intent = Intent(action).apply {
                if (requiresPackageUri(action)) {
                    data = Uri.fromParts(ANDROID_PACKAGE_SCHEME, context.packageName, null)
                }
            }
            tryOpenIntentOrError(context = context, intent = intent)
        }
    }
}

@SuppressLint("BatteryLife")
private fun requiresPackageUri(action: String): Boolean =
    when (action) {
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT -> true
        else -> false
    }

private fun openAppDetailsSettings(context: Context) {
    val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts(ANDROID_PACKAGE_SCHEME, context.packageName, null)
    }
    tryOpenIntentOrError(context = context, intent = appDetailsIntent)
}

private fun tryOpenIntentOrError(
    context: Context,
    intent: Intent,
): Boolean = runCatching { context.startActivity(intent) }.isSuccess

@Preview(name = "Permissions — loading")
@Composable
private fun PermissionsLoadingPreview() {
    VartovyiTheme {
        LoadingOverlay()
    }
}

@Preview(name = "Permissions — empty state")
@Composable
private fun PermissionsContentPreview() {
    VartovyiTheme {
        PermissionsContent(
            state = PermissionsUiContract.State(
                isLoading = false,
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Permissions — all granted")
@Composable
private fun PermissionsContentAllGrantedPreview() {
    VartovyiTheme {
        PermissionsContent(
            state = PermissionsUiContract.State(
                isLoading = false,
                isNotificationListenerGranted = true,
                isBatteryOptimizationIgnored = true,
                isDoNotDisturbAccessGranted = true,
                isPostNotificationsGranted = true,
                isFullScreenIntentGranted = true,
                allGranted = true,
                hasMissingPermissions = false,
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Permissions — partial granted")
@Composable
private fun PermissionsContentPartialGrantedPreview() {
    VartovyiTheme {
        PermissionsContent(
            state = PermissionsUiContract.State(
                isLoading = false,
                isNotificationListenerGranted = true,
                isBatteryOptimizationIgnored = false,
                isDoNotDisturbAccessGranted = false,
                isPostNotificationsGranted = true,
                isFullScreenIntentGranted = false,
                allGranted = false,
                hasMissingPermissions = true,
            ),
            onAction = {},
        )
    }
}
