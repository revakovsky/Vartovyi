package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun LogEmptyState(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        ) {
            Text(
                text = stringResource(R.string.log_empty),
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onBackground,
            )

            Text(
                text = stringResource(R.string.log_empty_hint),
                style = VartovyiTheme.typography.bodySmall,
                color = VartovyiTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "Log empty state")
@Composable
private fun LogEmptyStatePreview() {
    VartovyiTheme {
        LogEmptyState()
    }
}
