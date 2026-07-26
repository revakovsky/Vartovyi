package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme
import com.revakovskyi.vartovyi.ui.theme.bodyLinkSmall

@Composable
fun KeywordsTelegramReminderDialog(
    onDismiss: () -> Unit,
    onLearnMoreClick: () -> Unit,
) {
    VartovyiDialog(
        title = stringResource(R.string.keywords_telegram_reminder_dialog_title),
        message = stringResource(R.string.keywords_telegram_reminder_dialog_message),
        confirmText = stringResource(R.string.keywords_channels_intro_dialog_confirm),
        content = {
            Text(
                text = stringResource(R.string.keywords_telegram_reminder_dialog_learn_more),
                style = VartovyiTheme.typography.bodyLinkSmall,
                color = VartovyiTheme.colors.primary,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onLearnMoreClick,
                ),
            )
        },
        onDismiss = onDismiss,
    )
}

@Preview
@Composable
private fun KeywordsTelegramReminderDialogPreview() {
    VartovyiTheme {
        KeywordsTelegramReminderDialog(
            onDismiss = {},
            onLearnMoreClick = {},
        )
    }
}
