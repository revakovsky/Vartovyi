package com.revakovskyi.vartovyi.ui.screen.log.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TIME_FORMAT_PATTERN = "HH:mm:ss"
private const val MESSAGE_MAX_LINES = 3
private val COPY_BUTTON_SIZE = 32.dp
private val COPY_ICON_SIZE = 20.dp

@Composable
fun LogEventItemCard(
    modifier: Modifier = Modifier,
    event: AlertEvent,
    onCopyChannelClick: (channelName: String) -> Unit,
    onCopyMessageClick: (messageText: String) -> Unit,
) {
    val isAlarm = event.matchedKeyword.isNotBlank()

    val formattedTime = remember(event.timestamp) {
        formatLogEventTime(timestamp = event.timestamp)
    }

    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(VartovyiTheme.spacing.standard),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.standard),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = formattedTime,
                        style = VartovyiTheme.typography.labelMedium,
                        color = VartovyiTheme.colors.onSurfaceVariant,
                    )

                    Text(
                        text = event.senderName,
                        style = VartovyiTheme.typography.bodySmall,
                        color = VartovyiTheme.colors.onSurface,
                    )
                }

                IconButton(
                    onClick = { onCopyChannelClick(event.senderName) },
                    modifier = Modifier.size(COPY_BUTTON_SIZE)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.copy),
                        contentDescription = stringResource(R.string.log_copy),
                        tint = VartovyiTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(COPY_ICON_SIZE)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = event.messageText,
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurface,
                    maxLines = MESSAGE_MAX_LINES,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    onClick = { onCopyMessageClick(event.messageText) },
                    modifier = Modifier.size(COPY_BUTTON_SIZE)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.copy),
                        contentDescription = stringResource(R.string.log_copy),
                        tint = VartovyiTheme.colors.onSurfaceVariant,
                        modifier = Modifier.size(COPY_ICON_SIZE)
                    )
                }
            }

            if (isAlarm) {
                Surface(
                    color = VartovyiTheme.colors.errorContainer,
                    shape = VartovyiTheme.shapes.small,
                    border = BorderStroke(1.dp, VartovyiTheme.colors.error),
                    modifier = Modifier.padding(top = VartovyiTheme.spacing.extraSmall)
                ) {
                    Text(
                        text = "${stringResource(R.string.log_alarm)}: ${event.matchedKeyword}",
                        style = VartovyiTheme.typography.labelMedium,
                        color = VartovyiTheme.colors.error,
                        modifier = Modifier.padding(
                            horizontal = VartovyiTheme.spacing.small,
                            vertical = VartovyiTheme.spacing.extraSmall,
                        ),
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.log_skipped),
                    style = VartovyiTheme.typography.bodySmall,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            }
        }
    }
}

private fun formatLogEventTime(timestamp: Long): String {
    return SimpleDateFormat(TIME_FORMAT_PATTERN, Locale.getDefault()).format(Date(timestamp))
}

@Preview(name = "Log item - skipped")
@Composable
private fun LogEventItemCardSkippedPreview() {
    VartovyiTheme {
        LogEventItemCard(
            event = AlertEvent(
                id = "1",
                timestamp = 1_742_000_000_000,
                senderPackage = "org.telegram.messenger",
                senderName = "City News",
                messageText = "Daily update without matching keyword",
                matchedKeyword = "",
            ),
            onCopyChannelClick = {},
            onCopyMessageClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(name = "Log item - alarm")
@Composable
private fun LogEventItemCardAlarmPreview() {
    VartovyiTheme {
        LogEventItemCard(
            event = AlertEvent(
                id = "2",
                timestamp = 1_742_000_060_000,
                senderPackage = "org.telegram.messenger",
                senderName = "Alert Channel",
                messageText = "Attention, air alert reported",
                matchedKeyword = "air alert",
            ),
            onCopyChannelClick = {},
            onCopyMessageClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
