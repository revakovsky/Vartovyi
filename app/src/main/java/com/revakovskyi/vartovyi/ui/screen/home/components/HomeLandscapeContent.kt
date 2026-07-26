package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.screen.home.HomeUiContract
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

/** Cards on the left, shield on the right — a single column would not fit a short window */
@Composable
internal fun HomeLandscapeContent(
    modifier: Modifier = Modifier,
    state: HomeUiContract.State,
    homeContentLayoutCoordinates: () -> LayoutCoordinates?,
    onToggleMonitoring: () -> Unit,
    onAction: (action: HomeUiContract.Action) -> Unit,
    onSecurityIconCenterInHomeContentChanged: (center: Offset) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .displayCutoutPadding()
            .padding(horizontal = VartovyiTheme.spacing.standard)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            HomeCardsSection(
                state = state,
                onAction = onAction,
                modifier = Modifier.widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            StatusBlock(
                monitoringState = state.monitoringState,
                alarmRetriggerCooldownMillis = state.alarmRetriggerCooldownMillis,
                toggleButtonVerticalPadding = VartovyiTheme.spacing.small,
                onToggle = onToggleMonitoring,
                homeContentLayoutCoordinates = homeContentLayoutCoordinates,
                onSecurityIconCenterInHomeContentChanged =
                    onSecurityIconCenterInHomeContentChanged,
                modifier = Modifier
                    .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                    .fillMaxHeight()
            )
        }
    }
}

@Preview(name = "Home landscape — small phone", widthDp = 640, heightDp = 320)
@Composable
private fun HomeLandscapeContentSmallPhonePreview() {
    VartovyiTheme {
        HomeLandscapeContent(
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

@Preview(name = "Home landscape — tablet", widthDp = 1280, heightDp = 800)
@Composable
private fun HomeLandscapeContentTabletPreview() {
    VartovyiTheme {
        HomeLandscapeContent(
            state = HomeUiContract.State(
                isLoading = false,
                monitoringState = MonitoringState.ACTIVE,
                keywords = listOf("харків", "ракета+харків"),
                lastAlertEvent = AlertEvent(
                    id = "1",
                    timestamp = 1_700_000_000_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Тривога • Харків",
                    messageText = "Увага! Повітряна тривога в області, пройдіть в укриття.",
                    matchedKeyword = "харків",
                    status = AlertEventStatus.ALARM_TRIGGERED,
                ),
            ),
            homeContentLayoutCoordinates = { null },
            onToggleMonitoring = {},
            onAction = {},
            onSecurityIconCenterInHomeContentChanged = {},
        )
    }
}
