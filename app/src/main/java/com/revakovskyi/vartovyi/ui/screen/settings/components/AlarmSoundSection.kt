package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun AlarmSoundSection(
    modifier: Modifier = Modifier,
    selectedSoundTitle: String,
    onChooseSoundClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_alarm_sound_title),
            style = VartovyiTheme.typography.titleMedium,
            color = VartovyiTheme.colors.onBackground,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.small))

        Text(
            text = selectedSoundTitle,
            style = VartovyiTheme.typography.bodyLarge,
            color = VartovyiTheme.colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.standard))

        VartovyiActionButton(
            text = stringResource(R.string.settings_alarm_sound_choose_button),
            onClick = onChooseSoundClick,
            style = VartovyiActionButtonStyle.Outlined,
            borderColor = VartovyiTheme.colors.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
private fun AlarmSoundSectionPreview() {
    VartovyiTheme {
        AlarmSoundSection(
            selectedSoundTitle = "Default alarm sound",
            onChooseSoundClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}
