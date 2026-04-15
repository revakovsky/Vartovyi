package com.revakovskyi.vartovyi.ui.screen.keywords

import android.content.ClipData
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.TriggerKeywordRule
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsBackupRow
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsClearButton
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.components.StopWordsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.components.TelegramChannelsSection
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.rememberKeywordsBackupHelper
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/** Delay to let the keyboard fully animate open before scrolling the active field into view. */
private const val BRING_INTO_VIEW_DELAY_MS = 400L
private const val KEYWORDS_CHIP_CLIP_LABEL = "keywords_chip"

@Composable
fun KeywordsScreen(
    viewModel: KeywordsViewModel = koinViewModel(),
) {
    val clipboardManager = LocalClipboard.current
    val hapticFeedback = LocalHapticFeedback.current

    val state by viewModel.state.collectAsState()

    val chipCopiedMessage = stringResource(R.string.keywords_chip_copied)
    val keywordsClearedMessage = stringResource(R.string.keywords_clear_completed)
    val importSuccessMessage = stringResource(R.string.keywords_import_success)
    val exportSuccessMessage = stringResource(R.string.keywords_export_success)
    val exportErrorMessage = stringResource(R.string.keywords_export_error)
    val importInvalidFormatMessage = stringResource(R.string.keywords_import_invalid_format)
    val importUnsupportedVersion = stringResource(R.string.keywords_import_unsupported_version)
    val importWriteErrorMessage = stringResource(R.string.keywords_import_write_error)

    val backupHelper = rememberKeywordsBackupHelper(onAction = viewModel::onAction)

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is KeywordsUiContract.Event.KeywordAdded,
            is KeywordsUiContract.Event.KeywordRemoved,
            is KeywordsUiContract.Event.StopWordAdded,
            is KeywordsUiContract.Event.StopWordRemoved,
            is KeywordsUiContract.Event.TelegramChannelAdded,
            is KeywordsUiContract.Event.TelegramChannelRemoved,
                -> hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

            is KeywordsUiContract.Event.ChipCopied -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                SnackbarController.sendEvent(SnackbarEvent(message = chipCopiedMessage))

                clipboardManager.setClipEntry(
                    ClipEntry(ClipData.newPlainText(KEYWORDS_CHIP_CLIP_LABEL, event.text))
                )
            }

            is KeywordsUiContract.Event.KeywordsScreenDataCleared -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = keywordsClearedMessage),
                )
            }

            is KeywordsUiContract.Event.KeywordsExportSuccess -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = exportSuccessMessage),
                )
            }

            is KeywordsUiContract.Event.KeywordsExportError -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = exportErrorMessage),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportSuccess -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = importSuccessMessage),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportInvalidFormat -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = importInvalidFormatMessage)
                )
            }

            is KeywordsUiContract.Event.KeywordsImportUnsupportedVersion -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = String.format(
                            importUnsupportedVersion,
                            event.fileVersion
                        )
                    ),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportWriteError -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = importWriteErrorMessage),
                )
            }

            is KeywordsUiContract.Event.LaunchExportFilePicker,
            is KeywordsUiContract.Event.LaunchImportFilePicker,
                -> backupHelper.handleEvent(event)
        }
    }

    Crossfade(
        targetState = state.isLoading,
        animationSpec = tween(durationMillis = 500),
        label = "keywords_loading_crossfade",
    ) { isLoading ->
        if (isLoading) {
            LoadingOverlay()
        } else {
            KeywordsContent(
                state = state,
                onAction = viewModel::onAction,
            )
        }
    }

    if (state.duplicateWord != null) {
        VartovyiDialog(
            title = stringResource(R.string.keywords_duplicate_title),
            message = stringResource(R.string.keywords_duplicate_message, state.duplicateWord!!),
            confirmText = stringResource(R.string.keywords_duplicate_confirm),
            onDismiss = { viewModel.onAction(KeywordsUiContract.Action.DismissDuplicateWordDialog) },
        )
    }

    state.pendingRemoval?.let { pendingRemoval ->
        val pendingRemovalValue = when (pendingRemoval) {
            is KeywordsUiContract.PendingRemoval.Keyword -> pendingRemoval.keywordRule.displayValue
            is KeywordsUiContract.PendingRemoval.StopWord -> pendingRemoval.stopWord
            is KeywordsUiContract.PendingRemoval.TelegramChannel -> pendingRemoval.channel
        }

        VartovyiDialog(
            title = stringResource(R.string.keywords_remove_dialog_title),
            message = stringResource(R.string.keywords_remove_dialog_message, pendingRemovalValue),
            confirmText = stringResource(R.string.keywords_remove_dialog_confirm),
            dismissText = stringResource(R.string.keywords_remove_dialog_dismiss),
            onConfirm = { viewModel.onAction(KeywordsUiContract.Action.ConfirmPendingRemoval) },
            onDismiss = { viewModel.onAction(KeywordsUiContract.Action.DismissPendingRemovalDialog) },
        )
    }

    if (state.isClearKeywordsDialogVisible) {
        VartovyiDialog(
            title = stringResource(R.string.keywords_clear_dialog_title),
            message = stringResource(R.string.keywords_clear_dialog_message),
            confirmText = stringResource(R.string.keywords_clear_dialog_confirm),
            dismissText = stringResource(R.string.keywords_clear_dialog_dismiss),
            onDismiss = { viewModel.onAction(KeywordsUiContract.Action.DismissClearKeywordsDialog) },
            onConfirm = { viewModel.onAction(KeywordsUiContract.Action.ConfirmClearKeywords) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeywordsContent(
    modifier: Modifier = Modifier,
    state: KeywordsUiContract.State,
    onAction: (action: KeywordsUiContract.Action) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible

    val keywordsBivr = remember { BringIntoViewRequester() }
    val stopWordsBivr = remember { BringIntoViewRequester() }
    val telegramBivr = remember { BringIntoViewRequester() }
    var activeBivr by remember { mutableStateOf<BringIntoViewRequester?>(null) }

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            activeBivr?.bringIntoView()
        } else {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(activeBivr) {
        val bivr = activeBivr ?: return@LaunchedEffect
        delay(BRING_INTO_VIEW_DELAY_MS)
        bivr.bringIntoView()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = VartovyiTheme.spacing.small,
                end = VartovyiTheme.spacing.small,
                bottom = VartovyiTheme.spacing.small,
            )
    ) {
        KeywordsSection(
            bringIntoViewRequester = keywordsBivr,
            keywords = state.keywords,
            selectedTriggerKeywordRuleType = state.selectedTriggerKeywordRuleType,
            inputValue = state.inputKeyword,
            inputHint = when (state.selectedTriggerKeywordRuleType) {
                TriggerKeywordRuleType.WORD -> stringResource(R.string.keywords_trigger_hint_word)
                TriggerKeywordRuleType.ALL_WORDS -> stringResource(R.string.keywords_trigger_hint_all_words)
                TriggerKeywordRuleType.PHRASE -> stringResource(R.string.keywords_trigger_hint_phrase)
            },
            onTypeSelected = { type ->
                onAction(KeywordsUiContract.Action.SelectTriggerKeywordRuleType(type))
            },
            onInputChange = { value ->
                onAction(KeywordsUiContract.Action.UpdateKeywordInput(value))
            },
            onAdd = { onAction(KeywordsUiContract.Action.AddKeyword) },
            onCopy = { text -> onAction(KeywordsUiContract.Action.CopyChip(text)) },
            onRemove = { keywordRule ->
                onAction(KeywordsUiContract.Action.RemoveKeyword(keywordRule))
            },
            onFocusChanged = { isFocused ->
                if (isFocused) activeBivr = keywordsBivr
                else if (activeBivr == keywordsBivr) activeBivr = null
            },
        )

        StopWordsSection(
            bringIntoViewRequester = stopWordsBivr,
            stopWords = state.stopWords,
            inputValue = state.inputStopWord,
            onInputChange = { value ->
                onAction(KeywordsUiContract.Action.UpdateStopWordInput(value))
            },
            onAdd = { onAction(KeywordsUiContract.Action.AddStopWord) },
            onCopy = { text -> onAction(KeywordsUiContract.Action.CopyChip(text)) },
            onRemove = { stopWord ->
                onAction(KeywordsUiContract.Action.RemoveStopWord(stopWord))
            },
            onFocusChanged = { isFocused ->
                if (isFocused) activeBivr = stopWordsBivr
                else if (activeBivr == stopWordsBivr) activeBivr = null
            },
        )

        TelegramChannelsSection(
            bringIntoViewRequester = telegramBivr,
            isEnabled = state.isTelegramChannelFilterEnabled,
            channels = state.telegramChannels,
            inputValue = state.inputTelegramChannel,
            onToggle = { onAction(KeywordsUiContract.Action.ToggleTelegramChannelFilter) },
            onInputChange = { value ->
                onAction(KeywordsUiContract.Action.UpdateTelegramChannelInput(value))
            },
            onAdd = { onAction(KeywordsUiContract.Action.AddTelegramChannel) },
            onCopy = { text -> onAction(KeywordsUiContract.Action.CopyChip(text)) },
            onRemove = { channel ->
                onAction(KeywordsUiContract.Action.RemoveTelegramChannel(channel))
            },
            onFocusChanged = { isFocused ->
                if (isFocused) activeBivr = telegramBivr
                else if (activeBivr == telegramBivr) activeBivr = null
            },
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

        KeywordsBackupRow(
            isExportEnabled = state.canExport,
            onExportClick = { onAction(KeywordsUiContract.Action.ExportKeywords) },
            onImportClick = { onAction(KeywordsUiContract.Action.RequestImport) },
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

        KeywordsClearButton(
            isEnabled = state.hasKeywordDataToClear,
            onClick = { onAction(KeywordsUiContract.Action.OpenClearKeywordsDialog) },
            modifier = Modifier.padding(bottom = VartovyiTheme.spacing.small)
        )
    }
}

@Preview(name = "Keywords — empty", heightDp = 900)
@Composable
private fun KeywordsContentEmptyPreview() {
    VartovyiTheme {
        KeywordsContent(
            modifier = Modifier.fillMaxSize(),
            state = KeywordsUiContract.State(),
            onAction = {},
        )
    }
}

@Preview(name = "Keywords — with data", heightDp = 900)
@Composable
private fun KeywordsContentWithDataPreview() {
    VartovyiTheme {
        KeywordsContent(
            modifier = Modifier.fillMaxSize(),
            state = KeywordsUiContract.State(
                keywords = listOf(
                    TriggerKeywordRule.fromStorageValue("Салтівка"),
                    TriggerKeywordRule.fromStorageValue("ракета + харків"),
                    TriggerKeywordRule.fromStorageValue("\"вибух біля дому\""),
                ),
                stopWords = listOf("відбій", "чисто", "минула"),
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Keywords — Telegram filter on", heightDp = 900)
@Composable
private fun KeywordsContentTelegramFilterPreview() {
    VartovyiTheme {
        KeywordsContent(
            modifier = Modifier.fillMaxSize(),
            state = KeywordsUiContract.State(
                keywords = listOf(
                    TriggerKeywordRule.fromStorageValue("Салтівка"),
                    TriggerKeywordRule.fromStorageValue("ракета + харків"),
                ),
                stopWords = listOf("відбій"),
                isTelegramChannelFilterEnabled = true,
                telegramChannels = listOf("@air_alert_ua", "@kharkiv_alarm"),
            ),
            onAction = {},
        )
    }
}
