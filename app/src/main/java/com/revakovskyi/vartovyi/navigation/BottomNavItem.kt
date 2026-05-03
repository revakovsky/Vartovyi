package com.revakovskyi.vartovyi.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.revakovskyi.vartovyi.R

sealed class BottomNavItem(
    val route: Routes,
    @param:StringRes val labelResId: Int,
    @param:DrawableRes val iconResId: Int,
) {

    data object Home : BottomNavItem(
        route = Routes.Home,
        labelResId = R.string.nav_home,
        iconResId = R.drawable.home,
    )

    data object Keywords : BottomNavItem(
        route = Routes.Keywords,
        labelResId = R.string.nav_keywords,
        iconResId = R.drawable.words,
    )

    data object Logs : BottomNavItem(
        route = Routes.Log(),
        labelResId = R.string.nav_log,
        iconResId = R.drawable.logs,
    )

    data object Settings : BottomNavItem(
        route = Routes.Settings,
        labelResId = R.string.nav_settings,
        iconResId = R.drawable.settings,
    )

    companion object {
        val all: List<BottomNavItem> by lazy { listOf(Home, Keywords, Logs, Settings) }
    }

}
