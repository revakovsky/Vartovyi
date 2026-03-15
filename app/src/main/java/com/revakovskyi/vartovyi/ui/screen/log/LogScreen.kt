package com.revakovskyi.vartovyi.ui.screen.log

import android.content.ClipData
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.screen.log.components.LogClearButton
import com.revakovskyi.vartovyi.ui.screen.log.components.LogEmptyState
import com.revakovskyi.vartovyi.ui.screen.log.components.LogEventsList
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarController
import com.revakovskyi.vartovyi.ui.util.snackbar.SnackbarEvent
import com.revakovskyi.vartovyi.utils.ObserveSingleEvents
import org.koin.androidx.compose.koinViewModel

private const val CHANNEL_NAME_CLIP_LABEL = "channel_name"
private const val MESSAGE_TEXT_CLIP_LABEL = "message_text"

@Composable
fun LogScreen(
    viewModel: LogViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    val clipboardManager = LocalClipboard.current

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
    onAction: (action: LogUiContract.Action) -> Unit,
) {
    Crossfade(
        label = "logContentCrossfade",
        targetState = state.logEntries.isEmpty(),
        modifier = modifier.fillMaxSize()
    ) { isEmptyStateVisible ->
        if (isEmptyStateVisible) {
            LogEmptyState(modifier = Modifier.fillMaxSize())
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = VartovyiTheme.spacing.standard)
            ) {
                LogEventsList(
                    logEntries = state.logEntries,
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

@Preview(name = "Log - empty")
@Composable
private fun LogContentEmptyPreview() {
    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(),
            onAction = {},
        )
    }
}

@Preview(name = "Log - with entries")
@Composable
private fun LogContentWithEntriesPreview() {
    VartovyiTheme {
        LogContent(
            state = LogUiContract.State(
                logEntries = listOf(
                    AlertEvent(
                        id = "1",
                        timestamp = 1_742_000_000_000,
                        senderPackage = "org.telegram.messenger",
                        senderName = "Channels",
                        messageText = "Regular update without keywords",
                        matchedKeyword = "",
                    ),
                    AlertEvent(
                        id = "2",
                        timestamp = 1_742_000_060_000,
                        senderPackage = "org.telegram.messenger",
                        senderName = "Important Channel",
                        messageText = "Air alert in your region",
                        matchedKeyword = "air alert",
                    ),
                ),
            ),
            onAction = {},
        )
    }
}
