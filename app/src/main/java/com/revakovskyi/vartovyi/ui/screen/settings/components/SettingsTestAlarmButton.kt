package com.revakovskyi.vartovyi.ui.screen.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.revakovskyi.vartovyi.R
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButton
import com.revakovskyi.vartovyi.ui.components.VartovyiActionButtonStyle
import com.revakovskyi.vartovyi.ui.theme.VartovyiTheme

@Composable
fun SettingsTestAlarmButton(
    modifier: Modifier = Modifier,
    isAlarmRunning: Boolean,
    onClick: () -> Unit,
) {
    if (isAlarmRunning) {
        VartovyiActionButton(
            text = stringResource(R.string.settings_stop_test_alarm),
            onClick = onClick,
            style = VartovyiActionButtonStyle.Filled,
            containerColor = VartovyiTheme.colors.errorContainer,
            contentColor = VartovyiTheme.colors.onErrorContainer,
            icon = ImageVector.vectorResource(R.drawable.alarm),
            modifier = modifier
        )
    } else {
        VartovyiActionButton(
            text = stringResource(R.string.settings_test_alarm),
            onClick = onClick,
            style = VartovyiActionButtonStyle.Outlined,
            contentColor = VartovyiTheme.colors.error,
            borderColor = VartovyiTheme.colors.error,
            icon = ImageVector.vectorResource(R.drawable.alarm),
            iconTint = VartovyiTheme.colors.error,
            modifier = modifier
        )
    }
}

@Preview(name = "Idle")
@Composable
private fun PreviewSettingsTestAlarmButtonIdle() {
    VartovyiTheme {
        SettingsTestAlarmButton(
            isAlarmRunning = false,
            onClick = {},
        )
    }
}

@Preview(name = "Running")
@Composable
private fun PreviewSettingsTestAlarmButtonRunning() {
    VartovyiTheme {
        SettingsTestAlarmButton(
            isAlarmRunning = true,
            onClick = {},
        )
    }
}
