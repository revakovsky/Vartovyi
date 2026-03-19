package com.revakovskyi.vartovyi.ui.screen.log

import android.content.ClipData
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.AlertEventStatus
import com.revakovskyi.vartovyi.ui.components.LoadingOverlay
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.log.components.LogClearButton
import com.revakovskyi.vartovyi.ui.screen.log.components.LogEmptyState
import com.revakovskyi.vartovyi.ui.screen.log.components.LogEventsList
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import kotlinx.coroutines.flow.flowOf
import org.koin.androidx.compose.koinViewModel

private const val CHANNEL_NAME_CLIP_LABEL = "channel_name"
private const val MESSAGE_TEXT_CLIP_LABEL = "message_text"

private enum class LogContentViewState {
    LOADING,
    ERROR,
    EMPTY,
    CONTENT,
}

@Composable
fun LogScreen(
    viewModel: LogViewModel = koinViewModel(),
    highlightedLogEntryId: String? = null,
) {
    val clipboardManager = LocalClipboard.current

    val state by viewModel.state.collectAsState()
    val logEntries = viewModel.pagedLogEntries.collectAsLazyPagingItems()

    val channelCopiedMessage = stringResource(R.string.log_channel_copied)
    val messageCopiedMessage = stringResource(R.string.log_message_copied)

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
        highlightedLogEntryId = highlightedLogEntryId,
        resolveHighlightedLogEntryIndex = { eventId ->
            viewModel.getLogEntryIndexById(eventId = eventId)
        },
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
    highlightedLogEntryId: String?,
    resolveHighlightedLogEntryIndex: suspend (eventId: String) -> Int,
    onAction: (action: LogUiContract.Action) -> Unit,
) {
    val listState = rememberLazyListState()

    var isInitialScrollCompleted by remember(highlightedLogEntryId) { mutableStateOf(false) }
    var highlightedLogEntryIndex by remember(highlightedLogEntryId) { mutableStateOf(-1) }

    val refreshLoadState = logEntries.loadState.refresh
    val isInitialLoading = refreshLoadState is LoadState.Loading
    val isRefreshError = refreshLoadState is LoadState.Error
    val isEmptyStateVisible = !isInitialLoading && !isRefreshError && logEntries.itemCount == 0

    val contentViewState = when {
        isInitialLoading -> LogContentViewState.LOADING
        isRefreshError -> LogContentViewState.ERROR
        isEmptyStateVisible -> LogContentViewState.EMPTY
        else -> LogContentViewState.CONTENT
    }

    LaunchedEffect(highlightedLogEntryId) {
        if (highlightedLogEntryId == null) {
            highlightedLogEntryIndex = -1
            return@LaunchedEffect
        }

        highlightedLogEntryIndex = resolveHighlightedLogEntryIndex(highlightedLogEntryId)
    }

    LaunchedEffect(
        highlightedLogEntryIndex,
        logEntries.itemCount,
        refreshLoadState,
        isInitialScrollCompleted,
    ) {
        if (isInitialScrollCompleted) return@LaunchedEffect
        if (highlightedLogEntryIndex < 0) return@LaunchedEffect
        if (refreshLoadState !is LoadState.NotLoading) return@LaunchedEffect
        if (logEntries.itemCount <= 0) return@LaunchedEffect

        val safeIndex = highlightedLogEntryIndex.coerceAtMost(logEntries.itemCount - 1)
        listState.scrollToItem(index = safeIndex)
        isInitialScrollCompleted = true
    }

    Crossfade(
        label = "logContentCrossfade",
        targetState = contentViewState,
        modifier = modifier.fillMaxSize()
    ) { viewState ->
        when (viewState) {
            LogContentViewState.LOADING -> {
                LoadingOverlay()
            }

            LogContentViewState.ERROR -> {
                LogErrorState(
                    onRetry = { logEntries.retry() },
                )
            }

            LogContentViewState.EMPTY -> {
                LogEmptyState(modifier = Modifier.fillMaxSize())
            }

            LogContentViewState.CONTENT -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = VartovyiTheme.spacing.standard)
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
                        modifier = Modifier.weight(1f),
                    )

                    LogClearButton(
                        onClick = { onAction(LogUiContract.Action.OpenClearLogDialog) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LogErrorState(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = VartovyiTheme.spacing.standard),
    ) {
        androidx.compose.material3.Text(
            text = stringResource(R.string.log_load_failed),
            style = VartovyiTheme.typography.bodyMedium,
            color = VartovyiTheme.colors.onSurfaceVariant,
        )

        androidx.compose.material3.Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = VartovyiTheme.spacing.medium),
        ) {
            androidx.compose.material3.Text(
                text = stringResource(R.string.log_retry),
                style = VartovyiTheme.typography.titleMedium,
            )
        }
    }
}

@Preview(name = "Log - empty")
@Composable
private fun LogContentEmptyPreview() {
    val previewLogEntries = flowOf(PagingData.empty<AlertEvent>()).collectAsLazyPagingItems()

    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(),
            logEntries = previewLogEntries,
            highlightedLogEntryId = null,
            resolveHighlightedLogEntryIndex = { _ -> -1 },
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
            state = LogUiContract.State(),
            logEntries = previewLogEntries,
            highlightedLogEntryId = "2",
            resolveHighlightedLogEntryIndex = { _ -> 1 },
            onAction = {},
        )
    }
}
