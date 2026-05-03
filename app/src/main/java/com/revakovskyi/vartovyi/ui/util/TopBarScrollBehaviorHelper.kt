package com.revakovskyi.vartovyi.ui.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.revakovskyi.vartovyi.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun topBarScrollBehavior(selectedNavItem: BottomNavItem?): TopAppBarScrollBehavior =
    key(selectedNavItem) {
        when (selectedNavItem) {
            BottomNavItem.Keywords,
            BottomNavItem.Logs,
            BottomNavItem.Settings,
                -> TopAppBarDefaults.enterAlwaysScrollBehavior(state = rememberTopAppBarState())

            else -> TopAppBarDefaults.pinnedScrollBehavior(state = rememberTopAppBarState())
        }
    }
