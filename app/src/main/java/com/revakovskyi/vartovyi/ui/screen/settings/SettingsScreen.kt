package com.revakovskyi.vartovyi.ui.screen.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmDurationSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmSoundSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmVolumeSection
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
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val disableMonitoringMessage = stringResource(R.string.settings_test_alarm_disable_monitoring)
    val homeActionLabel = stringResource(R.string.nav_home)
    val defaultAlarmSoundTitle = stringResource(R.string.settings_alarm_sound_default)
    val alarmSoundPickerTitle = stringResource(R.string.settings_alarm_sound_picker_title)

    val selectedAlarmSoundTitle = remember(
        context,
        state.alarmSoundUri,
        defaultAlarmSoundTitle,
    ) {
        resolveAlarmSoundTitle(
            context = context,
            alarmSoundUri = state.alarmSoundUri,
            defaultAlarmSoundTitle = defaultAlarmSoundTitle,
        )
    }

    val alarmSoundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

        val selectedAlarmSoundUri = parsePickedAlarmSoundUri(result.data)
            ?.toString()
            .orEmpty()

        viewModel.onAction(SettingsUiContract.Action.SetAlarmSoundUri(selectedAlarmSoundUri))
    }

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

    Crossfade(
        targetState = state.isLoading,
        animationSpec = tween(durationMillis = 500),
        label = "settings_loading_crossfade",
    ) { isLoading ->
        if (isLoading) {
            LoadingOverlay()
        } else {
            SettingsContent(
                state = state,
                onAction = viewModel::onAction,
                selectedAlarmSoundTitle = selectedAlarmSoundTitle,
                onChooseAlarmSound = {
                    alarmSoundPickerLauncher.launch(
                        createAlarmSoundPickerIntent(
                            existingAlarmSoundUri = state.alarmSoundUri,
                            pickerTitle = alarmSoundPickerTitle,
                        )
                    )
                },
            )
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiContract.State,
    selectedAlarmSoundTitle: String,
    onAction: (action: SettingsUiContract.Action) -> Unit,
    onChooseAlarmSound: () -> Unit,
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
            modifier = Modifier.fillMaxSize()
        ) {
            AlarmSoundSection(
                selectedSoundTitle = selectedAlarmSoundTitle,
                onChooseSoundClick = onChooseAlarmSound,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VartovyiTheme.spacing.standard)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            AlarmDurationSection(
                durationSeconds = state.alarmDurationSeconds,
                onDurationChange = { seconds ->
                    onAction(SettingsUiContract.Action.SetAlarmDuration(seconds))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = VartovyiTheme.spacing.standard)
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            AlarmVolumeSection(
                volumePercent = state.alarmVolumePercent,
                onVolumeChange = { percent ->
                    onAction(SettingsUiContract.Action.SetAlarmVolume(percent))
                },
                modifier = Modifier.fillMaxWidth()
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
            selectedAlarmSoundTitle = "Default alarm sound",
            onAction = {},
            onChooseAlarmSound = {},
        )
    }
}

private fun createAlarmSoundPickerIntent(
    existingAlarmSoundUri: String,
    pickerTitle: String,
): Intent {
    val existingUri = existingAlarmSoundUri
        .takeIf { it.isNotBlank() }
        ?.let { Uri.parse(it) }
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    return Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_TYPE,
            RingtoneManager.TYPE_ALARM,
        )
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_TITLE,
            pickerTitle,
        )
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
            true,
        )
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
            false,
        )
        putExtra(
            RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
            existingUri,
        )
    }
}

private fun parsePickedAlarmSoundUri(intent: Intent?): Uri? {
    if (intent == null) return null

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(
            RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
            Uri::class.java,
        )
    } else {
        @Suppress("DEPRECATION")
        intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
    }
}

private fun resolveAlarmSoundTitle(
    context: Context,
    alarmSoundUri: String,
    defaultAlarmSoundTitle: String,
): String {
    val selectedUri = alarmSoundUri
        .takeIf { it.isNotBlank() }
        ?.let { Uri.parse(it) }
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: return defaultAlarmSoundTitle

    return runCatching {
        RingtoneManager.getRingtone(context, selectedUri)?.getTitle(context)
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: defaultAlarmSoundTitle
}
