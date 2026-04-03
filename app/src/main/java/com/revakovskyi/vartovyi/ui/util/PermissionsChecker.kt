package com.revakovskyi.vartovyi.ui.util

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

internal data class PermissionsCheckResult(
    val listenerGranted: Boolean,
    val batteryOptimizationIgnored: Boolean,
    val doNotDisturbAccessGranted: Boolean,
    val postNotificationsGranted: Boolean,
    val fullScreenIntentGranted: Boolean,
)

internal fun Context.checkPermissions(): PermissionsCheckResult {
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

    return PermissionsCheckResult(
        listenerGranted = listenerGranted,
        batteryOptimizationIgnored = batteryOptimizationIgnored,
        doNotDisturbAccessGranted = doNotDisturbAccessGranted,
        postNotificationsGranted = postNotificationsGranted,
        fullScreenIntentGranted = fullScreenIntentGranted,
    )
}
