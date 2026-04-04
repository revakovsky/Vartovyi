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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun SettingsVersionFooter(
    modifier: Modifier = Modifier,
    versionName: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.settings_version_number, versionName),
            style = VartovyiTheme.typography.bodySmall,
            color = VartovyiTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(VartovyiTheme.spacing.extraSmall))

        Text(
            text = stringResource(R.string.settings_produced_by),
            style = VartovyiTheme.typography.bodySmall,
            color = VartovyiTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun SettingsVersionFooterPreview() {
    VartovyiTheme {
        SettingsVersionFooter(versionName = "1.0")
    }
}
