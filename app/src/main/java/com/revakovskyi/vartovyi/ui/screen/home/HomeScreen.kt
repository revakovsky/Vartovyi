package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.constants.DEFAULT_KEYWORDS_SEED
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus
import com.revakovskyi.vartovyi.model.MonitoringState
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.screen.home.components.HomeLandscapeContent
import com.revakovskyi.vartovyi.ui.screen.home.components.HomeMonitoringActiveContentEffect
import com.revakovskyi.vartovyi.ui.screen.home.components.HomePortraitContent
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarAction
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    isRequiredPermissionsGranted: Boolean,
    onNavigateToKeywords: () -> Unit,
    onNavigateToLog: (logEntryId: String?) -> Unit,
    onNavigateToPermissions: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val permissionsRequiredMessage = stringResource(R.string.home_permissions_required_snackbar)
    val permissionsAction = stringResource(R.string.home_permissions_required_snackbar_action)

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is HomeUiContract.Event.NavigateToKeywords -> onNavigateToKeywords()
            is HomeUiContract.Event.NavigateToLog -> onNavigateToLog(event.logEntryId)
        }
    }

    Crossfade(
        targetState = state.isLoading,
        animationSpec = tween(durationMillis = 500),
        label = "home_loading_crossfade",
    ) { isLoading ->
        if (isLoading) {
            LoadingOverlay()
        } else {
            HomeContent(
                state = state,
                isRequiredPermissionsGranted = isRequiredPermissionsGranted,
                onAction = viewModel::onAction,
                onShowPermissionsRequiredMessage = {
                    coroutineScope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = permissionsRequiredMessage,
                                action = SnackbarAction(
                                    name = permissionsAction,
                                    action = { onNavigateToPermissions() }
                                ),
                                duration = SnackbarDuration.Long,
                            )
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeUiContract.State,
    isRequiredPermissionsGranted: Boolean,
    onAction: (action: HomeUiContract.Action) -> Unit,
    onShowPermissionsRequiredMessage: () -> Unit,
) {
    val windowSize = LocalWindowInfo.current.containerSize

    var homeContentLayoutCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var securityIconCenterInHomeContent by remember { mutableStateOf<Offset?>(null) }

    val isTwoPaneLayout = windowSize.width > windowSize.height

    val onToggleMonitoring: () -> Unit = {
        val isTryingToActivate = state.monitoringState != MonitoringState.ACTIVE

        if (isTryingToActivate && !isRequiredPermissionsGranted) {
            onShowPermissionsRequiredMessage()
        } else {
            onAction(HomeUiContract.Action.ToggleMonitoring)
        }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                homeContentLayoutCoordinates = coordinates
            }
    ) {
        if (state.monitoringState == MonitoringState.ACTIVE) {
            HomeMonitoringActiveContentEffect(
                ringCenterInParent = securityIconCenterInHomeContent,
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (isTwoPaneLayout) {
            HomeLandscapeContent(
                state = state,
                homeContentLayoutCoordinates = { homeContentLayoutCoordinates },
                onToggleMonitoring = onToggleMonitoring,
                onAction = onAction,
                onSecurityIconCenterInHomeContentChanged = { center ->
                    securityIconCenterInHomeContent = center
                },
            )
        } else {
            HomePortraitContent(
                state = state,
                homeContentLayoutCoordinates = { homeContentLayoutCoordinates },
                onToggleMonitoring = onToggleMonitoring,
                onAction = onAction,
                onSecurityIconCenterInHomeContentChanged = { center ->
                    securityIconCenterInHomeContent = center
                },
            )
        }
    }
}

@Preview(name = "Inactive — empty")
@Composable
private fun HomeContentInactivePreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(),
            isRequiredPermissionsGranted = false,
            onAction = {},
            onShowPermissionsRequiredMessage = {},
        )
    }
}

@Preview(name = "Active — real keywords, card hidden")
@Composable
private fun HomeContentActiveWithKeywordsPreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(
                monitoringState = MonitoringState.ACTIVE,
                keywords = listOf("ракета", "вибух", "тривога", "атака", "бомба"),
            ),
            isRequiredPermissionsGranted = true,
            onAction = {},
            onShowPermissionsRequiredMessage = {},
        )
    }
}

@Preview(name = "Active — only default keywords, CTA shown")
@Composable
private fun HomeContentActiveWithDefaultKeywordsPreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(
                monitoringState = MonitoringState.ACTIVE,
                keywords = DEFAULT_KEYWORDS_SEED,
            ),
            isRequiredPermissionsGranted = true,
            onAction = {},
            onShowPermissionsRequiredMessage = {},
        )
    }
}

@Preview(name = "Active — with last alert")
@Composable
private fun HomeContentActiveWithAlertPreview() {
    VartovyiTheme {
        HomeContent(
            state = HomeUiContract.State(
                monitoringState = MonitoringState.ACTIVE,
                keywords = listOf("ракета", "тривога"),
                lastAlertEvent = AlertEvent(
                    id = "1",
                    timestamp = 1_700_000_000_000L,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Повітряна тривога",
                    messageText = "Повітряна тривога в Київській та Харківській областях. " +
                            "Просимо негайно зайти у найближче укриття.",
                    matchedKeyword = "тривога",
                    status = AlertEventStatus.ALARM_TRIGGERED,
                ),
            ),
            isRequiredPermissionsGranted = true,
            onAction = {},
            onShowPermissionsRequiredMessage = {},
        )
    }
}
