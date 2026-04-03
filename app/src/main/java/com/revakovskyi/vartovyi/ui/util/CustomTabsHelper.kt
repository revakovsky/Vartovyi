package com.revakovskyi.vartovyi.ui.util

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun openCustomChromeTab(
    context: Context,
    url: String,
) {
    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    customTabsIntent.launchUrl(context, url.toUri())
}
