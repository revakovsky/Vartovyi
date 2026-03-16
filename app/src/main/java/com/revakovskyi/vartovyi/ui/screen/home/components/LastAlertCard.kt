package com.revakovskyi.vartovyi.ui.screen.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.domain.model.AlertEventStatus
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TIME_FORMAT_PATTERN = "HH:mm"
private const val MAX_MESSAGE_LINES = 2
private const val BORDER_STROKE_WIDTH_DP = 1

@Composable
fun LastAlertCard(
    modifier: Modifier = Modifier,
    lastAlertEvent: AlertEvent?,
    onClick: () -> Unit,
) {
    Surface(
        color = VartovyiTheme.colors.surface,
        shape = VartovyiTheme.shapes.large,
        onClick = onClick,
        enabled = lastAlertEvent != null,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VartovyiTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.home_last_trigger),
                style = VartovyiTheme.typography.titleMedium,
                color = VartovyiTheme.colors.onSurface,
            )

            Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

            if (lastAlertEvent == null) {
                Text(
                    text = stringResource(R.string.home_no_triggers),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            } else {
                val timeString = SimpleDateFormat(TIME_FORMAT_PATTERN, Locale.getDefault())
                    .format(Date(lastAlertEvent.timestamp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.extraSmall),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = timeString,
                        style = VartovyiTheme.typography.labelMedium,
                        color = VartovyiTheme.colors.onSurfaceVariant,
                    )

                    HomeKeywordChip(text = lastAlertEvent.senderName)
                }

                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

                Text(
                    text = lastAlertEvent.messageText,
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurface,
                    maxLines = MAX_MESSAGE_LINES,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(VartovyiTheme.spacing.medium))

                Surface(
                    color = VartovyiTheme.colors.surface,
                    shape = VartovyiTheme.shapes.small,
                    border = BorderStroke(
                        width = BORDER_STROKE_WIDTH_DP.dp,
                        color = VartovyiTheme.colors.error,
                    ),
                ) {
                    Text(
                        text = lastAlertEvent.matchedKeyword,
                        style = VartovyiTheme.typography.labelMedium,
                        color = VartovyiTheme.colors.error,
                        modifier = Modifier.padding(
                            horizontal = VartovyiTheme.spacing.small,
                            vertical = VartovyiTheme.spacing.extraSmall,
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLastAlertCard() {
    VartovyiTheme {
        LastAlertCard(
            lastAlertEvent = AlertEvent(
                id = "1",
                timestamp = 1_700_000_000_000L,
                senderPackage = "org.telegram.messenger",
                senderName = "Повітряна тривога",
                messageText = "Повітряна тривога в Київській та Харківській областях. Просимо негайно зайти у найближче укриття.",
                matchedKeyword = "тривога",
                status = AlertEventStatus.ALARM_TRIGGERED,
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewLastAlertCardEmpty() {
    VartovyiTheme {
        LastAlertCard(
            lastAlertEvent = null,
            onClick = {},
        )
    }
}
