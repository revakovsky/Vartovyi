package com.revakovskyi.vartovyi.ui.screen.keywords

import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.constants.KeywordRuleFormat
import com.revakovskyi.vartovyi.model.ImportStrategy
import com.revakovskyi.vartovyi.model.TriggerKeywordRuleType
import com.revakovskyi.vartovyi.ui.components.DialogChoice
import com.revakovskyi.vartovyi.ui.components.DialogChoiceRole
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiBackTopBar
import com.revakovskyi.vartovyi.ui.components.VartovyiChoiceDialog
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsBackupRow
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsClearButton
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsRestoreDefaultsButton
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.components.StopWordsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.components.TelegramChannelsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.model.ExportDestination
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.rememberKeywordsBackupHelper
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import com.revakovskyi.vartovyi.utils.parseTriggerKeywordRuleFromStorage
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/** Delay to let the keyboard fully animate open before scrolling the active field into view. */
private const val BRING_INTO_VIEW_DELAY_MS = 400L

/** Input row plus a slice of the suggestions list kept visible above the keyboard. */
private const val TELEGRAM_SUGGESTIONS_PEEK_HEIGHT_DP = 220
private const val KEYWORDS_CHIP_CLIP_LABEL = "keywords_chip"

@Composable
fun KeywordsScreen(
    viewModel: KeywordsViewModel = koinViewModel(),
    onNavigateBack: (() -> Unit)? = null,
) {
    val clipboardManager = LocalClipboard.current
    val hapticFeedback = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val resources = LocalResources.current

    val state by viewModel.state.collectAsStateWithLifecycle()

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

            is KeywordsUiContract.Event.KeywordNormalized -> {
                val cleanDisplayValue = event.displayValue.unwrapPhraseQuotes()
                showSnackbarWithClearFocus(
                    focusManager = focusManager,
                    message = resources.getString(
                        R.string.keywords_normalized,
                        cleanDisplayValue,
                    ),
                )
            }

            is KeywordsUiContract.Event.KeywordMultiLineNotAllowed -> {
                showSnackbarWithClearFocus(
                    focusManager = focusManager,
                    message = resources.getString(R.string.keywords_multiline_not_allowed),
                )
            }

            KeywordsUiContract.Event.KeywordStartsWithNonAlphanumeric -> {
                showSnackbarWithClearFocus(
                    focusManager = focusManager,
                    message = resources.getString(R.string.keywords_starts_with_non_alphanumeric),
                )
            }

            is KeywordsUiContract.Event.KeywordTermTooShort -> {
                val message = resources.getQuantityString(
                    R.plurals.keywords_term_too_short,
                    event.minLength,
                    event.minLength,
                )
                showSnackbarWithClearFocus(focusManager = focusManager, message = message)
            }

            is KeywordsUiContract.Event.KeywordsMaxReached -> {
                val message = resources.getQuantityString(
                    R.plurals.keywords_max_reached,
                    event.max,
                    event.max,
                )
                showSnackbarWithClearFocus(focusManager = focusManager, message = message)
            }

            is KeywordsUiContract.Event.ChipCopied -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                SnackbarController.sendEvent(
                    SnackbarEvent(message = resources.getString(R.string.keywords_chip_copied))
                )

                clipboardManager.setClipEntry(
                    ClipEntry(ClipData.newPlainText(KEYWORDS_CHIP_CLIP_LABEL, event.text))
                )
            }

            is KeywordsUiContract.Event.KeywordsScreenDataCleared -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = resources.getString(R.string.keywords_clear_completed)),
                )
            }

            is KeywordsUiContract.Event.DefaultKeywordsRestored -> {
                val message = if (event.addedCount == 0) {
                    resources.getString(R.string.keywords_restore_nothing_added)
                } else {
                    resources.getQuantityString(
                        R.plurals.keywords_restore_defaults_added,
                        event.addedCount,
                        event.addedCount,
                    )
                }
                SnackbarController.sendEvent(
                    event = SnackbarEvent(message = message)
                )
            }

            is KeywordsUiContract.Event.KeywordsExportSuccess -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = resources.getString(R.string.keywords_export_success)),
                )
            }

            is KeywordsUiContract.Event.KeywordsExportError -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(message = resources.getString(R.string.keywords_export_error)),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportSuccess -> {
                val message = when {
                    event.strategy == ImportStrategy.REPLACE -> {
                        resources.getString(R.string.keywords_import_replaced)
                    }

                    event.addedCount == 0 -> {
                        resources.getString(R.string.keywords_import_merge_nothing_added)
                    }

                    else -> {
                        resources.getQuantityString(
                            R.plurals.keywords_import_merged_added,
                            event.addedCount,
                            event.addedCount,
                            event.skippedCount,
                        )
                    }
                }
                SnackbarController.sendEvent(
                    SnackbarEvent(message = message),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportInvalidFormat -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = resources.getString(R.string.keywords_import_invalid_format)
                    )
                )
            }

            is KeywordsUiContract.Event.KeywordsImportUnsupportedVersion -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = resources.getString(
                            R.string.keywords_import_unsupported_version,
                            event.fileVersion,
                        )
                    ),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportWriteError -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = resources.getString(R.string.keywords_import_write_error)
                    ),
                )
            }

            is KeywordsUiContract.Event.KeywordsImportFileTooLarge -> {
                SnackbarController.sendEvent(
                    SnackbarEvent(
                        message = resources.getString(R.string.keywords_import_file_too_large)
                    ),
                )
            }

            is KeywordsUiContract.Event.LaunchExportFilePicker,
            is KeywordsUiContract.Event.LaunchExportShareSheet,
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
                onNavigateBack = onNavigateBack,
            )
        }
    }

    state.duplicateWord?.let { duplicateWord ->
        VartovyiDialog(
            title = stringResource(R.string.keywords_duplicate_title),
            message = stringResource(R.string.keywords_duplicate_message, duplicateWord),
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

    if (state.isRestoreDefaultsDialogVisible) {
        VartovyiDialog(
            title = stringResource(R.string.keywords_restore_defaults_dialog_title),
            message = stringResource(R.string.keywords_restore_defaults_dialog_message),
            confirmText = stringResource(R.string.keywords_restore_defaults_dialog_confirm),
            dismissText = stringResource(R.string.keywords_restore_defaults_dialog_dismiss),
            onDismiss = { viewModel.onAction(KeywordsUiContract.Action.DismissRestoreDefaultsDialog) },
            onConfirm = { viewModel.onAction(KeywordsUiContract.Action.ConfirmRestoreDefaults) },
        )
    }

    if (state.isImportStrategyDialogVisible) {
        VartovyiChoiceDialog(
            title = stringResource(R.string.keywords_import_strategy_dialog_title),
            message = stringResource(R.string.keywords_import_strategy_dialog_message),
            choices = listOf(
                DialogChoice(
                    text = stringResource(R.string.keywords_import_strategy_merge),
                    role = DialogChoiceRole.PRIMARY,
                    onClick = {
                        viewModel.onAction(
                            KeywordsUiContract.Action.SelectImportStrategy(ImportStrategy.MERGE)
                        )
                    },
                ),
                DialogChoice(
                    text = stringResource(R.string.keywords_import_strategy_replace),
                    role = DialogChoiceRole.DESTRUCTIVE,
                    onClick = {
                        viewModel.onAction(
                            KeywordsUiContract.Action.SelectImportStrategy(ImportStrategy.REPLACE)
                        )
                    },
                ),
                DialogChoice(
                    text = stringResource(R.string.keywords_import_strategy_cancel),
                    role = DialogChoiceRole.NEUTRAL,
                    onClick = {
                        viewModel.onAction(KeywordsUiContract.Action.DismissImportStrategyDialog)
                    },
                ),
            ),
            onDismiss = { viewModel.onAction(KeywordsUiContract.Action.DismissImportStrategyDialog) },
        )
    }

    if (state.isExportDestinationDialogVisible) {
        VartovyiChoiceDialog(
            title = stringResource(R.string.keywords_export_dialog_title),
            message = stringResource(R.string.keywords_export_dialog_message),
            choices = listOf(
                DialogChoice(
                    text = stringResource(R.string.keywords_export_save),
                    role = DialogChoiceRole.PRIMARY,
                    onClick = {
                        viewModel.onAction(
                            KeywordsUiContract.Action.SelectExportDestination(
                                ExportDestination.SAVE_TO_FILE
                            )
                        )
                    },
                ),
                DialogChoice(
                    text = stringResource(R.string.keywords_export_share),
                    role = DialogChoiceRole.PRIMARY,
                    onClick = {
                        viewModel.onAction(
                            KeywordsUiContract.Action.SelectExportDestination(ExportDestination.SHARE)
                        )
                    },
                ),
                DialogChoice(
                    text = stringResource(R.string.keywords_export_cancel),
                    role = DialogChoiceRole.NEUTRAL,
                    onClick = {
                        viewModel.onAction(
                            KeywordsUiContract.Action.DismissExportDestinationDialog
                        )
                    },
                ),
            ),
            onDismiss = {
                viewModel.onAction(KeywordsUiContract.Action.DismissExportDestinationDialog)
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KeywordsContent(
    modifier: Modifier = Modifier,
    state: KeywordsUiContract.State,
    onAction: (action: KeywordsUiContract.Action) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.isImeVisible

    val scrollState = rememberSaveable(saver = ScrollState.Saver) {
        ScrollState(0)
    }

    val keywordsBivr = remember { BringIntoViewRequester() }
    val stopWordsBivr = remember { BringIntoViewRequester() }
    val telegramBivr = remember { BringIntoViewRequester() }
    val telegramPeekRect = remember(density) {
        with(density) {
            Rect(
                left = 0f,
                top = 0f,
                right = 0f,
                bottom = TELEGRAM_SUGGESTIONS_PEEK_HEIGHT_DP.dp.toPx()
            )
        }
    }
    var activeBivr by remember { mutableStateOf<BringIntoViewRequester?>(null) }

    LaunchedEffect(isImeVisible) {
        if (isImeVisible) {
            activeBivr?.let { bivr ->
                bivr.bringIntoView(if (bivr == telegramBivr) telegramPeekRect else null)
            }
        } else if (activeBivr != telegramBivr) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(activeBivr) {
        val bivr = activeBivr ?: return@LaunchedEffect
        delay(BRING_INTO_VIEW_DELAY_MS)
        bivr.bringIntoView(if (bivr == telegramBivr) telegramPeekRect else null)
    }

    BackHandler(enabled = !isImeVisible && activeBivr == telegramBivr) {
        focusManager.clearFocus()
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (onNavigateBack != null) {
            VartovyiBackTopBar(
                title = stringResource(R.string.onboarding_keywords_title),
                backContentDescription = stringResource(R.string.keywords_back),
                onNavigateBack = onNavigateBack,
            )
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.weight(1f),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                modifier = Modifier
                    .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(
                        start = VartovyiTheme.spacing.small,
                        end = VartovyiTheme.spacing.small,
                        top = if (onNavigateBack != null) {
                            VartovyiTheme.spacing.small
                        } else {
                            VartovyiTheme.spacing.medium
                        },
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
                    suggestedChannels = state.suggestedTelegramChannels,
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
                    onSuggestionSelect = { channel ->
                        onAction(KeywordsUiContract.Action.SelectSuggestedTelegramChannel(channel))
                    },
                    onFocusChanged = { isFocused ->
                        if (isFocused) activeBivr = telegramBivr
                        else if (activeBivr == telegramBivr) activeBivr = null
                    },
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = VartovyiTheme.spacing.medium,
                            bottom = VartovyiTheme.spacing.small,
                        )
                ) {
                    KeywordsBackupRow(
                        isExportEnabled = state.canExport,
                        onExportClick = { onAction(KeywordsUiContract.Action.RequestExport) },
                        onImportClick = { onAction(KeywordsUiContract.Action.RequestImport) },
                    )

                    KeywordsRestoreDefaultsButton(
                        onClick = { onAction(KeywordsUiContract.Action.OpenRestoreDefaultsDialog) },
                    )

                    KeywordsClearButton(
                        isEnabled = state.hasKeywordDataToClear,
                        onClick = { onAction(KeywordsUiContract.Action.OpenClearKeywordsDialog) },
                        modifier = Modifier.padding(bottom = VartovyiTheme.spacing.small)
                    )
                }
            }
        }
    }
}

private suspend fun showSnackbarWithClearFocus(
    focusManager: FocusManager,
    message: String,
) {
    focusManager.clearFocus()
    SnackbarController.sendEvent(SnackbarEvent(message = message))
}

private fun String.unwrapPhraseQuotes(): String =
    if (length >= 2 &&
        startsWith(KeywordRuleFormat.QUOTE) &&
        endsWith(KeywordRuleFormat.QUOTE)
    ) {
        substring(1, length - 1)
    } else {
        this
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
                    parseTriggerKeywordRuleFromStorage("Салтівка"),
                    parseTriggerKeywordRuleFromStorage("ракета + харків"),
                    parseTriggerKeywordRuleFromStorage("\"вибух біля дому\""),
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
                    parseTriggerKeywordRuleFromStorage("Салтівка"),
                    parseTriggerKeywordRuleFromStorage("ракета + харків"),
                ),
                stopWords = listOf("відбій"),
                isTelegramChannelFilterEnabled = true,
                telegramChannels = listOf("@air_alert_ua", "@kharkiv_alarm"),
            ),
            onAction = {},
        )
    }
}
