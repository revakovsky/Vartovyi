package com.revakovskyi.vartovyi.ui.screen.log

import android.content.ClipData
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.model.AlertEvent
import com.revakovskyi.vartovyi.model.AlertEventStatus
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.log.components.LogClearButton
import com.revakovskyi.vartovyi.ui.screen.log.components.LogEmptyState
import com.revakovskyi.vartovyi.ui.screen.log.components.LogErrorState
import com.revakovskyi.vartovyi.ui.screen.log.components.LogEventsList
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

private const val CHANNEL_NAME_CLIP_LABEL = "channel_name"
private const val MESSAGE_TEXT_CLIP_LABEL = "message_text"
private const val LOG_LIST_STATE_KEY = "log_list_state"

@Composable
fun LogScreen(
    viewModel: LogViewModel = koinViewModel(),
    highlightedLogEntryId: String? = null,
) {
    val clipboardManager = LocalClipboard.current

    val state by viewModel.state.collectAsStateWithLifecycle()
    val logEntries = viewModel.pagedLogEntries.collectAsLazyPagingItems()

    val channelCopiedMessage = stringResource(R.string.log_channel_copied)
    val messageCopiedMessage = stringResource(R.string.log_message_copied)

    LaunchedEffect(highlightedLogEntryId) {
        viewModel.onAction(
            LogUiContract.Action.SyncHighlightLogEntry(logEntryId = highlightedLogEntryId),
        )
    }

    LaunchedEffect(logEntries) {
        snapshotFlow {
            val refresh = logEntries.loadState.refresh
            LogUiContract.Action.SyncLogListPresentation(
                isRefreshLoading = refresh is LoadState.Loading,
                isRefreshError = refresh is LoadState.Error,
                itemCount = logEntries.itemCount,
            )
        }
            .distinctUntilChanged()
            .collect { action -> viewModel.onAction(action) }
    }

    ObserveSingleEvents(flow = viewModel.events) { event ->
        when (event) {
            is LogUiContract.Event.CopyChannelNameRequested -> {
                clipboardManager.setClipEntry(
                    ClipEntry(
                        ClipData.newPlainText(CHANNEL_NAME_CLIP_LABEL, event.channelName),
                    ),
                )

                SnackbarController.sendEvent(
                    SnackbarEvent(message = channelCopiedMessage),
                )
            }

            is LogUiContract.Event.CopyMessageTextRequested -> {
                clipboardManager.setClipEntry(
                    ClipEntry(
                        ClipData.newPlainText(MESSAGE_TEXT_CLIP_LABEL, event.messageText),
                    ),
                )

                SnackbarController.sendEvent(
                    SnackbarEvent(message = messageCopiedMessage),
                )
            }
        }
    }

    LogContent(
        state = state,
        logEntries = logEntries,
        onAction = viewModel::onAction,
    )

    if (state.isClearDialogVisible) {
        VartovyiDialog(
            title = stringResource(R.string.log_clear_dialog_title),
            message = stringResource(R.string.log_clear_dialog_message),
            confirmText = stringResource(R.string.log_clear_dialog_confirm),
            dismissText = stringResource(R.string.log_clear_dialog_dismiss),
            onDismiss = { viewModel.onAction(LogUiContract.Action.DismissClearLogDialog) },
            onConfirm = { viewModel.onAction(LogUiContract.Action.ConfirmClearLog) },
        )
    }

}

