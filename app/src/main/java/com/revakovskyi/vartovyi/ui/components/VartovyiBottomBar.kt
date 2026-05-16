package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import com.revakovskyi.vartovyi.navigation.BottomNavItem
import com.revakovskyi.vartovyi.navigation.Routes
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun VartovyiBottomBar(
    modifier: Modifier = Modifier,
    selectedRoute: Routes?,
    onNavigate: (route: Routes) -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(VartovyiTheme.spacing.small)
        ) {
            BottomNavItem.all.forEach { item ->
                BottomBarItem(
                    item = item,
                    isSelected = selectedRoute == item.route,
                    itemPadding = PaddingValues(all = VartovyiTheme.spacing.none),
                    onNavigate = onNavigate,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    modifier: Modifier = Modifier,
    item: BottomNavItem,
    isSelected: Boolean,
    itemPadding: PaddingValues,
    onNavigate: (route: Routes) -> Unit,
) {
    val contentColor =
        if (isSelected) VartovyiTheme.colors.primary
        else VartovyiTheme.colors.onSurfaceVariant

    val interactionSource = remember { MutableInteractionSource() }

    val defaultFontSize: TextUnit = VartovyiTheme.typography.bodySmall.fontSize

    var labelFontSize by remember(item.labelResId) { mutableStateOf(defaultFontSize) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = interactionSource,
            ) { onNavigate(item.route) }
            .padding(itemPadding),
    ) {
        Icon(
            painter = painterResource(item.iconResId),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(VartovyiTheme.spacing.huge),
        )

        Text(
            text = stringResource(item.labelResId),
            style = VartovyiTheme.typography.bodySmall.copy(fontSize = labelFontSize),
            color = contentColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            onTextLayout = { result ->
                if (result.hasVisualOverflow) labelFontSize *= 0.9f
            },
        )
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

@Preview
@Composable
private fun BottomBarItemPreview() {
    VartovyiTheme {
        BottomBarItem(
            item = BottomNavItem.Home,
            isSelected = true,
            itemPadding = PaddingValues(all = VartovyiTheme.spacing.small),
            onNavigate = {},
        )
    }
}
