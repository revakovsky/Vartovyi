package com.revakovskyi.vartovyi.ui.screen.keywords.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiDialog
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun KeywordsChannelsIntroDialog(
    onDismiss: () -> Unit,
    onHideForever: () -> Unit,
) {
    var isDontShowAgainChecked by rememberSaveable { mutableStateOf(false) }

    VartovyiDialog(
        title = stringResource(R.string.keywords_channels_intro_dialog_title),
        message = stringResource(R.string.keywords_channels_intro_dialog_message),
        confirmText = stringResource(R.string.keywords_channels_intro_dialog_confirm),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VartovyiTheme.spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { isDontShowAgainChecked = !isDontShowAgainChecked },
                    ),
            ) {
                Checkbox(
                    checked = isDontShowAgainChecked,
                    onCheckedChange = { isChecked -> isDontShowAgainChecked = isChecked },
                    colors = CheckboxDefaults.colors(checkedColor = VartovyiTheme.colors.primary),
                )

                Text(
                    text = stringResource(R.string.keywords_channels_intro_dialog_dont_show_again),
                    style = VartovyiTheme.typography.bodyMedium,
                    color = VartovyiTheme.colors.onSurfaceVariant,
                )
            }
        },
        onDismiss = { if (isDontShowAgainChecked) onHideForever() else onDismiss() },
    )
}

@Preview
@Composable
private fun KeywordsChannelsIntroDialogPreview() {
    VartovyiTheme {
        KeywordsChannelsIntroDialog(
            onDismiss = {},
            onHideForever = {},
        )
    }
}
