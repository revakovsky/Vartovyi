package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun SettingsResetFactoryDefaultsButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    VartovyiActionButton(
        text = stringResource(R.string.settings_reset_factory_button),
        onClick = onClick,
        style = VartovyiActionButtonStyle.Outlined,
        contentColor = VartovyiTheme.colors.error,
        borderColor = VartovyiTheme.colors.error,
        modifier = modifier
    )
}

@Preview
@Composable
private fun SettingsResetFactoryDefaultsButtonPreview() {
    VartovyiTheme {
        SettingsResetFactoryDefaultsButton(onClick = {})
    }
}
