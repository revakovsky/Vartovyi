package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
internal fun HomePortraitContent(
    modifier: Modifier = Modifier,
    state: HomeUiContract.State,
    homeContentLayoutCoordinates: () -> LayoutCoordinates?,
    onToggleMonitoring: () -> Unit,
    onAction: (action: HomeUiContract.Action) -> Unit,
    onSecurityIconCenterInHomeContentChanged: (center: Offset) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxSize()
    ) {
        StatusBlock(
            monitoringState = state.monitoringState,
            alarmRetriggerCooldownMillis = state.alarmRetriggerCooldownMillis,
            onToggle = onToggleMonitoring,
            homeContentLayoutCoordinates = homeContentLayoutCoordinates,
            onSecurityIconCenterInHomeContentChanged =
                onSecurityIconCenterInHomeContentChanged,
            modifier = Modifier.weight(1f)
        )

        HomeCardsSection(
            state = state,
            onAction = onAction,
        )
    }
}

@Preview(name = "Home portrait", widthDp = 360, heightDp = 800)
@Composable
private fun HomePortraitContentPreview() {
    VartovyiTheme {
        HomePortraitContent(
            state = HomeUiContract.State(
                isLoading = false,
                monitoringState = MonitoringState.INACTIVE,
                keywords = listOf("<НАЗВА_МІСТА>"),
            ),
            homeContentLayoutCoordinates = { null },
            onToggleMonitoring = {},
            onAction = {},
            onSecurityIconCenterInHomeContentChanged = {},
        )
    }
}
