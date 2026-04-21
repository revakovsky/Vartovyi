package com.revakovskyi.vartovyi.ui.screen.settings

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmDurationSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmSoundSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.AlarmVolumeSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.DataSettingsSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.LegalDocumentsSettingsSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.ScheduleSettingsSection
import com.revakovskyi.vartovyi.ui.screen.settings.components.SettingsSectionContainer
import com.revakovskyi.vartovyi.ui.screen.settings.components.SettingsTestAlarmButton
import com.revakovskyi.vartovyi.ui.screen.settings.components.SettingsVersionFooter
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.AlarmSoundPickerHelper
import com.revakovskyi.vartovyi.ui.util.openCustomChromeTab
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarAction
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val SETTINGS_LOADING_CROSSFADE_DURATION_MILLIS = 500

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    val disableMonitoringMessage = stringResource(R.string.settings_test_alarm_disable_monitoring)
    val homeActionLabel = stringResource(R.string.nav_home)
    val alarmSoundPickerTitle = stringResource(R.string.settings_alarm_sound_picker_title)
    val factoryResetCompletedMessage = stringResource(R.string.settings_reset_factory_completed)

    val alarmSoundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult

        val selectedAlarmSoundUri = AlarmSoundPickerHelper.parsePickedAlarmSoundUri(result.data)
            ?.toString()
            .orEmpty()

        viewModel.onAction(SettingsUiContract.Action.SetAlarmSoundUri(selectedAlarmSoundUri))
    }

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
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

            is SettingsUiContract.Event.OpenUrl -> {
                openCustomChromeTab(context = context, url = event.url)
            }

            is SettingsUiContract.Event.ShowFactoryResetCompleted -> {
                coroutineScope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(message = factoryResetCompletedMessage),
                    )
                }
            }

            is SettingsUiContract.Event.OpenOnboardingGuide -> onNavigateToOnboarding()

            SettingsUiContract.Event.LaunchSoundPicker -> {
                alarmSoundPickerLauncher.launch(
                    AlarmSoundPickerHelper.createAlarmSoundPickerIntent(
                        existingAlarmSoundUri = state.alarmSoundUri,
                        pickerTitle = alarmSoundPickerTitle,
                    )
                )
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.onAction(SettingsUiContract.Action.CollapseSectionsOnScreenStop)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Crossfade(
            targetState = state.isLoading,
            animationSpec = tween(durationMillis = SETTINGS_LOADING_CROSSFADE_DURATION_MILLIS),
            label = "settings_loading_crossfade",
        ) { isLoading ->
            if (isLoading) {
                LoadingOverlay()
            } else {
                SettingsContent(
                    state = state,
                    onAction = viewModel::onAction,
                )
            }
        }

        if (state.isResetToFactoryDefaultsDialogVisible) {
            VartovyiDialog(
                title = stringResource(R.string.settings_reset_factory_dialog_title),
                message = stringResource(R.string.settings_reset_factory_dialog_message),
                confirmText = stringResource(R.string.settings_reset_factory_dialog_confirm),
                confirmContentColor = VartovyiTheme.colors.error,
                dismissText = stringResource(R.string.settings_reset_factory_dialog_dismiss),
                onConfirm = {
                    viewModel.onAction(SettingsUiContract.Action.ConfirmResetToFactoryDefaults)
                },
                onDismiss = {
                    viewModel.onAction(SettingsUiContract.Action.DismissResetToFactoryDefaultsDialog)
                },
            )
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiContract.State,
    onAction: (action: SettingsUiContract.Action) -> Unit,
) {
    val context = LocalContext.current

    val applicationVersionName = remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()
    }

    val testAlarmSourceChannelName = stringResource(R.string.settings_test_alarm_channel_name)
    val testAlarmSourceMessageText = stringResource(R.string.settings_test_alarm_message_text)
    val defaultAlarmSoundTitle = stringResource(R.string.settings_alarm_sound_default)

    val selectedAlarmSoundTitle = remember(
        context,
        state.alarmSoundUri,
        defaultAlarmSoundTitle,
    ) {
        AlarmSoundPickerHelper.resolveAlarmSoundTitle(
            context = context,
            alarmSoundUri = state.alarmSoundUri,
            defaultAlarmSoundTitle = defaultAlarmSoundTitle,
        )
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = VartovyiTheme.spacing.small)
    ) {
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
            modifier = Modifier.padding(vertical = VartovyiTheme.spacing.medium)
        )

        SettingsSectionContainer(
            title = stringResource(R.string.settings_section_data),
            isExpanded = state.expandedSection == SettingsUiContract.SettingsSection.DATA,
            onHeaderClick = {
                onAction(
                    SettingsUiContract.Action.ToggleSection(
                        section = SettingsUiContract.SettingsSection.DATA,
                    ),
                )
            },
        ) {
            DataSettingsSection(
                currentLogSizeLimit = state.logSizeLimit,
                currentAlarmCooldownDurationMillis = state.alarmRetriggerCooldownDurationMillis,
                onLogSizeLimitChange = { limit ->
                    onAction(SettingsUiContract.Action.SetLogSizeLimit(limit))
                },
                onAlarmCooldownDurationChange = { durationMillis ->
                    onAction(
                        SettingsUiContract.Action.SetAlarmRetriggerCooldownDurationMillis(
                            durationMillis
                        )
                    )
                },
                onResetToFactoryDefaultsClick = {
                    onAction(SettingsUiContract.Action.ShowResetToFactoryDefaultsDialog)
                },
            )
        }

        SettingsSectionContainer(
            title = stringResource(R.string.settings_section_sound),
            isExpanded = state.expandedSection == SettingsUiContract.SettingsSection.SOUND,
            onHeaderClick = {
                onAction(
                    SettingsUiContract.Action.ToggleSection(
                        section = SettingsUiContract.SettingsSection.SOUND,
                    ),
                )
            },
        ) {
            AlarmSoundSection(
                selectedSoundTitle = selectedAlarmSoundTitle,
                onChooseSoundClick = {
                    onAction(SettingsUiContract.Action.StartExternalPickerNavigation)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            AlarmDurationSection(
                durationSeconds = state.alarmDurationSeconds,
                onDurationChange = { seconds ->
                    onAction(SettingsUiContract.Action.SetAlarmDuration(seconds))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

            AlarmVolumeSection(
                volumePercent = state.alarmVolumePercent,
                onVolumeChange = { percent ->
                    onAction(SettingsUiContract.Action.SetAlarmVolume(percent))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        SettingsSectionContainer(
            title = stringResource(R.string.settings_section_schedule),
            titleTooltipText = stringResource(R.string.settings_section_schedule_tooltip),
            isExpanded = state.expandedSection == SettingsUiContract.SettingsSection.SCHEDULE,
            onHeaderClick = {
                onAction(
                    SettingsUiContract.Action.ToggleSection(
                        section = SettingsUiContract.SettingsSection.SCHEDULE,
                    ),
                )
            },
        ) {
            ScheduleSettingsSection(
                isScheduleEnabled = state.isScheduleEnabled,
                startTime = state.startTime,
                endTime = state.endTime,
                onScheduleEnabledChange = { enabled ->
                    onAction(SettingsUiContract.Action.SetScheduleEnabled(enabled))
                },
                onStartTimeChange = { time ->
                    onAction(SettingsUiContract.Action.SetStartTime(time))
                },
                onEndTimeChange = { time ->
                    onAction(SettingsUiContract.Action.SetEndTime(time))
                },
            )
        }

        SettingsSectionContainer(
            title = stringResource(R.string.settings_section_info),
            isExpanded = state.expandedSection == SettingsUiContract.SettingsSection.LEGAL,
            onHeaderClick = {
                onAction(
                    SettingsUiContract.Action.ToggleSection(
                        section = SettingsUiContract.SettingsSection.LEGAL,
                    ),
                )
            },
        ) {
            LegalDocumentsSettingsSection(
                onPrivacyPolicyClick = {
                    onAction(SettingsUiContract.Action.OpenPrivacyPolicy)
                },
                onTermsOfUseClick = {
                    onAction(SettingsUiContract.Action.OpenTermsOfUse)
                },
                onOpenOnboardingGuideClick = {
                    onAction(SettingsUiContract.Action.OpenOnboardingGuide)
                },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        SettingsVersionFooter(
            versionName = applicationVersionName,
            modifier = Modifier.padding(
                top = VartovyiTheme.spacing.medium,
                bottom = VartovyiTheme.spacing.large,
            )
        )
    }
}

@Preview(name = "Settings — default")
@Composable
private fun SettingsContentPreview() {
    VartovyiTheme {
        SettingsContent(
            state = SettingsUiContract.State(
                isLoading = false,
                expandedSection = SettingsUiContract.SettingsSection.DATA
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Settings — schedule on, test alarm")
@Composable
private fun SettingsContentPreviewScheduleAndAlarm() {
    VartovyiTheme {
        SettingsContent(
            state = SettingsUiContract.State(
                isLoading = false,
                isScheduleEnabled = true,
                startTime = "08:30",
                endTime = "18:00",
                alarmDurationSeconds = 90,
                alarmVolumePercent = 72,
                logSizeLimit = 1000,
                alarmRetriggerCooldownDurationMillis = 10 * 60 * 1000L,
                isAlarmRunning = true,
            ),
            onAction = {},
        )
    }
}
