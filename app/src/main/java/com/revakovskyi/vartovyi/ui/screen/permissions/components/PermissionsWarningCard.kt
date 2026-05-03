package com.revakovskyi.vartovyi.ui.screen.permissions.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun PermissionsWarningCard(
    modifier: Modifier = Modifier,
) {
    Surface(
        color = VartovyiTheme.colors.errorContainer,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.permissions_warning),
            style = VartovyiTheme.typography.bodyMedium,
            color = VartovyiTheme.colors.onErrorContainer,
            modifier = Modifier.padding(VartovyiTheme.spacing.standard),
        )
    }
}

@Preview
@Composable
private fun PermissionsWarningCardPreview() {
    VartovyiTheme {
        PermissionsWarningCard()
    }
}
