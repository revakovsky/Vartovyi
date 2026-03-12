package com.revakovskyi.vartovyi.ui.screen.keywords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.keywords.components.KeywordsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.components.StopWordsSection
import com.revakovskyi.vartovyi.ui.screen.keywords.components.TelegramChannelsSection
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/** Delay to let the keyboard fully animate open before scrolling the active field into view. */
private const val BRING_INTO_VIEW_DELAY_MS = 400L

@Composable
fun KeywordsScreen(
    viewModel: KeywordsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val state by viewModel.state.collectAsState()

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is KeywordsUiContract.Event.KeywordAdded,
            is KeywordsUiContract.Event.StopWordAdded,
            is KeywordsUiContract.Event.TelegramChannelAdded,
                -> {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
            }

            is KeywordsUiContract.Event.NavigateBack -> onNavigateBack()
        }
    }

    KeywordsContent(
        state = state,
        onAction = viewModel::onAction,
    )

    if (state.duplicateWord != null) {
        VartovyiDialog(
            title = stringResource(R.string.keywords_duplicate_title),
            message = stringResource(R.string.keywords_duplicate_message, state.duplicateWord!!),
            confirmText = stringResource(R.string.keywords_duplicate_confirm),
            onDismiss = { viewModel.onAction(KeywordsUiContract.Action.DismissDuplicateWordDialog) },
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
        verticalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.medium),
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(
                start = VartovyiTheme.spacing.standard,
                end = VartovyiTheme.spacing.standard,
                bottom = VartovyiTheme.spacing.standard,
            )
    ) {
        KeywordsSection(
            bringIntoViewRequester = keywordsBivr,
            keywords = state.keywords,
            inputValue = state.inputKeyword,
            onInputChange = { value ->
                onAction(KeywordsUiContract.Action.UpdateKeywordInput(value))
            },
            onAdd = { onAction(KeywordsUiContract.Action.AddKeyword) },
            onRemove = { keyword ->
                onAction(KeywordsUiContract.Action.RemoveKeyword(keyword))
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
            onRemove = { channel ->
                onAction(KeywordsUiContract.Action.RemoveTelegramChannel(channel))
            },
            onFocusChanged = { isFocused ->
                if (isFocused) activeBivr = telegramBivr
                else if (activeBivr == telegramBivr) activeBivr = null
            },
        )
    }
}

@Preview(name = "Keywords — empty")
@Composable
private fun KeywordsContentEmptyPreview() {
    VartovyiTheme {
        KeywordsContent(
            state = KeywordsUiContract.State(),
            onAction = {},
        )
    }
}

@Preview(name = "Keywords — with data")
@Composable
private fun KeywordsContentWithDataPreview() {
    VartovyiTheme {
        KeywordsContent(
            state = KeywordsUiContract.State(
                keywords = listOf("Салтівка", "ракета", "вибух"),
                stopWords = listOf("відбій", "чисто", "минула"),
            ),
            onAction = {},
        )
    }
}

@Preview(name = "Keywords — Telegram filter on")
@Composable
private fun KeywordsContentTelegramFilterPreview() {
    VartovyiTheme {
        KeywordsContent(
            state = KeywordsUiContract.State(
                keywords = listOf("Салтівка", "ракета"),
                stopWords = listOf("відбій"),
                isTelegramChannelFilterEnabled = true,
                telegramChannels = listOf("@air_alert_ua", "@kharkiv_alarm"),
            ),
            onAction = {},
        )
    }
}
