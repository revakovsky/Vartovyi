package com.revakovskyi.vartovyi.ui.screen.permissions.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun PermissionsRefreshButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.permissions_refresh_statuses),
            style = VartovyiTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
private fun PermissionsRefreshButtonPreview() {
    VartovyiTheme {
        PermissionsRefreshButton(
            onClick = {},
        )
    }
}