@Composable
private fun LogContent(
    modifier: Modifier = Modifier,
    state: LogUiContract.State,
    logEntries: LazyPagingItems<AlertEvent>,
    onAction: (action: LogUiContract.Action) -> Unit,
) {
    val listState = rememberSaveable(saver = LazyListState.Saver, key = LOG_LIST_STATE_KEY) {
        LazyListState()
    }

    var isInitialScrollCompleted by remember(state.highlightLogEntryId) { mutableStateOf(false) }

    val refreshLoadState = logEntries.loadState.refresh

    LaunchedEffect(
        state.highlightedLogEntryIndex,
        logEntries.itemCount,
        refreshLoadState,
        isInitialScrollCompleted,
    ) {
        if (isInitialScrollCompleted) return@LaunchedEffect
        if (state.highlightedLogEntryIndex < 0) return@LaunchedEffect
        if (refreshLoadState !is LoadState.NotLoading) return@LaunchedEffect
        if (logEntries.itemCount <= 0) return@LaunchedEffect

        val safeIndex = state.highlightedLogEntryIndex.coerceAtMost(logEntries.itemCount - 1)
        listState.scrollToItem(index = safeIndex)
        isInitialScrollCompleted = true
    }

    Crossfade(
        label = "logContentCrossfade",
        targetState = state.contentViewState,
        modifier = modifier
            .widthIn(max = VartovyiTheme.spacing.contentMaxWidth)
            .fillMaxSize()
    ) { viewState ->
        when (viewState) {
            LogUiContract.LogContentViewState.Loading -> {
                LoadingOverlay()
            }

            LogUiContract.LogContentViewState.Error -> {
                LogErrorState(
                    onRetry = { logEntries.retry() },
                )
            }

            LogUiContract.LogContentViewState.Empty -> {
                LogEmptyState(modifier = Modifier.fillMaxSize())
            }

            LogUiContract.LogContentViewState.Content -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = VartovyiTheme.spacing.small)
                ) {
                    LogEventsList(
                        listState = listState,
                        logEntries = logEntries,
                        onCopyChannelClick = { channelName ->
                            onAction(LogUiContract.Action.CopyChannelName(channelName))
                        },
                        onCopyMessageClick = { messageText ->
                            onAction(LogUiContract.Action.CopyMessageText(messageText))
                        },
                        modifier = Modifier.weight(1f)
                    )

                    LogClearButton(
                        onClick = { onAction(LogUiContract.Action.OpenClearLogDialog) },
                    )
                }
            }
        }
    }
}

@Preview(name = "Log - empty")
@Composable
private fun LogContentEmptyPreview() {
    val previewLogEntries = flowOf(PagingData.empty<AlertEvent>()).collectAsLazyPagingItems()

    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(
                contentViewState = LogUiContract.LogContentViewState.Empty,
            ),
            logEntries = previewLogEntries,
            onAction = {},
        )
    }
}

@Preview(name = "Log - with entries")
@Composable
private fun LogContentWithEntriesPreview() {
    val previewLogEntries = flowOf(
        PagingData.from(
            listOf(
                AlertEvent(
                    id = "1",
                    timestamp = 1_742_000_000_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Channels",
                    messageText = "Regular update without keywords",
                    matchedKeyword = "",
                    status = AlertEventStatus.SKIPPED,
                ),
                AlertEvent(
                    id = "2",
                    timestamp = 1_742_000_060_000,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Important Channel",
                    messageText = "Air alert in your region",
                    matchedKeyword = "air alert",
                    status = AlertEventStatus.ALARM_TRIGGERED,
                ),
                AlertEvent(
                    id = "3",
                    timestamp = 1_742_000_060_020,
                    senderPackage = "org.telegram.messenger",
                    senderName = "Alert Channel",
                    messageText = "Attention, air alert reported! Attention, air rted! Attention, air alert reported!",
                    matchedKeyword = "air alert",
                    status = AlertEventStatus.SKIPPED_COOLDOWN,
                ),
            )
        )
    ).collectAsLazyPagingItems()

    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(
                contentViewState = LogUiContract.LogContentViewState.Content,
                highlightLogEntryId = "2",
                highlightedLogEntryIndex = 1,
            ),
            logEntries = previewLogEntries,
            onAction = {},
        )
    }
}

@Preview(name = "Log - error")
@Composable
private fun LogContentErrorPreview() {
    val previewLogEntries = flowOf(PagingData.empty<AlertEvent>()).collectAsLazyPagingItems()

    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(
                contentViewState = LogUiContract.LogContentViewState.Error,
            ),
            logEntries = previewLogEntries,
            onAction = {},
        )
    }
}
