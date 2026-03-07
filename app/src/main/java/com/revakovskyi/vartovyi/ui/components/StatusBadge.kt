package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun StatusBadge(
    modifier: Modifier = Modifier,
    state: MonitoringState,
) {
    val color = when (state) {
        MonitoringState.ACTIVE -> VartovyiTheme.colors.primary
        MonitoringState.SCHEDULED -> VartovyiTheme.colors.secondary
        MonitoringState.INACTIVE -> VartovyiTheme.colors.surfaceVariant
    }

    Box(
        modifier = modifier
            .background(color = color, shape = VartovyiTheme.shapes.medium)
            .padding(
                horizontal = VartovyiTheme.spacing.standard,
                vertical = VartovyiTheme.spacing.small
            )
    ) {
        Text(
            text = state.name,
            style = VartovyiTheme.typography.labelLarge,
            color = VartovyiTheme.colors.onPrimary,
        )
    }
}

@Preview
@Composable
private fun StatusBadgePreview() {
    VartovyiTheme {
        StatusBadge(state = MonitoringState.ACTIVE)
    }
}
