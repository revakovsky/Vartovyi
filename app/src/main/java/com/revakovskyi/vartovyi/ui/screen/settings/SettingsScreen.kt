package com.revakovskyi.vartovyi.ui.screen.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmDurationSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.SettingsTestAlarmButton
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarAction
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val disableMonitoringMessage = stringResource(R.string.settings_test_alarm_disable_monitoring)
    val homeActionLabel = stringResource(R.string.nav_home)

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is SettingsUiContract.Event.NavigateBack -> onNavigateBack()
            is SettingsUiContract.Event.ShowDisableMonitoringForTestAlarm -> {
                coroutineScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = disableMonitoringMessage,
                            action = SnackbarAction(
                                name = homeActionLabel,
                                action = { onNavigateToHome() },
                            ),
                            duration = SnackbarDuration.Long,
                        )
                    )
                }
            }

            else -> Unit
        }
    }

    SettingsContent(
        state = state,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiContract.State,
    onAction: (action: SettingsUiContract.Action) -> Unit,
) {
    val testAlarmSourceChannelName = stringResource(R.string.settings_test_alarm_channel_name)
    val testAlarmSourceMessageText = stringResource(R.string.settings_test_alarm_message_text)

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = VartovyiTheme.spacing.standard)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            AlarmDurationSection(
                durationSeconds = state.alarmDurationSeconds,
                onDurationChange = { seconds ->
                    onAction(SettingsUiContract.Action.SetAlarmDuration(seconds))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VartovyiTheme.spacing.standard)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        SettingsTestAlarmButton(
            isAlarmRunning = state.isAlarmRunning,
            onClick = {
                onAction(
                    SettingsUiContract.Action.ToggleTestAlarm(
                        sourceChannelName = testAlarmSourceChannelName,
                        sourceMessageText = testAlarmSourceMessageText,
                    )
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

}

@Preview
@Composable
private fun SettingsContentPreview() {
    VartovyiTheme {
        SettingsContent(
            state = SettingsUiContract.State(),
            onAction = {},
        )
    }
}
