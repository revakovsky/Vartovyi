package com.revakovskyi.vartovyi.ui.screen.permissions.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

private val TOP_BAR_BACK_ICON_SIZE = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsHeader(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.permissions_title),
                style = VartovyiTheme.typography.titleLarge,
                color = VartovyiTheme.colors.onBackground,
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.back),
                    contentDescription = stringResource(R.string.permissions_back),
                    tint = VartovyiTheme.colors.onBackground,
                    modifier = Modifier.size(TOP_BAR_BACK_ICON_SIZE)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
    )
}

@Preview
@Composable
private fun PermissionsHeaderPreview() {
    VartovyiTheme {
        PermissionsHeader(
            onNavigateBack = {},
        )
    }
}
