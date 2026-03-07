package com.revakovskyi.vartovyi.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.domain.model.AlertEvent
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogItem(
    modifier: Modifier = Modifier,
    event: AlertEvent,
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

    Column(
        modifier = modifier.padding(VartovyiTheme.spacing.standard)
    ) {
        Text(
            text = dateFormat.format(Date(event.timestamp)),
            style = VartovyiTheme.typography.labelSmall,
            color = VartovyiTheme.colors.onSurfaceVariant,
        )

        Text(
            text = event.senderName,
            style = VartovyiTheme.typography.labelLarge,
            color = VartovyiTheme.colors.onSurface,
        )

        Text(
            text = event.messageText,
            style = VartovyiTheme.typography.bodySmall,
            color = VartovyiTheme.colors.onSurface,
        )

        Text(
            text = "keyword: ${event.matchedKeyword}",
            style = VartovyiTheme.typography.labelSmall,
            color = VartovyiTheme.colors.error,
        )
    }
}

@Preview
@Composable
private fun LogItemPreview() {
    VartovyiTheme {
        LogItem(
            event = AlertEvent(
                id = "1",
                timestamp = System.currentTimeMillis(),
                senderPackage = "org.telegram.messenger",
                senderName = "Канал Новини",
                messageText = "Увага! Ракетна небезпека у Харківській обл.",
                matchedKeyword = "ракетна",
            ),
        )
    }
}
