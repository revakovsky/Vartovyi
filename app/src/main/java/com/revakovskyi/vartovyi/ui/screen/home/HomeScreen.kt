package com.revakovskyi.vartovyi.ui.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.MonitoringState
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.screen.home.components.KeywordsCard
import com.revakovskyi.vartovyi.ui.screen.home.components.LastAlertCard
import com.revakovskyi.vartovyi.ui.screen.home.components.StatusBlock
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
    val state by viewModel.state.collectAsState()

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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        StatusBlock(
            monitoringState = state.monitoringState,
            onToggle = {
                val isTryingToActivate = state.monitoringState != MonitoringState.ACTIVE

                if (isTryingToActivate && !isRequiredPermissionsGranted) {
                    onShowPermissionsRequiredMessage()
                } else {
                    onAction(HomeUiContract.Action.ToggleMonitoring)
                }
            },
            modifier = Modifier.weight(1f),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
            modifier = Modifier.padding(horizontal = VartovyiTheme.spacing.standard),
        ) {
            KeywordsCard(
                keywords = state.keywords,
                onAddKeywords = { onAction(HomeUiContract.Action.NavigateToKeywords) },
                onMoreClick = { onAction(HomeUiContract.Action.NavigateToKeywords) },
            )

            LastAlertCard(
                lastAlertEvent = state.lastAlertEvent,
                onClick = {
                    onAction(
                        HomeUiContract.Action.NavigateToLog(
                            logEntryId = state.lastAlertEvent?.id,
                        ),
                    )
                },
                modifier = Modifier.padding(bottom = VartovyiTheme.spacing.small)
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

@Preview(name = "Active — with keywords")
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
                    messageText = "Повітряна тривога в Київській та Харківській областях. Просимо негайно зайти у найближче укриття.",
                    matchedKeyword = "тривога",
                ),
            ),
            isRequiredPermissionsGranted = true,
            onAction = {},
            onShowPermissionsRequiredMessage = {},
        )
    }
}
