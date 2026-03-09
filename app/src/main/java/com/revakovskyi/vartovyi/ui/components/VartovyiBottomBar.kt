package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.navigation.BottomNavItem
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiBottomBar(
    modifier: Modifier = Modifier,
    selectedRoute: Routes?,
    onNavigate: (route: Routes) -> Unit,
) {
    NavigationBar(
        containerColor = VartovyiTheme.colors.surface,
        modifier = modifier
    ) {
        BottomNavItem.all.forEach { item ->
            val isSelected = selectedRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        painter = painterResource(item.iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelResId),
                        style =
                            if (isSelected) VartovyiTheme.typography.titleMedium
                            else VartovyiTheme.typography.bodyMedium,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VartovyiTheme.colors.primary,
                    selectedTextColor = VartovyiTheme.colors.primary,
                    unselectedIconColor = VartovyiTheme.colors.onSurfaceVariant,
                    unselectedTextColor = VartovyiTheme.colors.onSurfaceVariant,
                    indicatorColor = VartovyiTheme.colors.surface,
                ),
            )
        }
    }
}

@Preview
@Composable
private fun VartovyiBottomBarPreview() {
    VartovyiTheme {
        VartovyiBottomBar(
            selectedRoute = Routes.Home,
            onNavigate = {},
        )
    }
}
